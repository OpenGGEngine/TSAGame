package com.opengg.dialogue;

import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Tuple;

import java.util.*;
import java.util.regex.Pattern;

public class DialogueManager implements KeyboardListener {
    private static Map<String, DialogueNode> nodes = new HashMap<>();
    private static DialogueSequence current;

    private final static Pattern commandPattern = Pattern.compile("\\{(.*?)}");
    private final static Pattern nodePattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL|Pattern.MULTILINE);

    public static void initialize(){

        KeyboardController.addKeyboardListener(new DialogueManager());
        loadNodes("dialog.txt");
    }

    public static void loadNodes(String path){
        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/" + path));

            var pattern = nodePattern.matcher(data);
            while (pattern.find()){
                var node = pattern.group(1);
                data = pattern.replaceFirst("");
                pattern = nodePattern.matcher(data);
                parseSingleNode(node);
            }
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

        if(commands.containsKey("sound")){
            newNode.sound = Resource.getSoundData(commands.get("sound").get(0));
            if(commands.get("sound").size() > 1)
                newNode.volume = Float.parseFloat(commands.get("sound").get(1));
        }

        newNode.itemSpawn = commands.getOrDefault("item", List.of("")).get(0);
        newNode.itemAmount = Integer.parseInt(commands.getOrDefault("item", List.of("","0")).get(1));

        newNode.anim = commands.getOrDefault("anim", List.of("idle")).get(0);

        var name = commands.get("name");

        if(name == null) throw new RuntimeException("Failed to parse dialogue node, no name");
        nodes.put(name.get(0), newNode);
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
        if(current.currentGUI == null) current.start();
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
