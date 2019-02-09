package com.opengg.game;

import java.util.List;

public class CharacterData {
    private String name;

    private List<Attack> attacks;

    private float health;
    private int maxHealth;

    private String displayName;

    private String sprite;

    private boolean hostile;

    public CharacterData(String name, List<Attack> attacks, int maxHealth, String displayName, String sprite, boolean hostile) {
        this.name = name;
        this.attacks = attacks;
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        this.displayName = displayName;
        this.hostile = hostile;
        this.sprite = sprite;
    }

    public String getName() {
        return name;
    }

    public List<Attack> getAttacks() {
        return attacks;
    }

    public void setAttacks(List<Attack> attacks) {
        this.attacks = attacks;
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
}
