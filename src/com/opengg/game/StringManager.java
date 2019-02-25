package com.opengg.game;

import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.util.StringUtil;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

public class StringManager {
    private static Map<String,String> values;
    private final static Pattern commentPattern = Pattern.compile("//.*");


    public static String getString(String key){
        return values.get(key);
    }

    public static void initialize(){
        try {
            var data = FileStringLoader.loadStringSequence(Resource.getAbsoluteFromLocal("/resources/text/strings.txt"));
            var matcher = commentPattern.matcher(data);
            data = matcher.replaceAll("");

            values = StringUtil.splitLines(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
