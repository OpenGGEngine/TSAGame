package com.opengg.components;

import com.opengg.core.engine.Resource;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.render.AnimatedTexture;

import java.util.HashMap;
import java.util.Map;

public class SpriteRenderComponent extends RenderComponent {
    Map<String, Tuple<AnimatedTexture, AnimatedTexture>> textures = new HashMap<>();
    private float angle = 0;

    String animToUse = "idle";

    AnimatedTexture current;

    public SpriteRenderComponent(){
        textures.put("walk", Tuple.of(
                new AnimatedTexture(
                        Resource.getTexturePath("anims/test/w1.png"),
                        Resource.getTexturePath("anims/test/w2.png"),
                        Resource.getTexturePath("anims/test/w3.png"),
                        Resource.getTexturePath("anims/test/w4.png"),
                        Resource.getTexturePath("anims/test/w5.png"),
                        Resource.getTexturePath("anims/test/w6.png"),
                        Resource.getTexturePath("anims/test/w7.png"),
                        Resource.getTexturePath("anims/test/w8.png")
                ),
                new AnimatedTexture(
                        Resource.getTexturePath("anims/test/w1.png"),
                        Resource.getTexturePath("anims/test/w2.png"),
                        Resource.getTexturePath("anims/test/w3.png"),
                        Resource.getTexturePath("anims/test/w4.png"),
                        Resource.getTexturePath("anims/test/w5.png"),
                        Resource.getTexturePath("anims/test/w6.png"),
                        Resource.getTexturePath("anims/test/w7.png"),
                        Resource.getTexturePath("anims/test/w8.png"))));

        textures.put("idle", Tuple.of(
                new AnimatedTexture(Resource.getTexturePath("anims/test/idle.png")),
                new AnimatedTexture(Resource.getTexturePath("anims/test/idle.png"))));

        this.setDrawable(ObjectCreator.createSquare(new Vector2f(-1,-1), new Vector2f(1,1), 0));
        this.setShader("texanim");
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setAnimToUse(String animToUse) {
        this.animToUse = animToUse;
    }

    @Override
    public void update(float delta){
        boolean forwards = angle >= 0;

        var texTuple = textures.get(animToUse);

        current = forwards ? texTuple.x : texTuple.y;

        current.update(delta);
    }

    @Override
    public void render(){
        boolean right = Math.abs(angle) <= 90;
        ShaderController.setUniform("invertMultiplier", right ? 0 : -1);

        current.use();
        super.render();

    }
}
