package com.opengg.game;

import java.util.*;

public class Inventory {
    Map<String, Integer> items = new LinkedHashMap<>();
    Set<String> abilities = new HashSet<>();

    public Inventory(){

    }

    public Inventory(Map<String, Integer> items){
        this.items.putAll(items);
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public Set<String> getAbilities() {
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

    public String getElementByIndex(int index){
        return (String)items.keySet().toArray()[index];
    }
}
