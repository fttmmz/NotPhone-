package com.example.notphone.ui.system;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notphone.R;
import com.example.notphone.logic.ChaosSettings;
import com.example.notphone.logic.MoodManager;
import com.example.notphone.logic.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends BaseActivity {
    private TextView tvMood;
    private TextView tvGreeting;
    private final Random random = new Random();
    private int lastDodged = -1;   // which icon dodged last time (-1 = none)
    private final List<AppEntry> apps = new ArrayList<>();
    private boolean nokiaMode;
    private IconAdapter adapter;
    private static final String LABEL_PREFIX = "label_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Setup saved the user's interface choice; pick the matching layout
        nokiaMode = getSharedPreferences("notphone", MODE_PRIVATE).getBoolean("interface_nokia", false);
        setContentView(nokiaMode ? R.layout.activity_home_nokia : R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        String name = getSharedPreferences("notphone", MODE_PRIVATE).getString("username", "friend");
        tvGreeting = findViewById(R.id.tvGreeting);
        tvMood = findViewById(R.id.tvMood);

        NotificationHelper.requestPermissionIfNeeded(this);

        // TEMPORARY test: tap the mood text to send a test notification (remove later)
        tvMood.setOnClickListener(v ->
                NotificationHelper.show(this, R.string.system_test_title, R.string.system_test_text));
        // TEMPORARY: long-press the mood text to cycle moods
        tvMood.setOnLongClickListener(v -> {
            MoodManager.setMood(this, (MoodManager.getMood(this) + 1) % 4);
            applyMood();
            return true;   // true = "I used up this long-press"
        });

        buildAppList();
        loadSavedLabels();
        if (nokiaMode) setUpKeypad(); else setUpGrid();
        applyMood();
    }

    private void buildAppList() {
        apps.add(new AppEntry(getString(R.string.system_app_memory), android.R.drawable.ic_menu_edit, null));
        apps.add(new AppEntry(getString(R.string.system_app_choice), android.R.drawable.ic_menu_agenda, null));
        apps.add(new AppEntry(getString(R.string.system_app_time), android.R.drawable.ic_menu_my_calendar, null));
    }

    private void setUpGrid() {
        GridView grid = findViewById(R.id.gvApps);
        adapter = new IconAdapter(this, apps);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener((parent, view, position, id) -> {
            if (position != lastDodged && shouldDodge()) {
                lastDodged = position;                       // this icon dodged, so next tap works
                dodge(view);                                 // "view" is the tapped icon
                Toast.makeText(this, R.string.system_dodge_hint, Toast.LENGTH_SHORT).show();
            } else {
                lastDodged = -1;
                openApp(position);
            }
        });
        grid.setOnItemLongClickListener((parent, view, position, id) -> {
            showRenameDialog(position);
            return true;   // true = "handled", so the normal tap doesn't also fire
        });
    }

    private void setUpKeypad() {
        int[] keyIds = { R.id.key0, R.id.key1, R.id.key2, R.id.key3, R.id.key4,
                R.id.key5, R.id.key6, R.id.key7, R.id.key8, R.id.key9 };
        for (int i = 0; i < keyIds.length; i++) {
            final int number = i;                       // a lambda needs a final copy of i
            findViewById(keyIds[i]).setOnClickListener(v -> pressKey(number));
        }
        View.OnClickListener decoration = v ->
                Toast.makeText(this, R.string.system_key_symbol, Toast.LENGTH_SHORT).show();
        findViewById(R.id.keyStar).setOnClickListener(decoration);
        findViewById(R.id.keyHash).setOnClickListener(decoration);
    }

    private void pressKey(int number) {
        if (number >= 1 && number <= apps.size()) {
            openApp(number - 1);                        // key 1 = first app, key 2 = second...
        } else {
            Toast.makeText(this, getString(R.string.system_key_empty, number), Toast.LENGTH_SHORT).show();
        }
    }

    // Typing a digit on a real or emulator keyboard works like pressing the on-screen key
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (nokiaMode && keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
            pressKey(keyCode - KeyEvent.KEYCODE_0);
            return true;                                // true = "I handled this key"
        }
        return super.onKeyDown(keyCode, event);         // everything else, like Back, behaves normally
    }

    private void openApp(int index) {
        AppEntry app = apps.get(index);
        if (app.target == null) {
            Toast.makeText(this, R.string.system_coming_soon, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, app.target));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvMood.setText(getString(R.string.system_mood_label, MoodManager.getName(this)));
        applyMood();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);   // turns the XML into real menu items
        return true;                                          // true = "yes, show the menu"
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_chaos) {
            showChaosDialog();
            return true;                                      // true = "I handled this tap"
        }
        return super.onOptionsItemSelected(item);
    }

    private void showChaosDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_chaos, null);
        SeekBar seek = view.findViewById(R.id.seekChaos);
        TextView label = view.findViewById(R.id.tvChaosLabel);
        String[] names = getResources().getStringArray(R.array.system_chaos_levels);

        seek.setProgress(ChaosSettings.getLevel(this));       // start at the saved level
        label.setText(names[seek.getProgress()]);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                label.setText(names[progress]);               // label follows the slider live
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        new AlertDialog.Builder(this)
                .setTitle(R.string.system_chaos_title)
                .setView(view)
                .setPositiveButton(R.string.system_save, (dialog, which) -> {
                    ChaosSettings.setLevel(this, seek.getProgress());
                    Toast.makeText(this,
                            getString(R.string.system_chaos_saved, names[seek.getProgress()]),
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.system_cancel, null)
                .show();
    }
    // One colour per mood: Cheerful, Grumpy, Sleepy, Chaotic
    private static final int[] MOOD_COLORS = {0xFFFFF4C2, 0xFFFFC9C9, 0xFFD6E4FF, 0xFFE8D0FF};

    private void applyMood() {
        int mood = MoodManager.getMood(this);
        String name = getSharedPreferences("notphone", MODE_PRIVATE).getString("username", "");
        String[] greetings = getResources().getStringArray(R.array.system_mood_greetings);

        tvGreeting.setText(String.format(greetings[mood], name));
        tvMood.setText(getString(R.string.system_mood_label, MoodManager.getName(this)));
        int background = MOOD_COLORS[mood];
        int chaos = ChaosSettings.getLevel(this);
        if (chaos == ChaosSettings.UNHINGED
                || (chaos == ChaosSettings.MILD && random.nextInt(100) < 30)) {
            // random pastel: any hue (0-359), low saturation, full brightness
            background = Color.HSVToColor(new float[]{random.nextInt(360), 0.25f, 1f});
        }
        findViewById(R.id.main).setBackgroundColor(background);
    }
    private boolean shouldDodge() {
        int chaos = ChaosSettings.getLevel(this);
        int chance = 0;
        if (chaos == ChaosSettings.MILD) chance = 25;
        if (chaos == ChaosSettings.UNHINGED) chance = 60;
        return random.nextInt(100) < chance;
    }

    private void dodge(View icon) {
        ObjectAnimator animator;
        if (random.nextBoolean()) {
            // slide sideways (left or right, 250 pixels)
            float dx = random.nextBoolean() ? 250f : -250f;
            animator = ObjectAnimator.ofFloat(icon, "translationX", 0f, dx);
        } else {
            // blink out
            animator = ObjectAnimator.ofFloat(icon, "alpha", 1f, 0f);
        }
        animator.setDuration(250);
        animator.setRepeatCount(1);                      // play once more...
        animator.setRepeatMode(ValueAnimator.REVERSE);   // ...backwards, so it returns
        animator.start();
    }
    private void loadSavedLabels() {
        SharedPreferences prefs = getSharedPreferences("notphone", MODE_PRIVATE);
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).label = prefs.getString(LABEL_PREFIX + i, apps.get(i).label);
        }
    }
    private String defaultLabel(int position) {
        int[] res = {R.string.system_app_memory, R.string.system_app_choice, R.string.system_app_time};
        return getString(res[position]);
    }

    private void showRenameDialog(int position) {
        View view = getLayoutInflater().inflate(R.layout.dialog_rename, null);
        EditText input = view.findViewById(R.id.etRename);
        input.setText(apps.get(position).label);
        input.setSelectAllOnFocus(true);

        new AlertDialog.Builder(this)
                .setTitle(R.string.system_rename_title)
                .setView(view)
                .setPositiveButton(R.string.system_save, (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    SharedPreferences prefs = getSharedPreferences("notphone", MODE_PRIVATE);
                    if (text.isEmpty()) {
                        prefs.edit().remove(LABEL_PREFIX + position).apply();
                        apps.get(position).label = defaultLabel(position);
                        Toast.makeText(this, R.string.system_restored, Toast.LENGTH_SHORT).show();
                    } else {
                        prefs.edit().putString(LABEL_PREFIX + position, text).apply();
                        apps.get(position).label = text;
                        Toast.makeText(this, R.string.system_renamed, Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();   // tells the grid "redraw yourself"
                })
                .setNegativeButton(R.string.system_cancel, null)
                .show();
    }
}
