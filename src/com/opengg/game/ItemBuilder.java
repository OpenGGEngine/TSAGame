package com.opengg.game;

public class ItemBuilder {
    private String name;
    private String displayName;
    private String desc;
    private boolean usable;
    private boolean unique;
    private int damage;
    private String sprite;

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemBuilder setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public ItemBuilder setUsable(boolean usable) {
        this.usable = usable;
        return this;
    }

    public ItemBuilder setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public boolean isUnique(){
        return unique;
    }

    public ItemBuilder setDamage(int damage) {
        this.damage = damage;
        return this;
    }

    public ItemBuilder setSprite(String sprite) {
        this.sprite = sprite;
        return this;
    }

    public Item createItem() {
        return new Item(name, displayName, desc, usable, unique, damage, sprite);
    }
}