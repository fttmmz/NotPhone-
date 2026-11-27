package com.example.notphone.logic;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import com.example.notphone.R;

import java.util.Random;
import android.app.Activity;
import android.content.Intent;


public final class GlitchManager {
    private GlitchManager() {}
    private static final Random RANDOM = new Random();
    // Makes a view shake, tilt and flicker for 0.4 seconds, then return to normal.
    public static void glitch(View view) {
        if (ChaosSettings.getLevel(view.getContext()) == ChaosSettings.OFF) return;  // respect the setting

        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 15f, -15f, 0f);
        ObjectAnimator tilt = ObjectAnimator.ofFloat(view, "rotation", 0f, 3f, -3f, 0f);
        ObjectAnimator flicker = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.3f, 1f, 0.5f, 1f);




        AnimatorSet set = new AnimatorSet();
        set.playTogether(shake, tilt, flicker);   // all three at the same time
        set.setDuration(400);
        set.start();

    }

    public static void startActivity(Activity from, Intent intent) {
        from.startActivity(intent);
        int chaos = ChaosSettings.getLevel(from);
        if (chaos == ChaosSettings.OFF) return;               // normal transition
        if (chaos == ChaosSettings.UNHINGED && RANDOM.nextBoolean()) {
            from.overridePendingTransition(R.anim.spin_in, R.anim.spin_out);
        } else {
            from.overridePendingTransition(R.anim.glitch_in, R.anim.glitch_out);
        }
    }
}
