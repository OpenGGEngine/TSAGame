package com.opengg.game;

import java.util.List;
import java.util.Map;

public class Quest {
    public QuestState state = QuestState.NOT_STARTED;
    public String name;
    public String displayName;

    public String desc = "";

    public String firstState;

    public String currentSubQuest;

    public Map<String, SubQuest> subQuests;// = new ArrayList<>();

    public static class SubQuest {
        public QuestState state = QuestState.NOT_STARTED;

        public String name;
        public String displayName;

        public String completionType;
        public String completionValue;

        public List<String> requirements;

        public boolean questEnder = false;
    }

    public enum QuestState{
        NOT_STARTED, ACTIVE, DONE, FAILED
    }
}
