package com.opengg.game;

import com.opengg.TSAGame;
import com.opengg.components.WorldAI;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;

public class BehaviorManager {
    public static final Map<String, BiFunction<WorldAI, List<String>, Vector2f>> behaviors = new HashMap<>();

    public static void initialize(){
        behaviors.put("chase", (c, data) -> {
            var player = TSAGame.INSTANCE.getCurrentPlayerComponent();
            var range = Integer.parseInt(data.get(0));
            if(c.getPosition().distanceToSquared(player.getPosition()) < range*range){
                Vector3f vel = player.getPosition().subtract(c.getPosition()).normalize().multiply(c.getSpeed());
                return new Vector2f(vel.x, vel.z);
            }
            return new Vector2f(0,0);
        });

        behaviors.put("patrol", (c, data) -> {
            c.getSavedValues().putIfAbsent("lastPos", "0");
            var value = c.getSavedValues().get("lastPos");
            var vector = Vector2f.parseVector2f(data.get(Integer.parseInt(value)));

            if(vector.subtract(new Vector2f(c.getPosition().x, c.getPosition().z)).lengthSquared() < 0.05f)
                c.getSavedValues().replace("lastPos", value.equals("0") ? "1" : "0");

            return vector.subtract(new Vector2f(c.getPosition().x, c.getPosition().z)).normalize().multiply(c.getSpeed());
        });

        behaviors.put("random", (c, data) -> {
            c.getSavedValues().putIfAbsent("target", c.getPosition().x + "," + (c.getPosition().z+0.01f));
            c.getSavedValues().putIfAbsent("center", c.getPosition().x + "," + (c.getPosition().z));
            c.getSavedValues().putIfAbsent("sleep", "0");
            var sleepCounter = (int) Float.parseFloat(c.getSavedValues().get("sleep"));
            var target = Vector2f.parseVector2f(c.getSavedValues().get("target"));
            var center = Vector2f.parseVector2f(c.getSavedValues().get("center"));


            if(sleepCounter != 0){
                c.getSavedValues().put("sleep", String.valueOf(sleepCounter-1));
                return new Vector2f(0,0);
            }

            if(target.subtract(new Vector2f(c.getPosition().x, c.getPosition().z)).lengthSquared() < 0.05f){
                var targetAngle = Math.random() * 2 * FastMath.PI;
                var targetDistance = Integer.parseInt(data.get(0)) * Math.random();
                var newTarget = new Vector2f(FastMath.cos((float) targetAngle), FastMath.sin((float) targetAngle)).multiply((float) targetDistance);
                newTarget = newTarget.add(center);

                c.getSavedValues().replace("target", newTarget.x + "," + newTarget.y);
                c.getSavedValues().put("sleep", String.valueOf((new Random().nextInt(2)+2) / OpenGG.getLastTickLength()));

                return new Vector2f(0,0);
            }

            return target.subtract(new Vector2f(c.getPosition().x, c.getPosition().z)).normalize().multiply(c.getSpeed());
        });

        behaviors.put("follow", (c, data) -> {
            var target = new Vector2f(TSAGame.INSTANCE.getCurrentPlayerComponent().getPosition().x, TSAGame.INSTANCE.getCurrentPlayerComponent().getPosition().z);

            if(target.subtract(new Vector2f(c.getPosition().x, c.getPosition().z)).lengthSquared() < Float.parseFloat(data.get(0))){
                return new Vector2f(0,0);
            }

            return target.subtract(new Vector2f(c.getPosition().x, c.getPosition().z)).normalize().multiply(c.getSpeed());
        });

        behaviors.put("none", ((c, data) -> new Vector2f(0,0)));
    }

    public static BiFunction<WorldAI, List<String>, Vector2f> getBehaviorFunc(String name){
        return behaviors.get(name);
    }
}
