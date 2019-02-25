package com.opengg.game;

import java.util.HashSet;

public class BattleInfo {
    public HashSet<String> allies = new HashSet<>();
    public HashSet<String> enemies = new HashSet<>();

    public String battleWorld = "battle.bwf";
    public String battleEnvironment;
}
