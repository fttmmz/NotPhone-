package com.example.notphone.ui.system;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notphone.R;
import com.example.notphone.logic.ChaosSettings;
import com.example.notphone.logic.GlitchManager;
import com.example.notphone.logic.MoodManager;

public class HiddenActivity extends BaseActivity {

    // PLACEHOLDERS until B's Repository exists. Later: ask B for real counts.
    private static final int PLACEHOLDER_NOTES = 12;
    private static final int PLACEHOLDER_POSTPONED = 5;

    private TextView[] lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden);

        String name = getSharedPreferences("notphone", MODE_PRIVATE).getString("username", "");
        int mood = MoodManager.getMood(this);
        int chaos = ChaosSettings.getLevel(this);
        String[] chaosNames = getResources().getStringArray(R.array.system_chaos_levels);
        String[] verdicts = getResources().getStringArray(R.array.system_personalities);

        TextView tvName = findViewById(R.id.tvHiddenName);
        TextView tvMood = findViewById(R.id.tvHiddenMood);
        TextView tvChaos = findViewById(R.id.tvHiddenChaos);
        TextView tvStats = findViewById(R.id.tvHiddenStats);
        TextView tvVerdict = findViewById(R.id.tvHiddenVerdict);

        tvName.setText(getString(R.string.system_hidden_name, name));
        tvMood.setText(getString(R.string.system_mood_label, MoodManager.getName(this)));
        tvChaos.setText(getString(R.string.system_hidden_chaos, chaosNames[chaos]));
        tvStats.setText(getString(R.string.system_hidden_stats, PLACEHOLDER_NOTES, PLACEHOLDER_POSTPONED));
        tvVerdict.setText(getString(R.string.system_hidden_verdict, verdicts[mood]));

        lines = new TextView[]{tvName, tvMood, tvChaos, tvStats, tvVerdict};
        glitchAll();

        findViewById(R.id.tvHiddenTitle).setOnClickListener(v -> glitchAll());   // tap the title to glitch again
        findViewById(R.id.btnHiddenBack).setOnClickListener(v -> finish());
        Log.d(TAG, "Opened via: " + getIntent().getData());   // null if opened by the 7-tap, or the link
    }

    private void glitchAll() {
        for (TextView line : lines) GlitchManager.glitch(line);
    }
}