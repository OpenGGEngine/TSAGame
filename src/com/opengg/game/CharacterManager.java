package com.opengg.game;

import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Tuple;
import com.opengg.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CharacterManager {
    private static HashMap<String, CharacterDataBuilder> builders = new HashMap<>();
    private static HashMap<String, Character> characters = new HashMap<>();
    private static HashMap<String, Integer> characterSpawnCount = new HashMap<>();

    private final static Pattern nodePattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL|Pattern.MULTILINE);
    private final static Pattern subPattern = Pattern.compile("\\{(.*?)}", Pattern.DOTALL|Pattern.MULTILINE);

    public static void initialize(){
        characters.put("player0", Player.PLAYER = new Player());

        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/characters.txt"));

            StringUtil.splitByPattern(data, nodePattern).forEach(CharacterManager::processCharacter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processCharacter(String data){

        List<Tuple<String, Integer>> items = StringUtil.splitByPattern(data, subPattern).stream()
                .map(CharacterManager::processInventoryItem)
                .collect(Collectors.toList());

        data = subPattern.matcher(data).replaceAll("");
        CharacterDataBuilder builder = new CharacterDataBuilder();

        var sections = StringUtil.splitLines(data);

        builder.setName(sections.get("name"));
        builder.setDisplayName(sections.get("displayName"));
        builder.setSprite(sections.get("sprite"));
        builder.setSize(Float.parseFloat(sections.get("size")));
        builder.setHostile(Boolean.parseBoolean(sections.getOrDefault("hostile", "false")));
        builder.setMaxHealth(
                Integer.parseInt(
                        sections.getOrDefault("health", "100")));
        builder.setUnique(Boolean.parseBoolean(sections.getOrDefault("unique", "false")));

        Inventory inventory = new Inventory();

        for(var item : items){
            if(ItemManager.generate(item.x).type == Item.ItemType.ITEM) inventory.addItem(item.x, item.y);
            else inventory.addAbility(item.x);
        }

        builder.setInventory(inventory);
        builders.put(builder.getName(), builder);
    }

    public static Tuple<String, Integer> processInventoryItem(String data){
        var sections = StringUtil.splitLines(data);

        var item = sections.get("item");
        var amount = Integer.parseInt(sections.getOrDefault(item, "-1"));

        return Tuple.of(item, amount);
    }

    public static Character getExisting(String name){
        return characters.get(name);
    }

    public static String generate(String name){
        if(!builders.containsKey(name)) throw new RuntimeException("Failed to find character " + name);

        var builder = builders.get(name);
        int amount = characterSpawnCount.getOrDefault(name, 0);

        if(amount == 1 && builder.isUnique()) throw new RuntimeException("Tried to create " + name + " but " + name + " is unique and already exists");

        var newID = name + amount;

        var newCharacter = builder.createCharacterData(newID);
        characterSpawnCount.put(name, amount + 1);

        characters.put(newID, newCharacter);
        return newID;
    }
}
