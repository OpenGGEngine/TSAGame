package com.opengg.game;

public class Item {
    public String name;
    public String displayName;
    public String desc;

    public boolean usable;
    public boolean unique;

    public int damage;
     
    public String sprite;

    public Item(String name, String displayName, String desc, boolean usable, boolean unique, int damage, String sprite) {
        this.name = name;
        this.displayName = displayName;
        this.desc = desc;
        this.usable = usable;
        this.unique = unique;
        this.damage = damage;
        this.sprite = sprite;
    }
}
