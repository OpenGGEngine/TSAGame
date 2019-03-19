package com.opengg.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.game.CharacterManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class EnemySpawner extends Component {
    private List<String> currentSpawns = new ArrayList<>();
    private int amount;
    private String character;

    public EnemySpawner(){

    }

    public EnemySpawner(String character, int amount){
        this.amount = amount;
        this.character = character;
    }

    public int getAmount() {
        return amount;
    }

    public EnemySpawner setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getCharacter() {
        return character;
    }

    public EnemySpawner setCharacter(String character) {
        this.character = character;
        return this;
    }

    @Override
    public void update(float delta){
        //currentSpawns.removeIf(spawn -> !((WorldAI) WorldEngine.getCurrent().find(spawn)).isLiving());
    }

    @Override
    public void onWorldEnable(){
        IntStream.range(currentSpawns.size(), amount)
                .mapToDouble(i -> (i / (float)amount) * 2 * FastMath.PI)
                .mapToObj(d -> new Vector3f(FastMath.sin((float) d) * 5 + new Random().nextFloat()*4, 0, FastMath.cos((float) d) * 5 + new Random().nextFloat()*4))
                .map(v -> new WorldEnemy(CharacterManager.generate(character)).setPositionOffset(v))
                .peek(c -> c.setPositionOffset(c.getPosition().add(this.getPosition())))
                .peek(c -> OpenGG.asyncExec(() -> this.getWorld().attach(c)))
                .forEach(c -> currentSpawns.add(((WorldAI) c).character));
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(character);
        out.write(amount);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        character = in.readString();
        amount = in.readInt();
    }
}
