package com.opengg.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.Vector3fm;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionType;
import com.opengg.core.world.Actionable;
import com.opengg.core.world.components.ActionTransmitterComponent;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.core.world.components.ControlledComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.dialogue.DialogueManager;
import com.opengg.dialogue.DialogueSequence;

import java.util.List;

public class Player extends ControlledComponent implements Actionable {
    SpriteRenderComponent sprite;
    PhysicsComponent physics;
    private Vector3fm control = new Vector3fm();
    float speed = 2;

    public Player(){
        sprite = new SpriteRenderComponent();
        this.attach(sprite);

        physics = new PhysicsComponent();
        physics.addCollider(new ColliderGroup(new AABB(3,3,3), new ConvexHull(List.of(
                new Vector3f(-1,-1,-1),
                new Vector3f(-1,-1,1),
                new Vector3f(-1,1,-1),
                new Vector3f(-1,1,1),
                new Vector3f(1,-1,-1),
                new Vector3f(1,-1,1),
                new Vector3f(1,1,-1),
                new Vector3f(1,1,1)
        ))));
        this.attach(physics);

        var camera = new CameraComponent();
        camera.setPositionOffset(new Vector3f(0,3,10));
        camera.setRotationOffset(new Vector3f(8,0,0));
        this.attach(camera);

        var control = new ActionTransmitterComponent();
        this.attach(control);

        this.setName("player");
    }

    @Override
    public void update(float delta){
        Vector3f vel = this.getRotation().transform(new Vector3f(control).multiply(speed));
        physics.getEntity().velocity = physics.getEntity().velocity.setX(vel.x).setZ(vel.z);
        sprite.setAngle((float) Math.toDegrees(FastMath.atan2(vel.z, vel.x)));
    }


    @Override
    public void onAction(Action action) {
        if(action.type == ActionType.PRESS){
            switch(action.name){
                case "forward":
                    control.z -= 1;
                    break;
                case "backward":
                    control.z += 1;
                    break;
                case "left":
                    control.x -= 1;
                    break;
                case "right":
                    control.x += 1;
                    break;
                case "up":
                    if(physics.getEntity().grounded) physics.getEntity().velocity = physics.getEntity().velocity.setY(5);
                    break;
                case "interact":
                    for(var component : getWorld().getAll()){
                        if(component instanceof InteractableAI) {
                            if (((InteractableAI) component).allowInteraction) {
                                if (component.getPosition().distanceTo(this.getPosition()) < 3f) {
                                    DialogueManager.setCurrent(new DialogueSequence(((InteractableAI) component).dialogue));
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
