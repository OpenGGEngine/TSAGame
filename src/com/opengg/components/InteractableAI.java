package com.opengg.components;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector2f;
import com.opengg.core.world.WorldEngine;

import java.util.List;

public class InteractableAI extends WorldAI {
    private String dialogue = "bird";
    private boolean allowInteraction = true;

    private boolean inDialogue;

    private String dialogueAnimation;

    public InteractableAI() {
    }

    public InteractableAI(String character) {
        super(character);
        args = List.of("0,8", "5,15");
        behavior = "patrol";
    }

    public String getDialogue() {
        return dialogue;
    }

    public void setDialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    public boolean allowInteraction() {
        return allowInteraction;
    }

    public void setAllowInteraction(boolean allowInteraction) {
        this.allowInteraction = allowInteraction;
    }

    public void setDialogueAnimation(String dialogueAnimation) {
        this.dialogueAnimation = dialogueAnimation;
    }

    public void setInDialogue(boolean inDialogue) {
        this.inDialogue = inDialogue;
    }

    @Override
    public void update(float delta){
        if(!inDialogue) super.update(delta);
        else{
            physics.getEntity().velocity = physics.getEntity().velocity.setX(0).setZ(0);
            var dir = WorldEngine.getCurrent().find("player").getPosition().subtract(this.getPosition());
            var dir2d = new Vector2f(dir.x, dir.z).normalize();
            sprite.setAngle((float) Math.toDegrees(FastMath.atan2(dir2d.y, dir2d.x)));

            sprite.setAnimToUse(dialogueAnimation);
        }
    }
}
