package com.opengg.components.viewmodels;

import com.opengg.components.TSAWorldChangeZone;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;

@ForComponent(TSAWorldChangeZone.class)
public class TSAWorldChangeZoneViewModel extends ViewModel<TSAWorldChangeZone> {
    @Override
    public void createMainViewModel() {
        addElement(new Element()
                    .internalName("target")
                    .name("Target entry zone")
                    .type(Element.Type.STRING)
                    .value("")
                    .autoUpdate(true));

        addElement(new Element()
                .internalName("world")
                .name("Target world")
                .type(Element.Type.STRING)
                .value("")
                .autoUpdate(true));
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return new Initializer();
    }

    @Override
    public TSAWorldChangeZone getFromInitializer(Initializer init) {
        return new TSAWorldChangeZone();
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("world"))
            component.setWorld((String) element.value);
        if(element.internalname.equals("target"))
            component.setTarget((String) element.value);
    }

    @Override
    public void updateView(Element element) {
        if(element.internalname.equals("world"))
            element.value = component.getTargetWorld();

        if(element.internalname.equals("target")) {
            element.value = component.getTarget();
        }
    }
}
