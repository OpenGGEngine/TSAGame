package com.opengg.components;

import java.util.List;

public class WorldEnemy extends WorldAI {
    float range = 20;

    public WorldEnemy(){
        args = List.of("0,0", "10,0");
        behavior = "patrol";
    }
}
