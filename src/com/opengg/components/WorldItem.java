package com.opengg.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector2f;
import com.opengg.core.physics.collision.AABB;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.Zone;
import com.opengg.core.world.components.triggers.TriggerInfo;
import com.opengg.game.ItemManager;
import com.opengg.game.Player;

import java.io.IOException;

public class WorldItem extends Component {
    Zone pickup = new Zone();
    String itemId;
    int amount;

    boolean autoGet = true;

    public WorldItem(){

    }

    public WorldItem(String itemid, int amount){
        this.itemId = itemid;
        this.amount = amount;

        pickup = new Zone(new AABB(0.3f,0.3f,0.3f));
        this.attach(pickup);

        this.attach(new RenderComponent(new TexturedDrawnObject(
                ObjectCreator.createSquare(new Vector2f(-0.25f,-0.25f), new Vector2f(0.25f,0.25f), 0),
                Resource.getSRGBTexture(ItemManager.generate(itemid).sprite))));

        pickup.addSubscriber((__, i) -> activate(i));
    }

    public void activate(TriggerInfo  i){
        if(!(i.data instanceof PlayerWorldComponent)) return;
        if(autoGet){
            Player.PLAYER.addItem(itemId, amount);
            OpenGG.asyncExec(() -> WorldEngine.markForRemoval(this));
        }
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(itemId);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException {
        super.deserialize(in);
        itemId = in.readString();

        OpenGG.asyncExec(() -> {
            pickup = getChildren().stream().filter(c -> c instanceof Zone).map(c -> (Zone)c).findFirst().get() ;
            pickup.addSubscriber((__, i) -> activate(i));
        });
    }
}
