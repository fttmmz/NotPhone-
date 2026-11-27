package com.example.notphone.logic;

public final class Keys {
    private Keys() {}   // nobody should ever create a Keys object

    // Names for data passed between screens (package prefix avoids clashes)
    public static final String EXTRA_NOTE_ID      = "com.example.notphone.NOTE_ID";
    public static final String EXTRA_TASK_TITLE   = "com.example.notphone.TASK_TITLE";
    public static final String EXTRA_EVENT_START  = "com.example.notphone.EVENT_START";
    public static final String EXTRA_PREFILL_TEXT = "com.example.notphone.PREFILL_TEXT";

    // Numbers that say which screen is answering (startActivityForResult)
    public static final int REQ_ROULETTE   = 100;
    public static final int REQ_PROOF      = 101;
    public static final int REQ_ALARM      = 102;
    public static final int REQ_PREP_TASKS = 103;
}