package com.opengg.components.viewmodels;

import com.opengg.components.WorldAI;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;
import com.opengg.game.CharacterManager;

import java.util.List;

@ForComponent(WorldAI.class)
public class WorldAIViewModel extends ViewModel<WorldAI> {
    @Override
    public void createMainViewModel() {
        Element character = new Element();
        character.autoupdate = false;
        character.type = Element.Type.STRING;
        character.name = "Character";
        character.internalname = "character";
        character.value = "bobomb";

        Element movement = new Element();
        movement.autoupdate = false;
        movement.type = Element.Type.STRING;
        movement.name = "Behavior";
        movement.internalname = "behavior";
        movement.value = "none";

        Element args = new Element();
        args.autoupdate = false;
        args.type = Element.Type.STRING;
        args.name = "Behavior arguments";
        args.internalname = "args";
        args.value = "";

        addElement(character);
        addElement(movement);
        addElement(args);
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
    public WorldAI getFromInitializer(Initializer init) {
        return new WorldAI(CharacterManager.generate((String) init.get("character").value).trim());
    }

    @Override
    public void onChange(Element element) {
        var comp = (WorldAI) component;
        if(element.internalname.equals("character")){
            comp.setCharacterType((String) element.value);
        }
        if(element.internalname.equals("behavior")){
            comp.setBehavior((String) element.value);
        }
        if(element.internalname.equals("args")){
            comp.setArgs(List.of(((String) element.value).split(";")));
        }
    }

    @Override
    public void updateView(Element element) {

    }
}
