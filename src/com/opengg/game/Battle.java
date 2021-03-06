package com.opengg.game;

import com.opengg.components.SpriteRenderComponent;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.*;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.util.ArrayUtil;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.CameraComponent;
import com.opengg.gui.GameMenu;

import java.awt.Color;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Battle implements KeyboardListener {
    String FONT = "Dosis-SemiBold.ttf";

    BattleInfo info;

    GUI battleGUI = new GUI();
    GUIGroup battleMenu;
    GUIGroup itemMenu;
    GUIGroup abilityMenu;
    GUIGroup generalMenu;
    GUIGroup enemyMenu;
    GUIGroup allyMenu;
    GUITextBox infoBox;
    GUITexture gameOver = new GUITexture(Resource.getTexture("gameover.png"),new Vector2f(0,0),new Vector2f(1));
    GUIButton yes = new GUIButton(new Vector2f(0.15f,0.2f),new Vector2f(0.3f,0.1f),Resource.getTexture("trans.png"));
    GUIButton no = new GUIButton(new Vector2f(0.6f,0.2f),new Vector2f(0.3f,0.1f),Resource.getTexture("trans.png"));
    GUIGroup infoMenu;

    Item selectedItem;

    ItemEffect i = new ItemEffect();

    boolean freeze = false;


    Item poison = new Item("poison", "poison", "", true, true, true,Item.ItemType.ABILITY, List.of(i), "");

    Runnable onComplete = () -> {};

    HashMap<String, SpriteRenderComponent> fighterRenderers = new HashMap<>();
    HashMap<String, HashSet<String>> statusEffects = new HashMap<>();
    Queue<Runnable> hackyGarbage = new LinkedList<>();

    public Battle(BattleInfo info){
        this.info = info; i.value = 3;i.name="damage";
        System.out.println(info.allies);
        System.out.println(info.enemies);
    }

    public void start(){
        yes.setOnClick(()->{Player.PLAYER.setHealth((((float)Player.PLAYER.getMaxHealth())/4) * 3);BattleManager.end(false);});
        no.setOnClick(()->{System.exit(0);});
        GameMenu.dialogDisable = true;
        GameMenu.setEnabled(false);
        KeyboardController.addKeyboardListener(this);
        info.allies.add("player0");
        statusEffects.put("player0",new HashSet<>());
        battleGUI.addItem("gameOver",gameOver.setLayer(-0.5f));
        battleGUI.addItem("yes",yes);
        battleGUI.addItem("no",no);
        gameOver.setEnabled(false);
        yes.setEnabled(false);
        no.setEnabled(false);

        for(var ally : info.allies){
            var renderer = new SpriteRenderComponent(CharacterManager.getExisting(ally).getSprite());
            WorldEngine.getCurrent().attach(renderer.setPositionOffset(new Vector3f(-2, 0, -3)).setScaleOffset(CharacterManager.getExisting(ally).getSize()));
            fighterRenderers.put(ally, renderer);
        }

        for(var enemy : info.enemies){
            var renderer = new SpriteRenderComponent(CharacterManager.getExisting(enemy).getSprite());
            renderer.setAngle(180);
            WorldEngine.getCurrent().attach(renderer.setPositionOffset(new Vector3f(3, 0, -6)).setScaleOffset(CharacterManager.getExisting(enemy).getSize()));
            fighterRenderers.put(enemy, renderer);
            statusEffects.put(enemy,new HashSet<>());
        }

        WorldEngine.getCurrent().attach(new CameraComponent().setPositionOffset(new Vector3f(0,2,0)).setRotationOffset(new Quaternionf(new Vector3f(15,0,0))));

        GUIController.addAndUse(battleGUI, "battle");
        battleGUI.addItem("menu", battleMenu = new GUIGroup(new Vector2f(0.03f, 0.6f)));
        battleGUI.addItem("info", infoMenu = new GUIGroup(new Vector2f(0.28f, 0.8f)));
        battleGUI.addItem("enemies", enemyMenu = new GUIGroup(new Vector2f(0.8f, 0.7f)));
        battleGUI.addItem("allies", allyMenu = new GUIGroup(new Vector2f(0.05f, 0.05f)));

        infoMenu.addItem("background",
                new GUITexture(Texture.ofColor(Color.GRAY, 0.8f), new Vector2f(-0.015f,-0.02f), new Vector2f(0.4f, 0.2f)).setLayer(-0.5f));
        infoMenu.setEnabled(false);

        enemyMenu.addItem("sublist", new GUIGroup());
        allyMenu.addItem("sublist", new GUIGroup());

        battleMenu.addItem("background",
                new GUITexture(Texture.ofColor(Color.GRAY, 0.8f), new Vector2f(-0.015f,-0.02f), new Vector2f(0.22f, 0.4f)).setLayer(-0.5f));

        battleMenu.addItem("item", itemMenu = new GUIGroup());
        battleMenu.addItem("ability", abilityMenu = new GUIGroup());
        battleMenu.addItem("general", generalMenu = new GUIGroup());

        itemMenu.addItem("sublist", new GUIGroup());
        itemMenu.addItem("back", new GUIButton(new Vector2f(0,0), new Vector2f(0.05f,0.05f),  Texture.ofColor(Color.BLUE), () -> enableSubMenu(generalMenu)));

        abilityMenu.addItem("sublist", new GUIGroup());
        abilityMenu.addItem("back", new GUIButton(new Vector2f(0,0), new Vector2f(0.05f,0.05f),  Texture.ofColor(Color.BLUE), () -> enableSubMenu(generalMenu)));

        infoBox = new GUITextBox(new Vector2f(0.25f,0), new Vector2f(0.5f,0.2f))
                .setText("")
                .setBackground(Texture.ofColor(Color.BLACK, 0.5f))
                .setSpeed(1/60f)
                .setTextSize(0.12f)
                .setMargin(0.03f)
                .setScrollMode(GUITextBox.ScrollMode.SPEEDABLE_SCROLL)
                .setFont(Resource.getTruetypeFont("consolas.ttf"));
        battleGUI.addItem("text", infoBox);


        var run = new GUIGroup();
        run.addItem("select", new GUIButton(new Vector2f(0.07f,0.025f), () -> end(false)));
        run.addItem("text", new GUIText(Text.from("Escape").size(0.15f).kerning(true), Resource.getTruetypeFont(FONT)));
        generalMenu.addItem("run", run);

        var items = new GUIGroup(new Vector2f(0, 0.04f));
        items.addItem("select", new GUIButton(new Vector2f(0.07f,0.025f), () -> enableSubMenu(itemMenu)));
        items.addItem("text", new GUIText(Text.from("Items").size(0.15f).kerning(true), Resource.getTruetypeFont(FONT)));
        generalMenu.addItem("items", items);
        //zooooweeeeemamaaaaaaaaa

        var abilities = new GUIGroup(new Vector2f(0,0.08f));
        abilities.addItem("select", new GUIButton(new Vector2f(0.1f,0.025f), () -> enableSubMenu(abilityMenu)));
        abilities.addItem("text", new GUIText(Text.from("Abilities").size(0.15f).kerning(true), Resource.getTruetypeFont(FONT)));
        generalMenu.addItem("abilityMenu", abilities);

        battleMenu.setEnabled(false);
        setText("You have encountered a " + CharacterManager.getExisting((String) info.enemies.toArray()[0]).getDisplayName() + "!", () -> {
        });
        freeze = false;
        hackyGarbage.add(() -> {infoBox.setText("What is your next move?");
            battleMenu.setEnabled(true);
            enableSubMenu(generalMenu);
            updateSubMenus();
        });


    }


    public void enableSubMenu(GUIGroup menu){
        for(var sub : List.of(itemMenu, abilityMenu, generalMenu)){
            if(menu == sub) sub.setEnabled(true);
            else  sub.setEnabled(false);
        }
        infoMenu.setEnabled(false);
        selectedItem = null;
    }

    public void updateSubMenus(){
        enableSubMenu(generalMenu);

        var items = Player.PLAYER.getInventory().getItems().entrySet()
                .stream()
                .map(e -> Tuple.of(ItemManager.generate(e.getKey()), e.getValue()))
                .filter(e -> e.y > 0)
                .filter(e -> e.x.usable)
                .collect(Collectors.toList());

        var abilities = Player.PLAYER.getInventory().getAbilities()
                .stream()
                .map(e -> Tuple.of(ItemManager.generate(e), -1))
                .filter(e -> e.x.usable)
                .collect(Collectors.toList());

        enemyMenu.addItem("background",
                new GUITexture(Texture.ofColor(Color.GRAY, 0.8f),
                        new Vector2f(-0.015f,-0.02f), new Vector2f(0.3f, 0.12f * info.enemies.size())).setLayer(-0.5f));

        allyMenu.addItem("background",
                new GUITexture(Texture.ofColor(Color.GRAY, 0.8f),
                        new Vector2f(-0.015f,-0.02f), new Vector2f(0.13f, 0.12f * info.enemies.size())).setLayer(-0.5f));

        populateSubMenu(itemMenu, items);
        populateSubMenu(abilityMenu, abilities);

        updateEnemies();
        updateAllies();
    }

    public void updateEnemies(){
        var list = (GUIGroup) enemyMenu.getItem("sublist");
        list.clear();

        int counter = 0;
        for(var enemy : info.enemies){
            var realEnemy = CharacterManager.getExisting(enemy);

            var holder = new GUIGroup(new Vector2f(0, 0.05f * counter));

            holder.addItem("name",
                    new GUIText(Text.from(realEnemy.getDisplayName()).size(0.1f), Resource.getTruetypeFont(FONT), new Vector2f(0,0.03f)));

            var string = "HP: " + realEnemy.getHealth() + "/" + realEnemy.getMaxHealth();
            /*holder.addItem("data",
                    new GUIText(Text.from(string).size(0.04f), Resource.getTruetypeFont(FONT), new Vector2f(0,0.0f)));*/

            holder.addItem("bar",
                    new GUIProgressBar(new Vector2f(0,0), new Vector2f(0.1f,0.01f), Color.red, Color.black).setPercent(realEnemy.getHealth()/realEnemy.getMaxHealth()));

            holder.addItem("button",
                    new GUIButton(new Vector2f(-0.005f,-0.005f),
                    new Vector2f(0.1f,0.06f), () -> playerAttack( realEnemy, selectedItem)));

            list.addItem(realEnemy.getName(), holder);

        }
    }

    public void updateAllies(){
        var list = (GUIGroup) allyMenu.getItem("sublist");
        list.clear();

        int counter = 0;
        for(var ally : info.allies){
            var realAlly = CharacterManager.getExisting(ally);

            var holder = new GUIGroup(new Vector2f(0, 0.05f * counter));

            holder.addItem("name",
                    new GUIText(Text.from(realAlly.getDisplayName()).size(0.1f), Resource.getTruetypeFont(FONT), new Vector2f(0,0.03f)));

            var string = "HP: " + realAlly.getHealth() + "/" + realAlly.getMaxHealth();
            /*holder.addItem("data",
                    new GUIText(Text.from(string).size(0.04f), Resource.getTruetypeFont(FONT), new Vector2f(0,0.0f)));*/

            holder.addItem("bar",
                    new GUIProgressBar(new Vector2f(0,0), new Vector2f(0.1f,0.01f), Color.red, Color.black).setPercent(realAlly.getHealth()/realAlly.getMaxHealth()));

            holder.addItem("button",
                    new GUIButton(new Vector2f(-0.005f,-0.005f),
                            new Vector2f(0.1f,0.06f), () -> playerAttack( realAlly, selectedItem)));

            list.addItem(realAlly.getName(), holder);

        }
    }

    public void populateSubMenu(GUIGroup menu, List<Tuple<Item, Integer>> items){
        var list = (GUIGroup) menu.getItem("sublist");
        list.clear();

        int counter = 0;
        for(var item : items){

            var holder = new GUIGroup(new Vector2f(0, 0.35f - (counter * 0.03f)));

            String value = item.x.displayName + (item.x.type == Item.ItemType.ITEM ? " x " + item.y : "");

            holder.addItem("name", new GUIText(Text.from(value).size(0.1f), Resource.getTruetypeFont(FONT)));
            if(item.x.targeted) holder.addItem("button", new GUIButton(new Vector2f(-0.01f,-0.01f), new Vector2f(0.12f,0.025f), () -> selectItem(item.x)));
            else holder.addItem("button", new GUIButton(new Vector2f(-0.01f,-0.01f), new Vector2f(0.12f,0.025f), () -> {
                if(selectedItem == item.x)
                    playerAttack( null, selectedItem);
                else
                    selectItem(item.x);
            }));
            list.addItem(item.x.name, holder);
            counter++;
        }
    }

    public void selectItem(Item item){
        infoMenu.setEnabled(true);
        infoMenu.addItem("desc", new GUIText(Text.from(item.desc).size(0.1f), Resource.getTruetypeFont(FONT), new Vector2f(0.001f,0.14f)));

        selectedItem = item;

        var string = item.effects.stream()
                .map(e -> e.displayName + ": " + e.valueText)
                .map(s -> " - " + s + "\n")
                .collect(Collectors.joining());

        infoMenu.addItem("effects",  new GUIText(Text.from(string).size(0.08f), Resource.getTruetypeFont(FONT), new Vector2f(0.001f,0.08f)));
    }

    private void runAIs() {
        for(var ally : info.allies){
            if(ally.equals("player0")) continue;
            processAI(ally, true);
        }

        for(var enemy : info.enemies){
            processAI(enemy, false);
        }

    }
    public void runStatusEffects(){
        if(statusEffects.isEmpty()){
            infoBox.setText("What is your next move?");
            battleMenu.setEnabled(true);
            updateSubMenus();
        }

        for(var entry:statusEffects.entrySet()){
            var source =CharacterManager.getExisting(entry.getKey());
            for(var effect:entry.getValue()){
                if(effect.equals("poison")){
                    hackyGarbage.add(()->{
                        freeze = false;attack(source,source,poison);
                    setText(source.getDisplayName() + " is poisoned.", () -> {
                    })
                    ;});
                 
                }
            }

        }
        hackyGarbage.add(()->{
            infoBox.setText("What is your next move?");
            battleMenu.setEnabled(true);
            enableSubMenu(generalMenu);
            updateSubMenus();});


    }

    private void processAI(String character, boolean ally){
        var target = ArrayUtil.getRandom(ally ? info.enemies : info.allies);
        var inventory = CharacterManager.getExisting(character).getInventory();

        var items = (HashMap<String, Integer>) ((HashMap<String, Integer>) inventory.items).clone();

        items.putAll(
                inventory.abilities.stream()
                    .collect(Collectors.toMap(s -> s, s -> -1))
        );

        if(items.isEmpty()){
            freeze = false;
            setText(CharacterManager.getExisting(character).getDisplayName() + " has no attacks remaining!", () -> {
                updateSubMenus();
            });
            updateSubMenus();
            hackyGarbage.add(()->{runStatusEffects();});
            return;
        }

        var attack = ArrayUtil.getRandom(items.keySet());

        if(ItemManager.generate(attack).targeted){
            attack(CharacterManager.getExisting(character), CharacterManager.getExisting(target), ItemManager.generate(attack));
        }else{
            attack(CharacterManager.getExisting(character), null, ItemManager.generate(attack));
        }
        fighterRenderers.get((String)info.enemies.toArray()[0]).setAnimToUse("attack");

        freeze = false;
        setText(CharacterManager.getExisting(character).getDisplayName() + " used " + ItemManager.generate(attack).displayName + "!", () -> {
        });
        updateSubMenus();
        hackyGarbage.add(()->{
            fighterRenderers.get((String)info.enemies.toArray()[0]).setAnimToUse("idle");
            runStatusEffects();});

    }

    public void playerAttack(Character enemy, Item attack){
        if(attack == null) return;

        attack(Player.PLAYER, enemy, attack);
        if(info.enemies.isEmpty()){
            return;
        }
        battleMenu.setEnabled(false);
        updateSubMenus();
        freeze = false;
        fighterRenderers.get((String)info.allies.toArray()[0]).setAnimToUse("attack");
        setText("You used " + attack.displayName + "!", () -> {

        });
        hackyGarbage.add(()->{
            fighterRenderers.get((String)info.allies.toArray()[0]).setAnimToUse("idle");
            runAIs();});

    }

    public void attack(Character source, Character enemy, Item attack){
        var targeted = attack.targeted;

        for(var effect : attack.effects){
            switch (effect.name){
                case "damage":
                    if(targeted)
                        if(source.equals(Player.PLAYER)){
                            damage(enemy,effect.value*(1+((float)Player.PLAYER.level/12)));
                        }else {
                            damage(enemy, effect.value);
                        }
                    else
                        info.enemies.forEach(e -> damage(enemy, effect.value));
                    break;
                case "heal":
                    if(targeted)
                        heal(source, effect.value);
                    else
                        info.allies.forEach(a -> heal(CharacterManager.getExisting(a), effect.value));
                    break;
                case "poison":
                    if(statusEffects.containsKey(enemy.getId()))
                    statusEffects.get(enemy.getId()).add("poison");
                    return;

            }
        }

        if(selectedItem != null && selectedItem.type == Item.ItemType.ITEM){
            if(source.getInventory().getItemCount(selectedItem.name) > 0)
            source.getInventory().addItem(selectedItem.name, -1);
        }

    }

    private void heal(Character source, float amount){
        source.setHealth(Math.min(source.getHealth() + amount, source.getMaxHealth()));
    }

    private void damage(Character target, float amount){

        target.setHealth(target.getHealth() - amount);
        if(target.getHealth() <= 0){
            target.setLiving(false);
            if(info.enemies.contains(target.getId())){
                info.enemies.remove(target.getId());
                if(info.enemies.isEmpty()){
                    hackyGarbage.add(()->{end(true);});
                }
            }else{
                if(target.getName().equals("player")){
                    hackyGarbage.add(()->{end(false);});
                }else{
                    info.allies.remove(target.getId());
                }
            }
        }
    }

    public void setText(String text, Runnable onAdvance){
        infoBox.reset();
        infoBox.setText(text);

        onComplete = onAdvance;
    }

    public void end(boolean success){
        battleMenu.setEnabled(false);
        updateSubMenus();
        statusEffects.clear();
        if(success){
            Player.PLAYER.exp += 2;
            int diff = Player.PLAYER.exp - Player.PLAYER.expNextLevel;
            String levelup = "";
            if(diff>=0){
                Player.PLAYER.level++;
                Player.PLAYER.exp = diff;
                Player.PLAYER.expNextLevel = (int)(Math.pow(2.714f,Player.PLAYER.level/20)*10);
                levelup+="You are now at level "+Player.PLAYER.level+"\n";
            }
            freeze = false;
            setText(levelup+"You have defeated the enemy. \n You acquired 10 gold!", () -> BattleManager.end(true));
            hackyGarbage.add(() -> BattleManager.end(true));
        }else{
            freeze = false;
            setText("You have failed to defeat " + CharacterManager.getExisting((String) info.enemies.toArray()[0]).getDisplayName(), () -> BattleManager.end(false));
            if(Player.PLAYER.getHealth()>0) {
                hackyGarbage.add(() -> {BattleManager.end(false);});
            }else {
                hackyGarbage.clear();
                hackyGarbage.add(() -> {gameOver.setEnabled(true);
                    yes.setEnabled(true);
                    no.setEnabled(true);
                    battleMenu.setEnabled(false);
                    enemyMenu.setEnabled(false);
                    });
            }
        }

    }

    public void update(){
        if(freeze){
            if(!hackyGarbage.isEmpty()) {
                hackyGarbage.poll().run();
            }
        }
    }


    @Override
    public void keyPressed(int key) {
        if(key == Key.KEY_SPACE){
            if(!infoBox.isComplete()){
                infoBox.forceComplete();
            }else{
                freeze=true;
            }
        }
    }

    @Override
    public void keyReleased(int key) {

    }
}
