package com.opengg.game;

public class ItemEffect {
    String name;
    String displayName;
    String valueText;

    float value;

    @Override
    public String toString() {
        return "ItemEffect{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", valueText='" + valueText + '\'' +
                ", value=" + value +
                '}';
    }
}
