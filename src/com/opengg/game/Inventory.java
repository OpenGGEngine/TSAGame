package com.opengg.game;

import java.util.HashMap;
import java.util.HashSet;

public class Inventory {
    HashMap<String, Integer> items = new HashMap<>();
    HashSet<String> abilities = new HashSet<>();


    public HashMap<String, Integer> getItems() {
        return items;
    }

    public HashSet<String> getAbilities() {
        return abilities;
    }

    public void addItem(String item, int amount){
        if(ItemManager.generate(item).type == Item.ItemType.ABILITY) addAbility(item);
        if(!items.containsKey(item))
            items.put(item, amount);
        else
            items.put(item, items.get(item) + amount);

        //var itemss = ItemManager.generate(item);
    }

    public void addAbility(String ability){
        if(ItemManager.generate(ability).type == Item.ItemType.ITEM) addItem(ability,1);
        abilities.add(ability);
    }

    public int getItemCount(String item){
        return items.get(item);
    }
}
