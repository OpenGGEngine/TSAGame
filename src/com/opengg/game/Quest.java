package com.opengg.game;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    boolean started;
    boolean complete;

    String name;
    String displayName;

    List<QuestState> states = new ArrayList<>();

    class QuestState{
        String name;
        String displayName;

        String activationType;
        String activationValue;
    }
}
