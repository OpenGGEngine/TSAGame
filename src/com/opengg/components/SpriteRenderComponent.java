package com.opengg.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.render.AnimatedTexture;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SpriteRenderComponent extends RenderComponent {
    private static Pattern numberIdentifier = Pattern.compile("([0-9]+)");
    private static Pattern valueIdentifier = Pattern.compile("([a-zA-Z]*)(?:[0-9]*)\\.");


    Map<String, AnimatedTexture> textures;
    private float angle = 0;

    String animToUse = "idle";

    AnimatedTexture current;

    String character;

    private static Map<String, AnimatedTexture> getTextureMapFor(String character) {
        var path = "resources/tex/characters/" + character + "/";
        var dir = new File(Resource.getAbsoluteFromLocal(path));

        var files = dir.list();

        var temporaryMap = new HashMap<String, List<Tuple<String, Integer>>>();

        for(String file : files){
            var matcher = numberIdentifier.matcher(file);

            int framenum = 0;
            if(matcher.find()){
                framenum = Integer.parseInt(matcher.group(1)); //gets frame number
            }
            var valueMatcher = valueIdentifier.matcher(file);//gets frame name
            valueMatcher.find();

            var framename = valueMatcher.group(1);

            if(!temporaryMap.containsKey(framename)){
                temporaryMap.put(framename, new ArrayList<>());
            }

            temporaryMap.get(framename).add(Tuple.of(Resource.getTexturePath("characters/" + character + "/" + file), framenum));
        }

        for(var list : temporaryMap.values()){
            list.sort(Comparator.comparingInt(x -> x.y));
        }

        return temporaryMap.entrySet().stream()
                .map(e -> Map.entry(
                        e.getKey(),
                        new AnimatedTexture(
                                e.getValue().stream()
                                    .map(ee -> ee.x)
                                    .collect(Collectors.toList()))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SpriteRenderComponent(){
        this.setDrawable(ObjectCreator.createSquare(new Vector2f(-1,-1), new Vector2f(1,1), 0));
        this.setShader("texanim");
    }

    public SpriteRenderComponent(String character){
        this();
        setCharacter(character);
    }

    public void setCharacter(String character){
        this.character = character;
        this.textures = getTextureMapFor(character);
        current = textures.get("idle");
    }

    public String getCharacter() {
        return character;
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

        if(textures.containsKey(animToUse))
            current = textures.get(animToUse);
        else
            current = forwards ? textures.get("front_" + animToUse) : textures.get("back_" + animToUse);

        current.update(delta);
    }

    @Override
    public void render(){
        boolean right = Math.abs(angle) <= 90;
        ShaderController.setUniform("invertMultiplier", right ? 0 : -1);

        current.use();
        super.render();
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(character);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        var chara = in.readString();
        setCharacter(chara);
    }
}
