package com.opengg.components;

import com.opengg.TSAGame;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.physics.collision.Floor;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.WorldChangeZone;
import com.opengg.core.world.components.physics.CollisionComponent;

import java.io.IOException;
import java.util.stream.Collectors;

public class TSAWorldChangeZone extends WorldChangeZone {
    private String target;

    public TSAWorldChangeZone(){
        super("", new AABB(2,2,2));

        this.setExitCondition(i -> i.data instanceof PlayerWorldComponent);
    }

    public TSAWorldChangeZone(String world, String target){
        this();
        this.target = target;
        this.setWorld(world);
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget(){
        return target;
    }

    public void setExitThing(String targeto){
        this.setOnExit((__) -> {
            ((WorldEntryZone) WorldEngine.getCurrent().find(targeto)).spawn();
            TSAGame.lastLoad = ((WorldEntryZone) WorldEngine.getCurrent().find(targeto));
            WorldEngine.getCurrent().remove(WorldEngine.getCurrent().getAllDescendants().stream()
                    .filter(c -> c instanceof WorldEnemy).map(c -> (WorldEnemy)c)
                    .filter(c -> c.getCharacterType().equals("default"))
                    .findAny()
                    .orElse(null));
            OpenGG.asyncExec(() -> {
                var colls = WorldEngine.getCurrent().getAllDescendants()
                        .stream()
                        .filter(c -> c instanceof CollisionComponent)
                        .map(c -> (CollisionComponent)c)
                        .collect(Collectors.toList());

                pre:
                for (var obj : WorldEngine.getCurrent().getSystem().getColliders()) {
                    for (var collider : colls) {
                        if (collider.getColliderGroup() == obj || !(obj.getColliders().stream().anyMatch(c -> c instanceof ConvexHull))) {
                            continue pre;
                        }
                    }
                    OpenGG.asyncExec(() -> WorldEngine.getCurrent().getSystem().removeCollider(obj));
                }

            });

        });
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(target);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        target = in.readString();
        this.setBox(new AABB(3,3,3));
        setExitThing(target);
    }
}
