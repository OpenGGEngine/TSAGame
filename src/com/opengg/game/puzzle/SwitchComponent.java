package com.opengg.game.puzzle;

import com.opengg.core.model.Model;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelComponent;

import java.util.HashSet;

public class SwitchComponent extends Component {
    public static HashSet<String> triggered = new HashSet<>();
    private boolean state = false;
    private ModelComponent model;
    public String name;

    public SwitchComponent(String name,Model m){
        this.name = name;
        model = new ModelComponent(m);
        this.attach(model);
    }
    public void toggle(){
        state = !state;
        if(state) triggered.add(name);
        else triggered.remove(name);
    }
}
