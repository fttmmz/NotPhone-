package com.example.notphone.ui.system;

public class AppEntry {
    public final String label;
    public final int iconRes;
    public final Class<?> target;   // the screen to open, or null until it exists

    public AppEntry(String label, int iconRes, Class<?> target) {
        this.label = label; this.iconRes = iconRes; this.target = target;
    }
}
