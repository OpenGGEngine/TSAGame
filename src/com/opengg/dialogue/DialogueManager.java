package com.opengg.dialogue;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Tuple;
import com.opengg.core.util.ArrayUtil;
import com.opengg.util.StringUtil;

import java.util.*;
import java.util.regex.Pattern;

public class DialogueManager implements KeyboardListener {
    private static Map<String, DialogueNode> nodes = new HashMap<>();
    private static DialogueSequence current;

    private final static Pattern commandPattern = Pattern.compile("\\{(.*?)}", Pattern.DOTALL|Pattern.MULTILINE);
    private final static Pattern nodePattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL|Pattern.MULTILINE);

    public static void initialize(){
        KeyboardController.addKeyboardListener(new DialogueManager());
        loadNodes("dialog.txt");
        GGConsole.log("Loaded " + nodes.size() + " dialogue nodes");
    }

    public static void loadNodes(String path){
        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/" + path));

            StringUtil.splitByPattern(data, nodePattern).forEach(DialogueManager::parseSingleNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseSingleNode(String data){
        var section = data;
        var matcher = commandPattern.matcher(section);

        var commands = new HashMap<String, List<String>>();
        while(matcher.find()){
            var text = matcher.group(1);
            var parts = text.split(";");
            var args = List.of(Arrays.copyOfRange(parts, 1, parts.length));
            commands.put(parts[0], args);
            section = matcher.replaceFirst("");
            matcher = commandPattern.matcher(section);
        }

        var newNode = new DialogueNode();
        newNode.hasOpts = commands.containsKey("optionCount");

        for(int i = 0; i < Integer.parseInt(commands.getOrDefault("optionCount", List.of("0")).get(0).trim()); i++){
            newNode.options.add(Tuple.of(commands.get("option" + i).get(1), commands.get("option" + i).get(0)));
        }

        newNode.text = section.trim();
        newNode.next = commands.getOrDefault("next", List.of("")).get(0);

        newNode.setNext = commands.getOrDefault("setNext", List.of("")).get(0);


        if(commands.containsKey("next") && commands.get("next").size() > 1){
            newNode.failOpt = commands.get("next").get(1);
            newNode.requirementQuest = commands.get("next").get(2);
            newNode.requirementQuestState = commands.get("next").get(3);
        }

        if(commands.containsKey("sound")){
            newNode.sound = Resource.getSoundData(commands.get("sound").get(0));
            if(commands.get("sound").size() > 1)
                newNode.volume = Float.parseFloat(commands.get("sound").get(1));
        }

        newNode.itemSpawn = commands.getOrDefault("item", List.of("")).get(0);
        newNode.itemAmount = Integer.parseInt(commands.getOrDefault("item", List.of("","0")).get(1));

        newNode.anim = commands.getOrDefault("anim", List.of("idle")).get(0);

        if(commands.containsKey("quest")){
            newNode.quest = commands.get("quest").get(0);
            if(commands.get("quest").size() == 2){
                newNode.questState = commands.get("quest").get(1);
            }
        }


        var name = commands.get("name").get(0);

        //System.out.println(name);
        if(name == null) throw new RuntimeException("Failed to parse dialogue node, no name");
        nodes.put(name, newNode);
    }

    public static DialogueNode getNodeByName(String name){
        return nodes.get(name);
    }

    public static void update(float delta){
        if(current != null){
            current.update(delta);
        }
    }

    public static void setCurrent(DialogueSequence current) {
        DialogueManager.current = current;
        if(current == null) return;
    }

    @Override
    public void keyPressed(int key) {
        if(current != null) current.keyPressed(key);
    }

    @Override
    public void keyReleased(int key) {
        if(current != null) current.keyReleased(key);
    }
}
