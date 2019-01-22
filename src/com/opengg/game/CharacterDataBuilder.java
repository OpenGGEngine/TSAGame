package com.opengg.game;

import java.util.ArrayList;
import java.util.List;

public class CharacterDataBuilder {
    private String name;
    private List<Attack> attacks = new ArrayList<>();
    private int maxHealth;
    private String displayName;
    private boolean hostile;

    private boolean unique;

    public CharacterDataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CharacterDataBuilder setAttacks(List<Attack> attacks) {
        this.attacks = attacks;
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

    public CharacterData createCharacterData() {
        return new CharacterData(name, attacks, maxHealth, displayName, hostile);
    }
}
