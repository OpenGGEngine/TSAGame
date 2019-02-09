package com.opengg.game;

import com.opengg.components.SpriteRenderComponent;
import com.opengg.core.Configuration;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIController;
import com.opengg.core.gui.GUIGroup;
import com.opengg.core.gui.GUITexture;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.WorldEngine;

import java.awt.*;
import java.util.HashMap;

public class Battle {
    BattleInfo info;

    GUI battleGUI = new GUI();
    GUIGroup battleMenu;
    GUIGroup itemMenu;
    GUIGroup weaponMenu;
    GUIGroup abilities;

    HashMap<String, SpriteRenderComponent> fighterRenderers = new HashMap<>();

    public Battle(BattleInfo info){
        this.info = info;
    }

    public void start(){
        var playerRenderer = new SpriteRenderComponent(Configuration.get("playerSprite"));
        WorldEngine.getCurrent().attach(playerRenderer.setPositionOffset(new Vector3f(-5, 1, -6)));
        fighterRenderers.put("player", playerRenderer);

        for(var ally : info.allies){
            var renderer = new SpriteRenderComponent(CharacterManager.getExisting(ally).getSprite());
            fighterRenderers.put(ally, renderer);
        }

        for(var enemy : info.enemies){
            var renderer = new SpriteRenderComponent(CharacterManager.getExisting(enemy).getSprite());
            WorldEngine.getCurrent().attach(renderer.setPositionOffset(new Vector3f(5, 1, -6)));
            fighterRenderers.put(enemy, renderer);
        }

        GUIController.addAndUse(battleGUI, "battle");
        battleGUI.addItem("menu", battleMenu = new GUIGroup(new Vector2f(0.05f, 0.45f)));
        battleMenu.addItem("background", new GUITexture(Texture.ofColor(Color.GRAY, 0.5f), new Vector2f(0,0), new Vector2f(0.3f, 0.5f)));
    }

    public void update(){

    }


}
