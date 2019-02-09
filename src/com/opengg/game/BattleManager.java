package com.opengg.game;

import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;

public class BattleManager {
    private static Battle current;

    public static void initialize(){

    }

    public static void runBattle(BattleInfo info){
        WorldLoader.keepWorld(WorldEngine.getCurrent());
        WorldEngine.useWorld(WorldLoader.loadWorld(info.battleWorld));

        current = new Battle(info);

        current.start();
    }

    public static void update() {
        if(current != null) current.update();
    }
}
