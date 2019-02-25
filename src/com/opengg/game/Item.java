package com.opengg.game;

import java.util.List;

public class Item {
    public String name;
    public String displayName;
    public String desc;

    public boolean usable;
    public boolean unique;
    public boolean targeted;

    public ItemType type;
    public List<ItemEffect> effects;

    public String sprite;

    public Item(String name, String displayName, String desc, boolean usable, boolean unique, boolean targeted, ItemType type, List<ItemEffect> effects, String sprite) {
        this.name = name;
        this.displayName = displayName;
        this.desc = desc;
        this.usable = usable;
        this.unique = unique;
        this.targeted = targeted;
        this.type = type;
        this.effects = effects;
        this.sprite = sprite;
    }

    public enum ItemType {
        ITEM, ABILITY
    }
}
