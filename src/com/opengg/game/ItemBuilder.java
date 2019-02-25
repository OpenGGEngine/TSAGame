package com.opengg.game;

import com.opengg.core.math.Tuple;

import java.util.List;

public class ItemBuilder {
    private String name;
    private String displayName;
    private String desc;
    private boolean usable;
    private boolean unique;
    private boolean targeted;
    private List<ItemEffect> effects;
    private Item.ItemType type;
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

    public ItemBuilder setTargeted(boolean targeted) {
        this.targeted = targeted;
        return this;
    }

    public ItemBuilder setUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public boolean isUnique(){
        return unique;
    }

    public ItemBuilder setEffects(List<ItemEffect> effects) {
        this.effects = effects;
        return this;
    }

    public ItemBuilder setSprite(String sprite) {
        this.sprite = sprite;
        return this;
    }

    public Item createItem() {
        return new Item(name, displayName, desc, usable, unique, targeted, type, effects, sprite);
    }

    public Item.ItemType setType(Item.ItemType type) {
        this.type = type;
        return type;
    }

    @Override
    public String toString() {
        return "ItemBuilder{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", desc='" + desc + '\'' +
                ", usable=" + usable +
                ", unique=" + unique +
                ", effects=" + effects +
                ", type=" + type +
                ", sprite='" + sprite + '\'' +
                '}';
    }
}