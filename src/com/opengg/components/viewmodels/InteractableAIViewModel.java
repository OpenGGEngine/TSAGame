package com.opengg.components.viewmodels;

import com.opengg.components.InteractableAI;
import com.opengg.components.WorldAI;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;
import com.opengg.game.CharacterManager;

import java.util.List;

@ForComponent(InteractableAI.class)
public class InteractableAIViewModel extends ViewModel<InteractableAI> {
    @Override
    public void createMainViewModel() {
        Element character = new Element();
        character.autoupdate = false;
        character.type = Element.Type.STRING;
        character.name = "Character";
        character.internalname = "character";
        character.value = "bobomb";

        Element movement = new Element();
        movement.autoupdate = true;
        movement.type = Element.Type.STRING;
        movement.name = "Behavior";
        movement.internalname = "behavior";
        movement.value = "none";

        Element args = new Element();
        args.autoupdate = true;
        args.type = Element.Type.STRING;
        args.name = "Behavior arguments";
        args.internalname = "args";
        args.value = "";

        Element dialogue = new Element();
        dialogue.autoupdate = true;
        dialogue.type = Element.Type.STRING;
        dialogue.name = "Starting dialogue node";
        dialogue.internalname = "dialogue";
        dialogue.value = "";

        addElement(character);
        addElement(movement);
        addElement(args);
        addElement(dialogue);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        init.addElement(new Element()
                .name("Character")
                .internalName("character")
                .type(Element.Type.STRING)
                .autoUpdate(true)
                .forceUpdate(true)
                .value(""));

        return init;
    }

    @Override
    public InteractableAI getFromInitializer(Initializer init) {
        return new InteractableAI(CharacterManager.generate((String) init.get("character").value).trim());
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("character")){
            component.setCharacterType((String) element.value);
        }
        if(element.internalname.equals("behavior")){
            component.setBehavior((String) element.value);
        }
        if(element.internalname.equals("args")){
            component.setArgs(List.of(((String) element.value).split(";")));
        }
        if(element.internalname.equals("dialogue")){
            component.setDialogue(((String) element.value));
        }
    }

    @Override
    public void updateView(Element element) {
        if(element.internalname.equals("character")){
            element.value = component.getCharacterType();
        }
        if(element.internalname.equals("behavior")){
            element.value = component.getBehavior();
        }
        if(element.internalname.equals("args")){
            element.value = component.getArgs();
        }
        if(element.internalname.equals("dialogue")){
            element.value = component.getDialogue();
        }
    }
}
