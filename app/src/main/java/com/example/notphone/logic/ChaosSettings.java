package com.example.notphone.logic;

import android.content.Context;

public final class ChaosSettings {
    public static final int OFF = 0, MILD = 1, UNHINGED = 2;
    private static final String PREFS = "notphone";
    private static final String KEY_LEVEL = "chaos_level";
    private ChaosSettings() {}

    public static int getLevel(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_LEVEL, MILD);
    }
    public static void setLevel(Context c, int level) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_LEVEL, level).apply();
    }
    public static boolean isOn(Context c) { return getLevel(c) != OFF; }
}
