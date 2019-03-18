package com.opengg.components.viewmodels;

import com.opengg.components.ButterflyParticleEmitter;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;

@ForComponent(ButterflyParticleEmitter.class)
public class ButterflyParticleEmitterViewModel extends ViewModel<ButterflyParticleEmitter> {
    @Override
    public void createMainViewModel() {
        addElement(new Element()
                    .name("Spawn amount")
                    .value(10)
                    .internalName("amount")
                    .type(Element.Type.INTEGER)
                    .autoUpdate(true));

        addElement(new Element()
                .name("Spawn range")
                .value(10)
                .internalName("range")
                .type(Element.Type.FLOAT)
                .autoUpdate(true));
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public ButterflyParticleEmitter getFromInitializer(Initializer init) {
        return new ButterflyParticleEmitter();
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("amount"))
            component.setAmount((Integer) element.value);

        if(element.internalname.equals("range"))
            component.setInitialSpread((Float) element.value);
    }

    @Override
    public void updateView(Element element) {
        if(element.internalname.equals("amount"))
            element.value = component.getAmount();

        if(element.internalname.equals("range"))
            element.value = component.getInitialSpread();
    }
}
