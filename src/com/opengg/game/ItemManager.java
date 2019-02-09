package com.opengg.game;

import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Tuple;
import com.opengg.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemManager {
    private static final HashMap<String, ItemBuilder> generators = new HashMap<>();

    private static final ArrayList<String> uniqueCreated = new ArrayList<>();

    private final static Pattern nodePattern = Pattern.compile("\\[(.*?)]", Pattern.DOTALL|Pattern.MULTILINE);

    public static void initialize(){
        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/items.txt"));

            var pattern = nodePattern.matcher(data);
            while (pattern.find()){
                var node = pattern.group(1);
                data = pattern.replaceFirst("");
                pattern = nodePattern.matcher(data);
                processItem(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processItem(String item){
        var sections = StringUtil.splitLines(item);

        ItemBuilder builder = new ItemBuilder();
        builder.setName(sections.get("name"));
        builder.setDisplayName(sections.get("displayName"));
        builder.setDesc(sections.get("desc"));

        builder.setUnique(Boolean.parseBoolean(sections.get("unique")));
        builder.setUsable(Boolean.parseBoolean(sections.get("usable")));

        builder.setSprite(sections.get("sprite"));

        generators.put(sections.get("name"), builder);
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
