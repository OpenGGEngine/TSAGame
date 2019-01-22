package com.opengg.render;

import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;

import java.util.List;

public class AnimatedTexture {
    Texture texture;
    List<String> sources;

    float animDelay = 0.1f;
    boolean repeat = true;

    int frame = 0;
    int animLength;

    float counter;

    public AnimatedTexture(String... sources){
        this(List.of(sources));
    }

    public AnimatedTexture(List<String> sources){
        animLength = sources.size();
        this.sources = sources;
        texture = Texture.create(Texture.arrayConfig(), sources.toArray(new String[0]));
    }

    public void reset(){
        counter = 0;
    }

    public void update(float delta){
        if(frame >= animLength-1){
            if(repeat){
                frame = 0;
            }else{
                return;
            }
        }

        counter += delta;
        if(counter > animDelay){
            counter = 0;
            frame++;
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void use(){
        texture.use(12);
        ShaderController.setUniform("layer", frame);
    }
}
