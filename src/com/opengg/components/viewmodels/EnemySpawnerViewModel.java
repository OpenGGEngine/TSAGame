package com.opengg.components.viewmodels;

import com.opengg.components.EnemySpawner;
import com.opengg.components.WorldAI;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;
import com.opengg.game.CharacterManager;

import java.util.List;

@ForComponent(EnemySpawner.class)
public class EnemySpawnerViewModel extends ViewModel<EnemySpawner> {
    @Override
    public void createMainViewModel() {
        Element character = new Element();
        character.autoupdate = true;
        character.type = Element.Type.STRING;
        character.name = "Character";
        character.internalname = "character";
        character.value = "default";

        Element amount = new Element();
        amount.autoupdate = true;
        amount.type = Element.Type.INTEGER;
        amount.name = "Spawn amount";
        amount.internalname = "amount";
        amount.value = 0;

        addElement(character);
        addElement(amount);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public EnemySpawner getFromInitializer(Initializer init) {
        return new EnemySpawner("default", 1);
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("character")){
            getComponent().setCharacter((String) element.value);
        }
        if(element.internalname.equals("amount")){
            getComponent().setAmount((Integer) element.value);
        }
    }

    @Override
    public void updateView(Element element) {
        if(element.internalname.equals("character")){
            element.value = getComponent().getCharacter();
        }
        if(element.internalname.equals("amount")){
            element.value = getComponent().getAmount();
        }
    }
}
