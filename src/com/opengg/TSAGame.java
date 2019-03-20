package com.opengg;

import com.opengg.components.InteractableAI;
import com.opengg.components.PlayerWorldComponent;
import com.opengg.components.WorldEnemy;
import com.opengg.components.WorldEntryZone;
import com.opengg.core.animation.Animation;
import com.opengg.core.animation.AnimationManager;
import com.opengg.core.audio.SoundEngine;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIButton;
import com.opengg.core.gui.GUIController;
import com.opengg.core.gui.GUITexture;
import com.opengg.core.io.ControlType;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.Component;
import com.opengg.dialogue.DialogueManager;
import com.opengg.dialogue.DialogueSequence;
import com.opengg.game.*;
import com.opengg.gui.GameMenu;

import java.awt.*;

import static com.opengg.core.io.input.keyboard.Key.*;

public class TSAGame extends GGApplication {
    public static TSAGame INSTANCE;
    public static WorldEntryZone lastLoad;

    public static void main(String[] args) {
        var wininfo = new WindowInfo();
        wininfo.width = 1280;
        wininfo.height = 720;
        wininfo.vsync = true;
        wininfo.resizable = true;
        wininfo.name = "TSA Game";
        OpenGG.initialize(INSTANCE = new TSAGame(), wininfo);
    }

    public PlayerWorldComponent getCurrentPlayerComponent(){
        return (PlayerWorldComponent) WorldEngine.getCurrent().find("player");
    }

    @Override
    public void setup() {
        this.applicationName = "Simon's Quest: Between Worlds";
        GGConsole.log("Initializing TSA Game submanagers");

        StringManager.initialize();
        ItemManager.initialize();
        BehaviorManager.initialize();
        DialogueManager.initialize();
        CharacterManager.initialize();
        QuestManager.initialize();
        BattleManager.initialize();
        GameMenu.initialize();

        ShaderController.use("object.vert", "arraytex.frag");
        ShaderController.saveCurrentConfiguration("texanim");

        ShaderController.use("particle.vert", "arraytex.frag");
        ShaderController.saveCurrentConfiguration("particleanim");

        ShaderController.findUniform("anim");
        ShaderController.setTextureLocation("anim", 12);

        ShaderController.findUniform("layer");
        ShaderController.setUniform("layer", 0);

        ShaderController.findUniform("invertMultiplier");
        ShaderController.setUniform("invertMultiplier", 0);

        GUI blackout = new GUI();
        GUIButton start = new GUIButton(new Vector2f(0,0),new Vector2f(0.2f,0.3f), Texture.ofColor(Color.BLUE));
        blackout.addItem("tex", new GUITexture(Texture.ofColor(Color.BLACK, 1), new Vector2f(0, 0), new Vector2f(1, 1)).setLayer(-0.5f));
        
        GUIController.addAndUse(blackout, "black");

        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "interact", KEY_ENTER);

        try {

            WorldEngine.useWorld(WorldLoader.loadWorld("flowerforest.bwf"));
            BindController.setEnabled(false);

            lastLoad = (WorldEntryZone) (WorldEngine.getCurrent().getAllDescendants().stream()
                    .filter(c -> c instanceof WorldEntryZone)
                    .filter(c -> c.getName().equals("spawn"))).findAny().get();

            Animation anim = new Animation(3, false);

            anim.addStaticEvent(Animation.AnimationStage.createStaticStage(0, 3,
                    d -> Texture.ofColor(Color.BLACK, (float) ((float) 1 - (d / 3))),
                    t -> ((GUITexture) GUIController.get("black").getRoot().getItem("tex")).setTexture(t)));
            anim.setOnCompleteAction(() -> {
                BindController.setEnabled(true);
                new DialogueSequence((InteractableAI) WorldEngine.getCurrent().find("workerbee0")).start();
            });
            GUIButton button = new GUIButton(new Vector2f(0.3f,0.1f),new Vector2f(0.4f,0.2f),Resource.getTexture("button.png"));
            button.setLayer(0.7f);
            button.setOnClick(()->{anim.start();button.setEnabled(false);});
           blackout.addItem("button",button);
            //blackout.addItem("button1",button);
            AnimationManager.register(anim);
            //anim.start();

            SoundEngine.setGlobalGain(0.5f);

        } catch (NullPointerException e) {
            GGConsole.error("Failed to find world!");
        }

        WorldEngine.getCurrent().attach(new PlayerWorldComponent().setPositionOffset(new Vector3f(WorldEngine.getCurrent().getAllDescendants().stream()
                .filter(c -> c instanceof WorldEntryZone)
                .filter(c -> c.getName().equals("spawn"))
                .map(Component::getPosition)
                .findAny().orElse(new Vector3f(0, 0, 0)))));



        WindowController.getWindow().setCursorLock(false);

        QuestManager.beginQuest("goToBeeville");
        Player.PLAYER.getInventory().addAbility("fists");
    }

    @Override
    public void render() {

    }

    @Override
    public void update(float delta) {
        WorldEngine.getCurrent().remove(WorldEngine.getCurrent().getAllDescendants().stream()
                .filter(c -> c instanceof WorldEnemy).map(c -> (WorldEnemy)c)
                .filter(c -> c.getCharacterType().equals("default"))
                .findAny()
                .orElse(null));
        GameMenu.update();
        DialogueManager.update(delta);
        QuestManager.update(delta);
        BattleManager.update();
    }
}
