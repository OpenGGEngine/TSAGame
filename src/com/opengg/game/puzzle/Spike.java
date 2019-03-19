package com.opengg.game.puzzle;

import com.opengg.TSAGame;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.PlayerComponent;
import com.opengg.core.world.components.Zone;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.game.Player;

public class Spike extends Zone {
    public Spike(Vector3f bounds){
        setBox(new AABB(bounds));
    }
    public void onTrigger(TriggerInfo trigger){
        if(trigger.data instanceof PlayerComponent){
            Player.PLAYER.setHealth(Player.PLAYER.getHealth()-10);
            ((PlayerComponent) trigger.data).setPositionOffset(TSAGame.lastLoad.getPosition());
        }
    }
}
