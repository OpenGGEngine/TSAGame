package com.opengg.components;

import com.opengg.BehaviorManager;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.game.CharacterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldAI extends Component {
    PhysicsComponent physics;
    SpriteRenderComponent sprite;

    String character;
    String characterType;

    String behavior = "none";
    List<String> args = List.of();

    HashMap<String, String> savedValues = new HashMap<>();
    float speed = 2;

    public WorldAI() {

    }

    public WorldAI(String character){
        this.character = character;
        sprite = new SpriteRenderComponent(CharacterManager.getExisting(character).getSprite());
        this.attach(sprite);

        characterType = CharacterManager.getExisting(character).getName();

        this.setUpdateDistance(50);

        physics = new PhysicsComponent();
        physics.addCollider(new ColliderGroup(new AABB(3,3,3), new ConvexHull(List.of(
                new Vector3f(-0.2f,0,-0.2f),
                new Vector3f(-0.2f,0,0.2f),
                new Vector3f(-0.2f,1,-0.2f),
                new Vector3f(-0.2f,1,0.2f),
                new Vector3f(0.2f,0,-0.2f),
                new Vector3f(0.2f,0,0.2f),
                new Vector3f(0.2f,1,-0.2f),
                new Vector3f(0.2f,1,0.2f)
        ))));
        this.attach(physics);
    }

    public float getSpeed() {
        return speed;
    }

    public String getCharacter() {
        return character;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void setCharacterType(String character){
        this.characterType = character;
    }

    public HashMap<String, String> getSavedValues() {
        return savedValues;
    }

    public void checkAlive(){
        if(!CharacterManager.getExisting(character).isLiving())
            WorldEngine.markForRemoval(this);
    }

    @Override
    public void update(float delta){
        var vel = BehaviorManager.getBehaviorFunc(behavior).apply(this, args);
        physics.getEntity().velocity = physics.getEntity().velocity.setX(vel.x).setZ(vel.y);
        sprite.setAngle((float) Math.toDegrees(FastMath.atan2(vel.y, vel.x)));

        if(vel.length() != 0) sprite.setAnimToUse("walk");
        else sprite.setAnimToUse("idle");
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(behavior);
        out.write(args.size());

        for(var arg : args){
            out.write(arg);
        }

        out.write(characterType);
        out.write(speed);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);

        behavior = in.readString();
        var argssize = in.readInt();

        args = new ArrayList<>();
        for(int i = 0; i < argssize; i++){
            args.add(in.readString());
        }

        characterType = in.readString();
        character = CharacterManager.generate(characterType);
        speed = in.readFloat();
    }
}
