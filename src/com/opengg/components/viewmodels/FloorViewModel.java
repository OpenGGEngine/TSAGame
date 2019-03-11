package com.opengg.components.viewmodels;

import com.opengg.components.FloorComponent;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.components.viewmodel.Element;
import com.opengg.core.world.components.viewmodel.ForComponent;
import com.opengg.core.world.components.viewmodel.Initializer;
import com.opengg.core.world.components.viewmodel.ViewModel;

@ForComponent(FloorComponent.class)
public class FloorViewModel extends ViewModel<FloorComponent> {

    @Override
    public void createMainViewModel() {
        Element blot = new Element();
        blot.type = Element.Type.TEXTURE;
        blot.name = "Blotmap";
        blot.value = TextureManager.getDefault();
        blot.internalname = "blot";

        Element t1 = new Element();
        t1.type = Element.Type.TEXTURE;
        t1.name = "Texture 1";
        t1.value = TextureManager.getDefault();
        t1.internalname = "t1";

        Element t2 = new Element();
        t2.type = Element.Type.TEXTURE;
        t2.name = "Texture 2";
        t2.value = TextureManager.getDefault();
        t2.internalname = "t2";

        Element t3 = new Element();
        t3.type = Element.Type.TEXTURE;
        t3.name = "Texture 3";
        t3.value = TextureManager.getDefault();
        t3.internalname = "t3";

        Element t4 = new Element();
        t4.type = Element.Type.TEXTURE;
        t4.name = "Texture 4";
        t4.value = TextureManager.getDefault();
        t4.internalname = "t4";

        getElements().add(blot);
        getElements().add(t1);
        getElements().add(t2);
        getElements().add(t3);
        getElements().add(t4);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public FloorComponent getFromInitializer(Initializer init) {
        FloorComponent comp = new FloorComponent();

        return comp;
    }

    @Override
    public void onChange(Element element) {
        if(element.internalname.equals("blot"))
            component.setBlotmap((TextureData)element.value);

        else if(element.internalname.startsWith("t"))
            OpenGG.asyncExec(() -> component.setArray((TextureData) getByName("t1").value, (TextureData) getByName("t2").value, (TextureData) getByName("t3").value, (TextureData) getByName("t4").value));
    }

    @Override
    public void updateView(Element element) {
        if(element.name.equals("blot")){
            element.value = component.getBlotmap().getData().get(0);
        }
        if(element.name.equals("t1")){
            element.value = component.getArray().getData().get(0);
        }
        if(element.name.equals("t2")){
            element.value = component.getArray().getData().get(1);
        }
        if(element.name.equals("t3")){
            element.value = component.getArray().getData().get(2);
        }
        if(element.name.equals("t4")){
            element.value = component.getArray().getData().get(3);
        }
    }
}
