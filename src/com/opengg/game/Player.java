package com.opengg.game;

import com.opengg.core.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player extends Character {
    public static Player PLAYER;

    List<String> partners = new ArrayList<>();

    public Player(){
        super("player", new Inventory(Map.of("gold", 100)), 100, "Player", Configuration.get("playerSprite"), false, "player0", 1);
    }

    public int getMoney(){
        return getInventory().getItemCount("gold");
    }
}
