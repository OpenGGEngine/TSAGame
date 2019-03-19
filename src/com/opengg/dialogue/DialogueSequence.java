package com.opengg.dialogue;

import com.opengg.components.InteractableAI;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.*;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.WorldEngine;
import com.opengg.game.CharacterManager;
import com.opengg.game.Player;
import com.opengg.game.Quest;
import com.opengg.game.QuestManager;
import com.opengg.gui.GameMenu;

import java.awt.*;

public class DialogueSequence implements KeyboardListener {
    DialogueNode current;

    GUI currentGUI;

    GUITextBox text;

    String title;

    InteractableAI ai;

    int pointer = 0;

    public DialogueSequence(InteractableAI ai){
        this.ai = ai;
        this.title = CharacterManager.getExisting(ai.getCharacter()).getDisplayName();

        text = new GUITextBox(new Vector2f(0.1f, 0.0f), new Vector2f(0.8f,0.4f))
                .setText("")
                .setBackground(Texture.ofColor(Color.BLACK, 0.5f))
                .setSpeed(1/60f)
                .setTextSize(0.12f)
                .setMargin(0.04f)
                .setScrollMode(GUITextBox.ScrollMode.SPEEDABLE_SCROLL)
                .setFont(Resource.getTruetypeFont("consolas.ttf"));

        currentGUI = new GUI();
        currentGUI.addItem("text", text);

        text.setLayer(0.5f);

        this.enableNode(ai.getDialogue());
        if(ai.getDialogue() == null) throw new RuntimeException("Failed to find dialog named " + ai.getDialogue());
    }

    public void start(){
        WorldEngine.getCurrent().find("player").setEnabled(false);

        GUIController.addAndUse(currentGUI, "dialogue");
        DialogueManager.setCurrent(this);

        ai.setInDialogue(true);
        GameMenu.setEnabled(false);
        GameMenu.dialogDisable = true;
    }

    public void update(float delta){
        text.setText(getText(current));
    }

    public String getText(DialogueNode node){
        var printedString = title + ":\n\n" + node.text;
            printedString += "\n";
            for(int i = 0; i < node.options.size(); i++){
                var data = node.options.get(i);
                if(i == pointer)
                    printedString += "\n\n -> " + data.x;
                else
                    printedString += "\n\n -  " + data.x;
            }

        return printedString;
    }

    public void end(){
        WorldEngine.getCurrent().find("player").setEnabled(true);
        GUIController.deactivateGUI("dialogue");
        DialogueManager.setCurrent(null);

        ai.setInDialogue(false);
        GameMenu.dialogDisable = false;
    }

    public void enableNode(String next){
        current = DialogueManager.getNodeByName(next);

        pointer = 0;
        text.reset();
        text.setText(getText(current));

        if(current.sound != null){
            var sound = new Sound(current.sound);
            sound.setGain(current.volume);
            sound.play();
        }

        if(current.itemAmount != 0){
            Player.PLAYER.getInventory().addItem(current.itemSpawn, current.itemAmount);
        }

        if(!current.quest.equals("")){
            if(!current.questState.equals("")){
                QuestManager.advanceSubQuest(current.quest, current.questState);
            }else{
                QuestManager.beginQuest(current.quest);
            }
        }

        if(!current.setNext.equals("")){
            ai.setDialogue(current.setNext);
        }

        ai.setDialogueAnimation(current.anim);
    }

    public void getNext(){
         if(!current.hasOpts){
            if(!current.next.isEmpty()){
                if(!current.requirementQuest.isEmpty()){
                    if(QuestManager.getQuests().get(current.requirementQuest).subQuests.get(current.requirementQuestState).state == Quest.QuestState.DONE){
                        enableNode(current.next);
                    }else {
                        enableNode(current.failOpt);
                    }
                }else {
                    enableNode(current.next);
                }
            }else{
                OpenGG.asyncExec(this::end);
            }
        }else{
            var next = current.options.get(pointer).y;
            enableNode(next);
        }
    }

    @Override
    public void keyPressed(int key) {
        if(key == Key.KEY_SPACE){
            if(!text.isComplete()){
                text.forceComplete();
            }else {
                getNext();
            }
        }else if(key == Key.KEY_DOWN){
            if(current.hasOpts)
                if(text.isComplete())
                    if(pointer < current.options.size()-1)
                        pointer++;

        }else if(key == Key.KEY_UP){
            if(current.hasOpts)
                if(text.isComplete()){
                    if(pointer > 0)
                        pointer--;
            }
        }
    }

    @Override
    public void keyReleased(int key) {

    }
}
