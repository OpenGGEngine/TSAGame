package com.opengg.components;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.particle.Particle;
import com.opengg.core.world.components.particle.ParticleEmitter;
import com.opengg.render.AnimatedTexture;

import java.io.IOException;

public class ButterflyParticleEmitter extends ParticleEmitter {
    AnimatedTexture texture;
    int amount;
    boolean done = false;
    float initialSpread = 10f;

    public ButterflyParticleEmitter(){
        this(25);
    }

    public ButterflyParticleEmitter(int amount) {
        this.amount = amount;
        texture = SpriteRenderComponent.getTextureMapFor("butterfly").get("fly");
        texture.setAnimDelay(0.05f);
        this.setShader("particleanim");
    }

    public void onWorldEnable(){
        if(done) return;
        for(int i = 0; i < amount; i++){
            var particle = new Particle(
                    this.getPosition().add(
                            new Vector3f(FastMath.random(2)-1,0,FastMath.random(2)-1).multiply(initialSpread).setY(4)),
                    new Vector3f(FastMath.random(), FastMath.random(), FastMath.random()),
                    0f, -1f, 0.1f);
            this.addParticle(particle);
        }
        done = true;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getInitialSpread() {
        return initialSpread;
    }

    public void setInitialSpread(float initialSpread) {
        this.initialSpread = initialSpread;
    }

    @Override
    public void update(float delta){
        super.update(delta);

        for(var particle : this.getParticles()){
            particle.setVelocity(
                    particle.getVelocity().add(
                            new Vector3f(FastMath.random(2)-1,FastMath.random(2)-1,FastMath.random(2)-1).multiply(45 * delta)).normalize()
            );
        }

        texture.update(delta);
    }

    @Override
    public void render(){
        texture.use();
        super.render();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(initialSpread);
        out.write(amount);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        initialSpread = in.readFloat();
        amount = in.readInt();
    }

}
