package com.opengg.dialogue;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIController;
import com.opengg.core.gui.GUIText;
import com.opengg.core.gui.GUITexture;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.WorldEngine;

import java.awt.*;

public class DialogueSequence implements KeyboardListener {
    boolean complete;
    DialogueNode current;

    GUI currentGUI;
    String previousGUI;

    GUIText text;

    float counter = 0;
    int textLength = 0;

    int pointer = 0;

    public DialogueSequence(String start){
        this.current = DialogueManager.getNodeByName(start);
        if(start == null) throw new RuntimeException("Failed to find dialog named " + start);
    }

    public void start(){
        previousGUI = GUIController.getCurrentName();
        WorldEngine.getCurrent().find("player").setEnabled(false);

        currentGUI = new GUI();
        currentGUI.addItem("text", text = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0.12f, 0.36f)));
        currentGUI.addItem("background",
                new GUITexture(Texture.ofColor(Color.BLACK, 0.5f), new Vector2f(0.1f,0), new Vector2f(0.8f,0.4f)));
        text.setLayer(0.5f);
        GUIController.addAndUse(currentGUI, "dialogue");
        DialogueManager.setCurrent(this);

    }

    public void update(float delta){
        counter += delta;
        if(counter > (1/50f)){
            counter = 0;
            if(textLength < current.text.length())
                textLength += 1;

            var printedString = current.text.substring(0, textLength);
            if(textLength == current.text.length()){
                printedString += "\n";
                for(int i = 0; i < current.options.size(); i++){
                    var data = current.options.get(i);
                    if(i == pointer)
                        printedString += "\n\n -> " + data.x;
                    else
                        printedString += "\n\n -  " + data.x;
                }
            }
            text.setText(Text.from(printedString).size(0.12f).maxLineSize(1f).kerning(true)
                    .hasNewline(true));
        }
    }

    public void end(){
        WorldEngine.getCurrent().find("player").setEnabled(true);
        GUIController.useGUI(previousGUI);
        DialogueManager.setCurrent(null);
    }

    @Override
    public void keyPressed(int key) {
        if(key == Key.KEY_SPACE){
            if(!current.hasOpts){
                if(!current.next.isEmpty()){
                    current = DialogueManager.getNodeByName(current.next);
                    textLength = 0;
                    counter = 0;
                    pointer = 0;
                }else{
                    OpenGG.asyncExec(this::end);
                }
            }else{
                if(textLength == current.text.length()){
                    var next = current.options.get(pointer).y;
                    current = DialogueManager.getNodeByName(next);
                    textLength = 0;
                    counter = 0;
                    pointer = 0;
                }
            }
        }else if(key == Key.KEY_DOWN){
            if(current.hasOpts)
                if(textLength == current.text.length())
                    if(pointer < current.options.size()-1)
                        pointer++;

        }else if(key == Key.KEY_UP){
            if(current.hasOpts)
                if(textLength == current.text.length()){
                    if(pointer > 0)
                        pointer--;
            }
        }
    }

    @Override
    public void keyReleased(int key) {

    }
}
