package com.opengg.game;

import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Tuple;
import com.opengg.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemManager {
    private static final HashMap<String, ItemBuilder> generators = new HashMap<>();

    private static final ArrayList<String> uniqueCreated = new ArrayList<>();

    private final static Pattern nodePattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL|Pattern.MULTILINE);
    private final static Pattern subPattern = Pattern.compile("\\{(.*?)}", Pattern.DOTALL|Pattern.MULTILINE);


    public static void initialize(){
        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/items.txt"));

            StringUtil.splitByPattern(data, nodePattern).forEach(ItemManager::processItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processItem(String item){
        List<ItemEffect> effects = StringUtil.splitByPattern(item, subPattern).stream()
                .map(ItemManager::processEffect)
                .collect(Collectors.toList());

        item = subPattern.matcher(item).replaceAll("");

        ItemBuilder builder = new ItemBuilder();

        var sections = StringUtil.splitLines(item);

        builder.setName(sections.get("name"));
        builder.setDisplayName(sections.get("displayName"));
        builder.setDesc(sections.get("desc"));

        builder.setUnique(Boolean.parseBoolean(sections.getOrDefault("unique", "false")));
        builder.setUsable(Boolean.parseBoolean(sections.get("usable")));
        builder.setTargeted(Boolean.parseBoolean(sections.getOrDefault("targeted", "false")));

        builder.setSprite(sections.get("sprite"));

        builder.setType(Item.ItemType.valueOf(sections.get("type").toUpperCase()));

        builder.setEffects(effects);


        generators.put(sections.get("name"), builder);
    }

    private static ItemEffect processEffect(String effectString){
        var sections = StringUtil.splitLines(effectString);
        ItemEffect effect = new ItemEffect();
        effect.name = sections.get("name");
        effect.value = Float.valueOf(sections.getOrDefault("value", "0"));
        effect.displayName = StringManager.getString(effect.name + "Display");
        effect.valueText = StringManager.getString(effect.name + "Value").replace("VAL", Float.toString(effect.value));

        return effect;
    }

    public static Item generate(String name){
        var builder = generators.get(name);
        /*if(builder.isUnique()){
            if(uniqueCreated.contains(name)){
                return null;
            }else{
                uniqueCreated.add(name);
            }
        }*/

        return builder.createItem();
    }

    public static boolean hasBeenCreated(String name){
        return uniqueCreated.contains(name);
    }
}
