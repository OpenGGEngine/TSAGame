package com.opengg.game;

import com.opengg.TSAGame;
import com.opengg.components.WorldAI;
import com.opengg.core.animation.Animation;
import com.opengg.core.animation.AnimationManager;
import com.opengg.core.gui.GUIController;
import com.opengg.core.gui.GUITexture;
import com.opengg.core.math.FastMath;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;

import java.awt.*;

public class BattleManager {
    private static Battle current;
    private static World last;

    public static void initialize(){

    }

    public static void runBattle(BattleInfo info){
        Animation animation = new Animation(3.5, false);
        WorldEngine.getCurrent().setEnabled(false);
        animation.addStaticEvent(Animation.AnimationStage.createStaticStage(0,1.5,
                (d) -> Texture.ofColor(Color.WHITE, (FastMath.sin((float) (FastMath.degreesToRadians * ((d*720)-180)))+1)/2),
                t -> ((GUITexture) GUIController.get("black").getRoot().getItem("tex")).setTexture(t)));

        animation.addStaticEvent(Animation.AnimationStage.createStaticStage(1.5,2,
                (d) -> Texture.ofColor(Color.WHITE, (float) (d * 2)),
                t -> ((GUITexture)GUIController.get("black").getRoot().getItem("tex")).setTexture(t))
                .setUseLocalTimeReference(true)
                .setOnComplete(() -> {
                    WorldLoader.keepWorld(last = WorldEngine.getCurrent());
                    WorldEngine.useWorld(WorldLoader.loadWorld(info.battleWorld));

                    current = new Battle(info);
                    current.start();
                }));

        animation.addStaticEvent(Animation.AnimationStage.createStaticStage(2,2.5,
                (d) -> Texture.ofColor(Color.WHITE, (float) ((float) 1-(d*2))),
                t -> ((GUITexture)GUIController.get("black").getRoot().getItem("tex")).setTexture(t))
                .setUseLocalTimeReference(true));

        animation.start();
        AnimationManager.register(animation);
    }

    public static void end(boolean success){
        GUIController.deactivateGUI("battle");
        WorldEngine.useWorld(last);
        WorldEngine.getCurrent().setEnabled(true);
        WorldEngine.getCurrent().getAllDescendants().stream()
                .filter(s -> s instanceof WorldAI)
                .map(s -> (WorldAI)s)
                .forEach(WorldAI::checkAlive);

        if(!success)
            WorldEngine.getCurrent().find("player").setPositionOffset(TSAGame.lastLoad.getPosition());
    }

    public static void update() {
        if(current != null) current.update();
    }
}
