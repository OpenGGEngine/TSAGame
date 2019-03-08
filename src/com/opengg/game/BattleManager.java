package com.opengg.game;

import com.opengg.components.WorldAI;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;

public class BattleManager {
    private static Battle current;
    private static World last;

    public static void initialize(){

    }

    public static void runBattle(BattleInfo info){
        WorldLoader.keepWorld(last = WorldEngine.getCurrent());
        WorldEngine.useWorld(WorldLoader.loadWorld(info.battleWorld));

        current = new Battle(info);

        current.start();
    }

    public static void end(){
        WorldEngine.useWorld(last);
        WorldEngine.getCurrent().getAllDescendants().stream()
                .filter(s -> s instanceof WorldAI)
                .map(s -> (WorldAI)s)
                .forEach(WorldAI::checkAlive);
    }

    public static void update() {
        if(current != null) current.update();
    }
}
