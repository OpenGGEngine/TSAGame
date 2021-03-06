package com.opengg.game;

import com.opengg.core.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Player extends Character {
    public static Player PLAYER;
    public int level=1;
    public int exp = 5;
    public int expNextLevel = 10;

    List<String> partners = new ArrayList<>();

    public Player(){
        super("player", new Inventory(Map.of("gold", 100,"honey",2,"stingerSpear",1)), 80, "Player", Configuration.get("playerSprite"), false, "player0", 2);
    }

    public int getMoney(){
        return getInventory().getItemCount("gold");
    }
}
