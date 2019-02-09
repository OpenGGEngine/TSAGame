package com.opengg.components;

import com.opengg.core.world.WorldEngine;
import com.opengg.game.BattleInfo;
import com.opengg.game.BattleManager;
import com.opengg.game.CharacterManager;

import java.util.List;

public class WorldEnemy extends WorldAI {
    public WorldEnemy(){
        super(CharacterManager.generate("bobomb"));
        args = List.of("0,0", "10,0");
        behavior = "patrol";
    }

    @Override
    public void update(float delta){
        super.update(delta);
        if(this.getPosition().distanceTo(WorldEngine.getCurrent().find("player").getPosition()) < 0.5f){
            BattleInfo info = new BattleInfo();
            info.enemies.add(this.character);

            BattleManager.runBattle(info);
        }
    }
}
