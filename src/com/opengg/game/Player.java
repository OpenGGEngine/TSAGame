package com.opengg.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player {
    public static final Player PLAYER = new Player();

    int health;

    HashMap<String, Integer> items = new HashMap<>();
    List<String> partners = new ArrayList<>();

    public Player(){

    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void addItem(String item, int amount){
        if(!items.containsKey(item))
            items.put(item, amount);
        else
            items.put(item, items.get(item) + amount);

        var itemss = ItemManager.generate(item);
        System.out.println(itemss.desc);
    }

    public int getItemCount(String item){
        return items.get(item);
    }

}
