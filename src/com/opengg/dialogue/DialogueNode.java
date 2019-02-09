package com.opengg.dialogue;

import com.opengg.core.audio.SoundData;
import com.opengg.core.math.Tuple;

import java.util.ArrayList;
import java.util.List;

public class DialogueNode {
    String text;
    boolean hasOpts = false;

    List<Tuple<String, String>> options = new ArrayList<>();

    String triggerTarget;
    String triggerData;

    SoundData sound;
    float volume = 1.0f;

    String anim = "idle";

    String itemSpawn = "";
    int itemAmount = 0;

    String next;

    String quest = "";
    String questState = "";
}
