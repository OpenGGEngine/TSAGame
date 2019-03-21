package com.opengg.components;

import com.opengg.core.world.components.Component;

public class WorldEntryZone extends Component {
    public void spawn() {
        if(getWorld().find("player") != null) getWorld().find("player").setPositionOffset(this.getPosition());
        else getWorld().attach(new PlayerWorldComponent().setPositionOffset(this.getPosition()) );

    }
}
