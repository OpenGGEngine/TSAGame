package com.opengg.game;

public class CharacterDataBuilder {
    private String name;
    private Inventory inventory = new Inventory();
    private int maxHealth;
    private String displayName;
    private boolean hostile;
    private String sprite;

    private boolean unique;

    public CharacterDataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public CharacterDataBuilder setInventory(Inventory inventory) {
        this.inventory = inventory;
        return this;
    }

    public CharacterDataBuilder setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public CharacterDataBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public CharacterDataBuilder setSprite(String sprite) {
        this.sprite = sprite;
        return this;
    }

    public CharacterDataBuilder setHostile(boolean hostile) {
        this.hostile = hostile;
        return this;
    }

    public CharacterDataBuilder setUnique(boolean unique){
        this.unique = unique;
        return this;
    }

    public boolean isUnique() {
        return unique;
    }

    public Character createCharacterData(String id) {
        return new Character(name, inventory, maxHealth, displayName, sprite, hostile, id);
    }

    @Override
    public String toString() {
        return "CharacterDataBuilder{" +
                "name='" + name + '\'' +
                ", inventory=" + inventory +
                ", maxHealth=" + maxHealth +
                ", displayName='" + displayName + '\'' +
                ", hostile=" + hostile +
                ", sprite='" + sprite + '\'' +
                ", unique=" + unique +
                '}';
    }
}
