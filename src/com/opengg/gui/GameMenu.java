package com.opengg.gui;

import com.opengg.core.engine.Resource;
import com.opengg.core.gui.*;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.WorldEngine;
import com.opengg.game.ItemManager;
import com.opengg.game.Player;
import com.opengg.game.Quest;
import com.opengg.game.QuestManager;

import java.awt.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class GameMenu {
    static HashMap<String, Texture> itemCache = new HashMap<>();

    static GUI mainMenu;
    static GUIGroup menuGroup;
    static GUIGroup inventoryMenu;
    static GUIGroup questMenu;
    static GUIGroup playerMenu;

    static int current = 1;

    static int pointer = 0;

    static boolean active = false;

    public static void initialize(){
        KeyboardController.addKeyboardListener(new InputListener());

        mainMenu = new GUI();

        menuGroup = new GUIGroup(new Vector2f(0.25f,0.25f));
        menuGroup.addItem("background", new GUITexture(Texture.ofColor(Color.GRAY), new Vector2f(0,0), new Vector2f(0.5f,0.5f)));
        menuGroup.getItem("background").setLayer(-0.5f);

        menuGroup.addItem("left", new GUIButton(
                new Vector2f(0,0.45f),
                new Vector2f(0.05f, 0.05f),
                Texture.ofColor(Color.RED),
                () -> current--));

        menuGroup.addItem("right", new GUIButton(
                new Vector2f(0.45f,0.45f),
                new Vector2f(0.05f, 0.05f),
                Texture.ofColor(Color.RED),
                () -> current++));

        inventoryMenu = new GUIGroup(new Vector2f(0,0));
        inventoryMenu.addItem("title",
                new GUIText(Text.from("Inventory").size(0.14f).center(true), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.18f,0.45f)));
        inventoryMenu.addItem("holder", new GUIGroup(new Vector2f(0,0)));

        questMenu = new GUIGroup(new Vector2f(0,0));
        questMenu.addItem("title",
                new GUIText(Text.from("Quests").size(0.14f).center(true), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.20f,0.45f)));
        questMenu.addItem("holder1", new GUIGroup(new Vector2f(0,0)));
        questMenu.addItem("holder2", new GUIGroup(new Vector2f(0.2f,0)));
        questMenu.addItem("arrow", new GUITexture(Texture.ofColor(Color.BLUE), new Vector2f(0,0), new Vector2f(0.02f,0.02f)));


        playerMenu = new GUIGroup(new Vector2f(0,0));

        mainMenu.getRoot().addItem("menu", menuGroup);

        menuGroup.addItem("inv", inventoryMenu);
        menuGroup.addItem("quests", questMenu);
        menuGroup.addItem("player", playerMenu);


        GUIController.addAndUse(mainMenu, "menu");

    }

    public static void update(){
        menuGroup.setEnabled(active);

        if(!active) return;


        if(current == 0){
            menuGroup.getItem("left").setEnabled(false);
            menuGroup.getItem("right").setEnabled(true);


            inventoryMenu.setEnabled(true);
            questMenu.setEnabled(false);
            playerMenu.setEnabled(false);

            int i = 0;

            var holder = (GUIGroup) inventoryMenu.getItem("holder");

            holder.clear();

            for(var item : Player.PLAYER.getItems().entrySet()){
                if(item.getValue() == 0) continue;

                var itemGroup = new GUIGroup(new Vector2f(0, 0.40f - (i * 0.08f)));

                var itemClass = ItemManager.generate(item.getKey());

                Texture texture;
                if(itemCache.containsKey(itemClass.sprite)) texture = itemCache.get(itemClass.sprite);
                else texture = Texture.create(Texture.config(), itemClass.sprite);
                itemCache.putIfAbsent(itemClass.sprite, texture);

                itemGroup.addItem(item.getKey(), new GUITexture(
                        texture, new Vector2f(0.001f,-0.025f), new Vector2f(0.05f,0.05f)));

                itemGroup.addItem("quantity", new GUIText(
                        Text.from( " x " + item.getValue()).size(0.1f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.05f,0)
                ));

                itemGroup.addItem("desc", new GUIText(
                        Text.from(itemClass.desc).size(0.1f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.10f,0)
                ));

                holder.addItem(item.getKey(), itemGroup);

                i++;
            }
        }
        else if(current == 1){

            menuGroup.getItem("left").setEnabled(true);
            menuGroup.getItem("right").setEnabled(true);

            inventoryMenu.setEnabled(false);
            questMenu.setEnabled(true);
            playerMenu.setEnabled(false);

            var questHolder = (GUIGroup) questMenu.getItem("holder1");

            questHolder.clear();

            var subHolder = (GUIGroup) questMenu.getItem("holder2");

            subHolder.clear();

            var arrow = (GUIItem) questMenu.getItem("arrow");

            int currentLine = 0;

            var progress = QuestManager.getQuests().values().stream()
                    .filter(q -> q.state == Quest.QuestState.ACTIVE).collect(Collectors.toList());

            pointer = FastMath.clamp(pointer, 0, progress.size()-1);

            for(var quest : progress) {
                var singleQuest = new GUIGroup(new Vector2f(0, 0.42f - (currentLine * 0.048f)));
                singleQuest.addItem("label",
                        new GUIText(Text.from(quest.displayName).size(0.09f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.02f,0)));
                int cline2 = currentLine;

                singleQuest.addItem("listener", new GUIButton(
                        new Vector2f(0.029f,-0.02f),
                        new Vector2f(0.18f, 0.06f),
                        Texture.ofColor(Color.RED, 0.5f),
                        () -> pointer = cline2));

                questHolder.addItem(quest.name, singleQuest);

                if(currentLine == pointer) arrow.setPositionOffset(new Vector2f(0, 0.42f - (currentLine * 0.048f)));
                currentLine++;
            }

            var selectedQuest = progress.get(pointer);

            /*subHolder.addItem("name",
                    new GUIText(Text.from(selectedQuest.displayName).size(0.09f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.029f,0.40f)));*/

            currentLine = 0;

            var allSubs = selectedQuest.subQuests.values().stream()
                    .filter(s -> s.state != Quest.QuestState.NOT_STARTED)
                    .collect(Collectors.toList());

            for(var sub : allSubs){
                var singleSub = new GUIGroup(new Vector2f(0.0f, 0.42f - (currentLine * 0.048f)));

                singleSub.addItem("text", new GUIText(Text.from(sub.displayName).size(0.08f), Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.02f,0)));
                subHolder.addItem(sub.name, singleSub);
                currentLine++;
            }

        }else if(current == 2){
            menuGroup.getItem("left").setEnabled(true);
            menuGroup.getItem("right").setEnabled(false);

            inventoryMenu.setEnabled(false);
            questMenu.setEnabled(false);
            playerMenu.setEnabled(true);
        }
    }

    public static void setEnabled(boolean enabled){
        if(enabled){
            active = true;
            WorldEngine.shouldUpdate(false);
        }else{
            active = false;
            WorldEngine.shouldUpdate(true);
        }
    }

    static class InputListener implements KeyboardListener{

        @Override
        public void keyPressed(int key) {
            if(key == Key.KEY_ESCAPE) setEnabled(!active);

            if(!active) return;

            if(key == Key.KEY_DOWN) pointer++;
            if(key == Key.KEY_UP && pointer != 0) pointer--;
        }

        @Override
        public void keyReleased(int key) {

        }
    }
}
