package com.opengg.components.viewmodels;

import com.opengg.components.WorldEntryZone;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;

@ForComponent(WorldEntryZone.class)
public class WorldEntryZoneViewModel extends ViewModel<WorldEntryZone> {

    @Override
    public void createMainViewModel() {

    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public WorldEntryZone getFromInitializer(Initializer init) {
        return new WorldEntryZone();
    }

    @Override
    public void onChange(Element element) {

    }

    @Override
    public void updateView(Element element) {

    }

}