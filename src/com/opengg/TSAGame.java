package com.opengg;

import com.opengg.components.*;
import com.opengg.core.audio.SoundEngine;
import com.opengg.core.audio.Soundtrack;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.io.ControlType;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.LightComponent;
import com.opengg.core.world.components.ModelComponent;
import com.opengg.dialogue.DialogueManager;
import com.opengg.game.*;
import com.opengg.gui.GameMenu;

import static com.opengg.core.io.input.keyboard.Key.*;

public class TSAGame extends GGApplication {
    public static TSAGame INSTANCE;

    public static void main(String[] args) {
        var wininfo = new WindowInfo();
        wininfo.width = 1024;
        wininfo.height = 1024;
        wininfo.vsync = false;
        wininfo.name = "TSA Game";
        OpenGG.initialize(INSTANCE = new TSAGame(), wininfo);
    }

    public PlayerWorldComponent getCurrentPlayerComponent(){
        return (PlayerWorldComponent) WorldEngine.getCurrent().find("player");
    }

    @Override
    public void setup() {
        this.applicationName = "TSA Game";
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

        ShaderController.findUniform("anim");
        ShaderController.setTextureLocation("anim", 12);

        ShaderController.findUniform("layer");
        ShaderController.setUniform("layer", 0);

        ShaderController.findUniform("invertMultiplier");
        ShaderController.setUniform("invertMultiplier", 0);

        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "interact", KEY_ENTER);

       try {
           WorldEngine.useWorld(WorldLoader.loadWorld("flowerforest.bwf"));
       }catch(NullPointerException e){
           GGConsole.error("Failed to find world!");
       }

       // WorldEngine.getCurrent().attach(new LightComponent(Light.createDirectional(new Quaternionf(new Vector3f(0,0,-80)), new Vector3f(1,1,200f/255f))));
        WorldEngine.getCurrent().attach(new PlayerWorldComponent().setPositionOffset(new Vector3f(WorldEngine.getCurrent().getAllDescendants().stream()
                                                                                                                            .filter(c -> c instanceof WorldEntryZone)
                                                                                                                            .filter(c -> c.getName().equals("spawn"))
                                                                                                                            .map(Component::getPosition)
                                                                                                                            .findAny().orElse(new Vector3f(0,0,0))                                                                                                                            )));

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(
                Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 500f));

       /* world2.getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(
                Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 500f));*/

        WindowController.getWindow().setCursorLock(false);

        Soundtrack test = new Soundtrack();
        test.addSong(Resource.getSoundData("stardust.ogg"));
        test.play();

        SoundEngine.setGlobalGain(0.0f);
    }

    @Override
    public void render() {

    }

    @Override
    public void update(float delta) {
        GameMenu.update();
        DialogueManager.update(delta);
        QuestManager.update(delta);
        BattleManager.update();
    }
}
