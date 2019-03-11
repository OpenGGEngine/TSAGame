package com.opengg.game;

import java.util.List;

public class Character {
    private String id;

    private String name;

    private Inventory inventory;

    private float health;
    private int maxHealth;

    private String displayName;

    private String sprite;

    private boolean hostile;

    private float size;

    private boolean living = true;

    public Character(String name, Inventory inventory, int maxHealth, String displayName, String sprite, boolean hostile, String id, float size) {
        this.name = name;
        this.inventory = inventory;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.displayName = displayName;
        this.hostile = hostile;
        this.sprite = sprite;
        this.id = id;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSprite() {
        return sprite;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    public float getSize() {
        return size;
    }

    public boolean isLiving() {
        return living;
    }

    public String getId() {
        return id;
    }

    public void setLiving(boolean living) {
        this.living = living;
    }
}
