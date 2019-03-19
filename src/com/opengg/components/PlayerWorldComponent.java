package com.opengg.components;

import com.opengg.core.Configuration;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector3fm;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.ActionTransmitterComponent;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.ControlledComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.dialogue.DialogueManager;
import com.opengg.dialogue.DialogueSequence;
import com.opengg.game.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerWorldComponent extends ControlledComponent implements Actionable {
    SpriteRenderComponent sprite;
    PhysicsComponent physics;
    private Vector3fm control = new Vector3fm();
    float speed = 4;
    Sound step;

    public PlayerWorldComponent(){
        step = new Sound(Resource.getSoundData("footstep.ogg"));
        var size = Player.PLAYER.getSize();

        sprite = new SpriteRenderComponent(Configuration.get("playerSprite"));
        sprite.setScaleOffset(size);
        this.attach(sprite);


        physics = new PhysicsComponent();
        physics.addCollider(new ColliderGroup(new AABB(3,3,3), new ConvexHull(List.of(
                new Vector3f(-size/2,0,-0.2f),
                new Vector3f(-size/2,0,0.2f),
                new Vector3f(-size/2,size,-0.2f),
                new Vector3f(-size/2,size,0.2f),
                new Vector3f(size/2,0,-0.2f),
                new Vector3f(size/2,0,0.2f),
                new Vector3f(size/2,size,-0.2f),
                new Vector3f(size/2,size,0.2f)
        ))));
        this.attach(physics);

        var camera = new CameraComponent();
        camera.setPositionOffset(new Vector3f(0,1.8f,3).multiply(size));
        camera.setRotationOffset(new Vector3f(30,0,0));
        this.attach(camera);

        var control = new ActionTransmitterComponent();
        this.attach(control);

        this.setName("player");
    }

    @Override
    public void update(float delta){
        if(this.getPosition().y < 1) this.setPositionOffset(this.getPosition().setY(1));

        Vector3f vel = this.getRotation().transform(new Vector3f(control).multiply(speed));
        physics.getEntity().velocity = physics.getEntity().velocity.setX(vel.x).setZ(vel.z);
        sprite.setAngle((float) Math.toDegrees(FastMath.atan2(vel.z, vel.x)));



        if(vel.length() != 0){
            sprite.setAnimToUse("walk");
        }
        else{
            sprite.setAnimToUse("idle");
            step.stop();
        }

        boolean inZone = false;
        for(var comp : WorldEngine.getCurrent().getAllDescendants().stream().filter(c -> c instanceof FloorComponent).map(c -> (FloorComponent)c).collect(Collectors
                .toList())){
            if(!comp.getRotation().toEuler().equals(new Vector3f())) continue;
            AABB aabb = new AABB(new Vector3f(1,100,1));
            aabb.setScale(comp.getScale());
            aabb.setPosition(comp.getPosition());
            aabb.recalculate();
            if(aabb.isColliding(this.getPosition())) inZone = true;
        }

        if(!inZone){
            this.setPositionOffset(this.getPosition().multiply(0.8f));
        }
    }

    @Override
    public void onDisable(){
        step.stop();
    }

    @Override
    public void onAction(Action action) {
        if(action.type == ActionType.PRESS){
            switch(action.name){
                case "forward":
                    step.play();

                    control.z -= 1;
                    break;
                case "backward":
                    step.play();

                    control.z += 1;
                    break;
                case "left":            step.play();

                    control.x -= 1;
                    break;
                case "right":            step.play();

                    control.x += 1;
                    break;
                case "up":
                    physics.getEntity().velocity = physics.getEntity().velocity.setY(5);
                    break;
                case "interact":
                    for(var component : getWorld().getAllDescendants()){
                        if(component instanceof InteractableAI) {
                            var ai = (InteractableAI) component;
                            if (ai.allowInteraction()) {
                                if (ai.getPosition().distanceTo(this.getPosition()) < 3f) {
                                    new DialogueSequence(ai).start();
                                }
                            }
                        }
                    }
                    break;
            }
        }else{
            switch(action.name){
                case "forward":
                    control.z += 1;
                    break;
                case "backward":
                    control.z -= 1;
                    break;
                case "left":
                    control.x += 1;
                    break;
                case "right":
                    control.x -= 1;
                    break;

            }
        }
    }
}
