package com.example.notphone.logic;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.notphone.R;
public final class MoodManager {
    public static final int CHEERFUL = 0, GRUMPY = 1, SLEEPY = 2, CHAOTIC = 3;
    private static final String PREFS = "notphone";
    private static final String KEY_MOOD = "mood";
    private MoodManager() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
    public static int getMood(Context c) { return prefs(c).getInt(KEY_MOOD, CHEERFUL); }
    public static void setMood(Context c, int mood) { prefs(c).edit().putInt(KEY_MOOD, mood).apply(); }

    // B and C will call these when things happen in their apps
    public static void onTaskPostponed(Context c) { setMood(c, GRUMPY); }
    public static void onTaskCompleted(Context c) { setMood(c, CHEERFUL); }
    public static void onParadox(Context c)       { setMood(c, CHAOTIC); }

    public static String getName(Context c) {
        return c.getResources().getStringArray(R.array.system_moods)[getMood(c)];
    }
}
