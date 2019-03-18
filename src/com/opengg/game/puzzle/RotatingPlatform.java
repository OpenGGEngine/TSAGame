package com.opengg.game.puzzle;

import com.opengg.core.world.components.Component;

import java.util.Arrays;

public class RotatingPlatform extends Component {
    private String[] switches;

    /**
     *
     * @param config Switches checked separated by comma
     */
    public RotatingPlatform(String config){
        switches = config.split(",");
    }
    public void update(float delta){
        if(SwitchComponent.triggered.containsAll(Arrays.asList(switches))){

        }else{

        }
    }
}
