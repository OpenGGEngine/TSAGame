package com.opengg.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.physics.collision.ColliderGroup;
import com.opengg.core.physics.collision.ConvexHull;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.physics.CollisionComponent;

import java.io.IOException;
import java.util.List;

public class FloorComponent extends RenderComponent {
    private Texture blotmap = Texture.get2DTexture(TextureManager.getDefault());
    private Texture array = Texture.getArrayTexture(TextureManager.getDefault(),
            TextureManager.getDefault(),TextureManager.getDefault(),TextureManager.getDefault());

    public FloorComponent(){
        this.attach(new CollisionComponent(new ColliderGroup(new AABB(300,300,300), new ConvexHull(List.of(
                new Vector3f(-1,-1,-1f),
                new Vector3f(-1,-1,1f),
                new Vector3f(-1,1,-1f),
                new Vector3f(-1,1,1f),
                new Vector3f(1,-1,-1f),
                new Vector3f(1,-1,1f),
                new Vector3f(1,1,-1f),
                new Vector3f(1,1,1f)
        )))));

        this.setShader("terrain");
        this.setDrawable(ObjectCreator.createQuadPrism(new Vector3f(-1,-1,-1), new Vector3f(1,1,1)));
    }

    public void setBlotmap(TextureData blotmap){
        this.blotmap = Texture.create(Texture.config(), blotmap);
    }

    public void setArray(TextureData a1, TextureData a2, TextureData a3, TextureData a4){
        this.array = Texture.create(Texture.arrayConfig(), a1, a2, a3, a4);
    }

    public Texture getArray() {
        return array;
    }

    public Texture getBlotmap() {
        return blotmap;
    }

    @Override
    public void render(){
        blotmap.use(1);
        array.use(11);
        ShaderController.setUniform("scale", this.getScale());
        super.render();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(blotmap.getData().get(0).source);
        out.write(array.getData().get(0).source);
        out.write(array.getData().get(1).source);
        out.write(array.getData().get(2).source);
        out.write(array.getData().get(3).source);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);

        String blot = in.readString();

        String s1 = in.readString(), s2 = in.readString(), s3 = in.readString(), s4 = in.readString();

        this.setBlotmap(Resource.getTextureData(blot));
        this.setArray(Resource.getTextureData(s1), Resource.getTextureData(s2), Resource.getTextureData(s3), Resource.getTextureData(s4));
    }
}
