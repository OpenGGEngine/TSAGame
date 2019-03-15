package com.opengg.game.puzzle;

import com.opengg.core.model.Model;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.ModelComponent;

import java.util.Arrays;

public class DoorComponent extends Component {
    private ModelComponent model;
    private String[] switches;

    /**
     *
     * @param m Model of door
     * @param config Switches checked separated by comma
     */
    public DoorComponent(Model m,String config){
        model = new ModelComponent(m);
        this.attach(model);
        switches = config.split(",");
    }
    public void update(float delta){
        if(SwitchComponent.triggered.containsAll(Arrays.asList(switches))){
            //open the door
        }else{
            //Close the door
        }
    }
}
