package com.opengg.dialogue;

import com.opengg.core.math.Tuple;

import java.util.ArrayList;
import java.util.List;

public class DialogueNode {
    String text;
    boolean hasOpts = false;

    List<Tuple<String, String>> options = new ArrayList<>();

    String triggerTarget;
    String triggerData;

    String next;
}
