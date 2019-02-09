package com.opengg.game;

import java.util.HashMap;

public class CharacterManager {
    private static HashMap<String, CharacterDataBuilder> builders = new HashMap<>();
    private static HashMap<String, CharacterData> characters = new HashMap<>();
    private static HashMap<String, Integer> characterSpawnCount = new HashMap<>();

    public static void initialize(){
        builders.put("bobomb", new CharacterDataBuilder()
                                .setDisplayName("Bob-omb")
                                .setMaxHealth(100)
                                .setHostile(false)
                                .setName("bobomb")
                                .setSprite("test")
                                .setUnique(true));
    }

    public static CharacterData getExisting(String name){
        return characters.get(name);
    }

    public static String generate(String name){
        if(!builders.containsKey(name)) throw new RuntimeException("Failed to find character " + name);

        var builder = builders.get(name);
        int amount = characterSpawnCount.getOrDefault(name, 0);

        if(amount == 1 && builder.isUnique()) throw new RuntimeException("Tried to create " + name + " but " + name + " is unique and already exists");

        var newCharacter = builder.createCharacterData();
        characterSpawnCount.replace(name, amount + 1);

        var newID = name + amount;
        characters.put(newID, newCharacter);
        return newID;
    }
}
