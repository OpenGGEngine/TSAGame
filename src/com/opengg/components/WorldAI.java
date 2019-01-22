package com.opengg.components;

import com.opengg.BehaviorManager;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.physics.PhysicsComponent;

import java.util.HashMap;
import java.util.List;

public class WorldAI extends Component {
    PhysicsComponent physics;
    SpriteRenderComponent sprite;

    String character;

    String behavior = "none";
    List<String> args = List.of();

    HashMap<String, String> savedValues = new HashMap<>();
    float speed = 2;

    public WorldAI() {
        this("default");
    }

    public WorldAI(String character){
        this.character = character;
        sprite = new SpriteRenderComponent();
        this.attach(sprite);

        this.setUpdateDistance(50);

        physics = new PhysicsComponent();
        physics.addCollider(new ColliderGroup(new AABB(3,3,3), new ConvexHull(List.of(
                new Vector3f(-1,0,-0.2f),
                new Vector3f(-1,0,0.2f),
                new Vector3f(-1,1,-0.2f),
                new Vector3f(-1,1,0.2f),
                new Vector3f(1,0,-0.2f),
                new Vector3f(1,0,0.2f),
                new Vector3f(1,1,-0.2f),
                new Vector3f(1,1,0.2f)
        ))));
        this.attach(physics);
    }

    public float getSpeed() {
        return speed;
    }

    public String getCharacter() {
        return character;
    }

    public HashMap<String, String> getSavedValues() {
        return savedValues;
    }

    @Override
    public void update(float delta){
        var vel = BehaviorManager.getBehaviorFunc(behavior).apply(this, args);
        physics.getEntity().velocity = physics.getEntity().velocity.setX(vel.x).setZ(vel.y);
        sprite.setAngle((float) Math.toDegrees(FastMath.atan2(vel.y, vel.x)));

        if(vel.length() != 0) sprite.setAnimToUse("walk");
        else sprite.setAnimToUse("idle");
    }
}
