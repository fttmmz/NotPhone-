package com.example.notphone.ui.system;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class HomeActivity extends BaseActivity {
    private TextView tvMood;
    private final List<AppEntry> apps = new ArrayList<>();
    private boolean nokiaMode;

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
        ((TextView) findViewById(R.id.tvGreeting)).setText(getString(R.string.system_greeting, name));
        tvMood = findViewById(R.id.tvMood);
        NotificationHelper.requestPermissionIfNeeded(this);

// TEMPORARY test: tap the mood text to send a test notification (remove later)
        tvMood.setOnClickListener(v ->
                NotificationHelper.show(this, R.string.system_test_title, R.string.system_test_text));

        buildAppList();
        if (nokiaMode) setUpKeypad(); else setUpGrid();
    }

    private void buildAppList() {
        apps.add(new AppEntry(getString(R.string.system_app_memory), android.R.drawable.ic_menu_edit, null));
        apps.add(new AppEntry(getString(R.string.system_app_choice), android.R.drawable.ic_menu_agenda, null));
        apps.add(new AppEntry(getString(R.string.system_app_time), android.R.drawable.ic_menu_my_calendar, null));
    }

    private void setUpGrid() {
        GridView grid = findViewById(R.id.gvApps);
        grid.setAdapter(new IconAdapter(this, apps));
        grid.setOnItemClickListener((parent, view, position, id) -> openApp(position));
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
}
