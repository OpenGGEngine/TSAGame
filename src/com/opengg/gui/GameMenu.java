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
import com.opengg.game.*;

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
    static GUIGroup bottomDescBox;
    static GUITexture arrow1 = new GUITexture(Resource.getTexture("arrow.png"),new Vector2f(0f,0f),new Vector2f(0.02f,0.025f));
    static GUITexture itembox = new GUITexture(Texture.ofColor(Color.WHITE),new Vector2f(0.20f,0.08f),new Vector2f(0.1f,0.1f));
    static GUIText description = new GUIText(Resource.getTruetypeFont("realfont.ttf"),new Vector2f(0.3255f,0.15f));

    static int current = 2;

    static int pointer = 0;

    static boolean active = false;
    public static boolean dialogDisable = false;

    public static void initialize(){
        KeyboardController.addKeyboardListener(new InputListener());

        mainMenu = new GUI();

        menuGroup = new GUIGroup(new Vector2f(0.15f,0.15f));
        menuGroup.addItem("background", new GUITexture(Resource.getTexture("menu.png"), new Vector2f(0,0), new Vector2f(0.7f,0.7f)));
        menuGroup.getItem("background").setLayer(-0.5f);
        menuGroup.addItem("pArrow",arrow1);
        menuGroup.addItem("items", new GUIButton(
                new Vector2f(0.040f,0.587f),
                new Vector2f(0.08f, 0.06f),
                Resource.getTexture("trans.png"),
                () -> current=0));

        menuGroup.addItem("status", new GUIButton(
                new Vector2f(0.040f,0.457f),
                new Vector2f(0.09f, 0.06f),
                Resource.getTexture("trans.png"),
                () -> current=1));

        menuGroup.addItem("quests1", new GUIButton(
                new Vector2f(0.040f,0.337f),
                new Vector2f(0.10f, 0.06f),
                Resource.getTexture("trans.png"),
                () -> current=2));

        menuGroup.addItem("save", new GUIButton(
                new Vector2f(0.040f,0.21f),
                new Vector2f(0.08f, 0.06f),
                Resource.getTexture("trans.png"),
                () -> current=3));

        menuGroup.addItem("quit", new GUIButton(
                new Vector2f(0.040f,0.087f),
                new Vector2f(0.08f, 0.06f),
                Resource.getTexture("trans.png"),
                () -> current++));

        inventoryMenu = new GUIGroup(new Vector2f(0,0));
        inventoryMenu.addItem("title",
                new GUIText(Text.from("Inventory").size(0.18f).center(true), Resource.getTruetypeFont("realfont.ttf"), new Vector2f(0f,0f)));

        inventoryMenu.addItem("holder", new GUIGroup(new Vector2f(0,0)));

        questMenu = new GUIGroup(new Vector2f(0,0));
        questMenu.addItem("title",
                new GUIText(Text.from("Quests").size(0.18f).center(true), Resource.getTruetypeFont("realfont.ttf"), new Vector2f(0.370f,0.65f)));
        questMenu.addItem("holder1", new GUIGroup(new Vector2f(0f,0.0f)));
        questMenu.addItem("holder2", new GUIGroup(new Vector2f(0f,0)));
        questMenu.addItem("arrow", new GUITexture(Texture.ofColor(Color.BLUE), new Vector2f(0,0), new Vector2f(0.02f,0.02f)));


        playerMenu = new GUIGroup(new Vector2f(0,0));
        playerMenu.addItem("bar",new GUIProgressBar(new Vector2f(0.45f,0.635f),new Vector2f(0.2f,0.02f),Color.GREEN,Color.GRAY).setLayer(-0.5f));
        playerMenu.addItem("barText",new GUIText(Text.from("0/0").size(0.1f),Resource.getTruetypeFont("realfont.ttf"),new Vector2f(0.55f,0.665f)));
        playerMenu.addItem("expbar",new GUIProgressBar(new Vector2f(0.45f,0.58f),new Vector2f(0.2f,0.02f),Color.CYAN,Color.GRAY));
        playerMenu.addItem("expText",new GUIText(Text.from("Lvl 1: 0/0 to next").size(0.08f),Resource.getTruetypeFont("realfont.ttf"),new Vector2f(0.49f,0.61f)));
        playerMenu.addItem("gold",
                new GUIText(Text.from(Player.PLAYER.getMoney() + " gold").size(0.14f).center(true),
                        Resource.getTruetypeFont("realfont.ttf"), new Vector2f(0.51f,0.1f)));

        mainMenu.getRoot().addItem("menu", menuGroup);

        menuGroup.addItem("inv", inventoryMenu);
        menuGroup.addItem("quests", questMenu);
        menuGroup.addItem("player", playerMenu);

        bottomDescBox = new GUIGroup(new Vector2f(0f,0f));
        bottomDescBox.addItem("boxTex",new GUITexture(Resource.getTexture("bottomBox.png"),new Vector2f(0.1655f,0),new Vector2f(0.535f,0.2f)));
        description.setText(Text.from("sdsdsdsdsdsdajkbafsd.kjadf \n jdajk.sahdbadshazcuh").size(0.1f));
        bottomDescBox.addItem("descTex",description);
        bottomDescBox.addItem("descTexture",itembox);
        menuGroup.addItem("bottomD",bottomDescBox);

        GUIController.addAndUse(mainMenu, "menu");

    }

    public static void update(){
        menuGroup.setEnabled(active);
        bottomDescBox.setEnabled(true);
        arrow1.setEnabled(true);
        if(!active) return;


        if(current == 0){
            inventoryMenu.setEnabled(true);
            questMenu.setEnabled(false);
            playerMenu.setEnabled(false);

            int i = 0;

            var holder = (GUIGroup) inventoryMenu.getItem("holder");

            holder.clear();
            String selectedItem = Player.PLAYER.getInventory().getElementByIndex(pointer);
            Item tempItem = ItemManager.generate(selectedItem);
            description.setText(tempItem.desc);
            itembox.setTexture(Texture.create(Texture.config(),tempItem.sprite));
            arrow1.setPositionOffset(new Vector2f(0.19f, 0.64f - (pointer * 0.045f)));
            for(var item : Player.PLAYER.getInventory().getItems().entrySet()){
                Item temp = ItemManager.generate(item.getKey());
                GUIText text = new GUIText(Text.from(temp.displayName+ "        : " + Player.PLAYER.getInventory().getItems().get(item.getKey())).size(0.1f),
                        Resource.getTruetypeFont("realfont.ttf"),new Vector2f(0.22f, 0.65f - (i * 0.045f)));
                holder.addItem(item.getKey(),text);
                i++;
            }
        }
        else if(current == 2){

            var progress = QuestManager.getQuests().values().stream()
                    .filter(q -> q.state == Quest.QuestState.ACTIVE).collect(Collectors.toList());

            pointer = FastMath.clamp(pointer, 0, Math.max(progress.size()-1,0));

            inventoryMenu.setEnabled(false);
            questMenu.setEnabled(true);
            playerMenu.setEnabled(false);

            var questHolder = (GUIGroup) questMenu.getItem("holder1");

            questHolder.clear();

            var holder1 = (GUIGroup) questMenu.getItem("holder2");
            holder1.clear();
            int i=0;
            for(var quest:((Quest)QuestManager.getQuests().values().toArray()[pointer]).subQuests.values()){
                 String mod = "";
                switch(quest.state){
                    case DONE:
                        mod = "Done";
                        break;
                    case ACTIVE:
                        mod = "Active";
                        break;
                    case NOT_STARTED:
                        continue;
                }
                GUIText text = new GUIText(Text.from(quest.displayName+":  "+mod).size(0.08f),
                        Resource.getTruetypeFont("realfont.ttf"),new Vector2f(0.42f, 0.60f - (i * 0.045f)));
                holder1.addItem(quest.name,text);
                i++;
            }

            arrow1.setPositionOffset(new Vector2f(0.19f, 0.60f - (pointer * 0.045f)));
            i=0;
            for(var quest : progress){
                GUIText text = new GUIText(Text.from(quest.displayName).size(0.1f),
                        Resource.getTruetypeFont("realfont.ttf"),new Vector2f(0.22f, 0.60f - (i * 0.045f)));
                questHolder.addItem(quest.name,text);
                i++;
            }

            if(progress.isEmpty()) return;
            var selectedQuest = progress.get(pointer);
            description.setText(selectedQuest.desc);

        }else if(current == 1){
            inventoryMenu.setEnabled(false);
            questMenu.setEnabled(false);
            playerMenu.setEnabled(true);
            bottomDescBox.setEnabled(false);
            arrow1.setEnabled(false);
            ((GUIText)playerMenu.getItem("barText")).setText((int)Player.PLAYER.getHealth()+"/"+Player.PLAYER.getMaxHealth());
            ((GUIProgressBar)playerMenu.getItem("bar")).setPercent(Player.PLAYER.getHealth()/Player.PLAYER.getMaxHealth());
            ((GUIText)playerMenu.getItem("expText")).setText("Lvl " + Player.PLAYER.level+" :" +Player.PLAYER.exp+"/"+Player.PLAYER.expNextLevel+ " to next");
            ((GUIProgressBar)playerMenu.getItem("expbar")).setPercent((float)Player.PLAYER.exp/Player.PLAYER.expNextLevel);
            ((GUIText)playerMenu.getItem("gold")).setText("Gold: " + Player.PLAYER.getMoney());
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
            if(!dialogDisable) {
                if (key == Key.KEY_ESCAPE) setEnabled(!active);

                if (!active) return;

                if (key == Key.KEY_DOWN) pointer++;
                if (key == Key.KEY_UP && pointer != 0) pointer--;
            }
        }

        @Override
        public void keyReleased(int key) {

        }
    }
}
