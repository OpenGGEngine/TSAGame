package com.opengg.game;

import com.opengg.core.Configuration;
import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuestManager {
    public static final HashMap<String, Quest> quests = new HashMap<>();

    private final static Pattern nodePattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL|Pattern.MULTILINE);
    private final static Pattern subPattern = Pattern.compile("\\{(.*?)}", Pattern.DOTALL|Pattern.MULTILINE);

    static float counter = 0;

    public static void initialize(){
        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/quests.txt"));

            var pattern = nodePattern.matcher(data);
            while (pattern.find()){
                var node = pattern.group(1);
                data = pattern.replaceFirst("");
                pattern = nodePattern.matcher(data);
                processQuest(node);
            }

            GGConsole.log("Loaded quests: " + quests.keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void beginQuest(String questString){
        GGConsole.log("Beginning quest " + questString);

       var quest = quests.get(questString);
        quest.state = Quest.QuestState.ACTIVE;
        quest.currentSubQuest = quest.firstState;
        quest.subQuests.get(quest.currentSubQuest).state = Quest.QuestState.ACTIVE;
    }

    public static void advanceSubQuest(String questString, String subquestString){
        var quest = quests.get(questString);
        var subquest = quest.subQuests.get(subquestString);

        subquest.state = Quest.QuestState.DONE;

        if(subquest.questEnder){
            quest.state = Quest.QuestState.DONE;
            onQuestComplete(quest);
            return;
        }

        for(var otherSub : quest.subQuests.values()){
            if(otherSub.state == Quest.QuestState.DONE) continue;

            var shouldAdvance = otherSub.requirements.stream()
                    .map(s -> quest.subQuests.get(s))
                    .allMatch(s -> s.state == Quest.QuestState.DONE);

            if(shouldAdvance){
                quest.currentSubQuest = otherSub.name;  //find new subquest
                otherSub.state = Quest.QuestState.ACTIVE;
                return;
            }
        }

        quest.state = Quest.QuestState.DONE; //if no new subquests, automatically complete
        onQuestComplete(quest);
    }

    public static void update(float delta){
        counter += delta;

        if(counter > Configuration.getFloat("questUpdateRate")/1000f){
            counter = 0;

            quests.values().forEach(QuestManager::updateQuest);
        }
    }

    private static void updateQuest(Quest quest) {
        if(quest.state == Quest.QuestState.NOT_STARTED) return;
        if(quest.state == Quest.QuestState.DONE) return;
        if(quest.state == Quest.QuestState.FAILED) return;

        var sub = quest.subQuests.get(quest.currentSubQuest);

        if(sub.completionType.equals("auto")) return;

        if(sub.completionType.equals("item")){
            var sections = sub.completionValue.split(":");

            var item = sections[0];
            var amount = Integer.parseInt(sections[1]);

            if(Player.PLAYER.getItems().containsKey(item) && Player.PLAYER.getItems().get(item) >= amount){
                advanceSubQuest(quest.name, sub.name);
            }
        }
    }

    private static void onQuestComplete(Quest quest){
        System.out.println(quest.name + "   " + quest.displayName);
    }

    private static void processQuest(String node) {
        List<String> subQuests = new ArrayList<>();

        var pattern = subPattern.matcher(node);
        while (pattern.find()){
            var subNode = pattern.group(1);
            node = pattern.replaceFirst("");
            pattern = subPattern.matcher(node);
            subQuests.add(subNode);
        }

        var sections = StringUtil.splitLines(node);
        
        Quest quest = new Quest();
        quest.displayName = sections.get("displayName");
        quest.name = sections.get("name");
        quest.firstState = sections.get("firstState");
        quest.currentSubQuest = quest.firstState;

        quest.subQuests = subQuests.stream().map(QuestManager::getState).collect(Collectors.toMap(s -> s.name, s -> s));

        quests.put(quest.name, quest);
    }

    private static Quest.SubQuest getState(String questState){
        var sections = StringUtil.splitLines(questState);

        Quest.SubQuest state = new Quest.SubQuest();
        state.name = sections.get("name");
        state.displayName = sections.get("displayName");
        state.requirements = List.of(sections.getOrDefault("requirements", "").split(":"));
        state.completionType = sections.getOrDefault("completionType", "auto");
        state.completionValue = sections.getOrDefault("completionValue", "");

        return state;
    }

    public static Map<String, Quest> getQuests(){
        return quests;
    }
}
