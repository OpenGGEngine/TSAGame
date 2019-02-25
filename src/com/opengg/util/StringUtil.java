package com.opengg.util;

import com.opengg.core.math.Tuple;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtil {
    public static Map<String, String> splitLines(String node){
        var lines = node.split(";");
        var sections = Arrays.stream(lines)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> Tuple.of(s.substring(0,s.indexOf("=")), s.substring(s.indexOf("=") + 1)))
                .map(s -> Tuple.of(s.x.trim(), s.y.trim()))
                .collect(Collectors.toMap(s -> s.x, s -> s.y));
        return sections;
    }

    public static List<String> splitByPattern(String data, Pattern nodePattern){

        var list = new ArrayList<String>();

        var pattern = nodePattern.matcher(data);
        while (pattern.find()){
            var node = pattern.group(1);
            data = pattern.replaceFirst("");
            pattern = nodePattern.matcher(data);
            list.add(node);
        }

        return list;
    }
}
