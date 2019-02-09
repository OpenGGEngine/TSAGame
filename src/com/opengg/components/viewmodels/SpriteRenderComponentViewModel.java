package com.opengg.components.viewmodels;

import com.opengg.components.SpriteRenderComponent;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;

@ForComponent(SpriteRenderComponent.class)
public class SpriteRenderComponentViewModel extends ViewModel<SpriteRenderComponent> {
    @Override
    public void createMainViewModel() {
        addElement(new Element()
                .name("Sprite")
                .internalName("sprite")
                .value("test")
                .type(Element.Type.STRING)
                .autoUpdate(false));
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        init.addElement(new Element()
                        .name("Sprite")
                        .internalName("sprite")
                        .value("test")
                        .type(Element.Type.STRING)
                        .autoUpdate(false)
        );
        return init;
    }

    @Override
    public SpriteRenderComponent getFromInitializer(Initializer init) {
        return new SpriteRenderComponent((String) init.get("sprite").value);
    }

    @Override
    public void onChange(Element element) {
        if(element.name.equals("sprite"))
            component.setCharacter((String) element.value);
    }

    @Override
    public void updateView(Element element) {
        this.getByName("sprite").value = component.getCharacter();
    }
}
