package com.opengg.components;

import com.opengg.core.physics.collision.AABB;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.WorldChangeZone;

import java.io.IOException;

public class TSAWorldChangeZone extends WorldChangeZone {
    private String target;

    public TSAWorldChangeZone(){
        this.target = "";
        this.setExitCondition(i -> i.data instanceof PlayerWorldComponent);
    }

    public TSAWorldChangeZone(String world, String target){
        super(world, new AABB(2,2,2));
        this.target = target;

        this.setOnExit((__) -> ((WorldEntryZone) WorldEngine.getCurrent().find(target)).spawn());
        this.setExitCondition(i -> i.data instanceof PlayerWorldComponent);
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget(){
        return target;
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

        this.setOnExit((__) -> ((WorldEntryZone) WorldEngine.getCurrent().find(target)).spawn());
    }
}
