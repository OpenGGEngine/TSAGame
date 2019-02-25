package com.opengg.game;

import com.opengg.core.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Player extends Character {
    public static Player PLAYER;

    List<String> partners = new ArrayList<>();

    public Player(){
        super("player", new Inventory(), 100, "Player", Configuration.get("playerSprite"), false, "player0");
    }
}
