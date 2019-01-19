package com.opengg;

import com.opengg.components.InteractableAI;
import com.opengg.components.Player;
import com.opengg.components.WorldEnemy;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.io.ControlType;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.WorldLoader;
import com.opengg.core.world.components.*;
import com.opengg.dialogue.DialogueManager;
import com.opengg.dialogue.DialogueSequence;

import static com.opengg.core.io.input.keyboard.Key.*;

public class TSAGame extends GGApplication {
    public static TSAGame INSTANCE;

    public static void main(String[] args) {
        var wininfo = new WindowInfo();
        wininfo.width = 1280;
        wininfo.height = 720;
        wininfo.name = "TSA Game";
        OpenGG.initialize(INSTANCE = new TSAGame(), wininfo);
    }

    public Player getCurrentPlayerComponent(){
        return (Player) WorldEngine.getCurrent().find("player");
    }

    @Override
    public void setup() {
        BehaviorManager.initialize();
        DialogueManager.initialize();
        DialogueManager.loadNodes("dialog.txt");

        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "interact", KEY_ENTER);

        WorldEngine.getCurrent().attach(new Player());
        WorldEngine.getCurrent().attach(new ModelRenderComponent(Resource.getModel("defaults/hemi.bmf")));
        WorldEngine.getCurrent().attach(new LightComponent(
                Light.createPointShadow(new Vector3f(0,-10,0), new Vector3f(1), 1000, 512, 512 )));
        WorldEngine.getCurrent().attach(new WorldEnemy().setPositionOffset(new Vector3f(0,0,-10)));
        WorldEngine.getCurrent().attach(new InteractableAI().setPositionOffset(-8,0,-5));
        //WorldEngine.getCurrent().attach(new WorldChangeZone("world2", new AABB(2,2,2)).setPositionOffset(new Vector3f(10,0,0)));
        WorldLoader.keepWorld(WorldEngine.getCurrent());

        World world2 = new World("world2");
        world2.attach(new FreeFlyComponent());
        world2.attach(new ModelRenderComponent(Resource.getModel("defaults/torus.bmf")));
        world2.attach(new LightComponent(
                Light.createPointShadow(new Vector3f(0,-10,0), new Vector3f(1,0,0), 1000, 512, 512 )));

        world2.attach(new WorldChangeZone("default", new AABB(2,2,2)).setPositionOffset(new Vector3f(0,-5,0)));

        WorldLoader.keepWorld(world2);

        WorldEngine.getCurrent().getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(
                Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 500f));

        world2.getRenderEnvironment().setSkybox(new Skybox(Texture.getSRGBCubemap(
                Resource.getTexturePath("skybox\\majestic_ft.png"),
                Resource.getTexturePath("skybox\\majestic_bk.png"),
                Resource.getTexturePath("skybox\\majestic_up.png"),
                Resource.getTexturePath("skybox\\majestic_dn.png"),
                Resource.getTexturePath("skybox\\majestic_rt.png"),
                Resource.getTexturePath("skybox\\majestic_lf.png")), 500f));

        WindowController.getWindow().setCursorLock(true);

    }

    @Override
    public void render() {

    }

    @Override
    public void update(float delta) {
        DialogueManager.update(delta);
    }
}
