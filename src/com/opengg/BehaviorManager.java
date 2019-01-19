package com.opengg;

import com.opengg.components.WorldAI;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        behaviors.put("none", ((c, data) -> new Vector2f(0,0)));
    }

    public static BiFunction<WorldAI, List<String>, Vector2f> getBehaviorFunc(String name){
        return behaviors.get(name);
    }
}
