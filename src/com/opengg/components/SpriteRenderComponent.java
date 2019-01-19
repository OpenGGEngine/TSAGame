package com.opengg.components;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.world.components.RenderComponent;

import java.awt.*;

public class SpriteRenderComponent extends RenderComponent {
    private Texture fl = Texture.ofColor(Color.red);
    private Texture bl = Texture.ofColor(Color.black);
    private Texture fr = Texture.ofColor(Color.blue);
    private Texture br = Texture.ofColor(Color.green);
    private float angle = 0;


    public SpriteRenderComponent(){
        this.setDrawable(ObjectCreator.createSquare(new Vector2f(-1,-1), new Vector2f(1,1), 0));
        this.setShader("object");
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void render(){
        boolean forwards = angle >= 0;
        boolean right = Math.abs(angle) <= 90;

        if(forwards)
            if(right)
                fr.use(0);
            else
                fl.use(0);
        else
            if(right)
                br.use(0);
            else
                bl.use(0);

        super.render();
    }
}
