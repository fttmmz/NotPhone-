package com.example.notphone.ui.system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notphone.R;
import com.example.notphone.logic.MoodManager;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {
    private TextView tvMood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String name = getSharedPreferences("notphone", MODE_PRIVATE).getString("username", "friend");
        ((TextView) findViewById(R.id.tvGreeting)).setText(getString(R.string.system_greeting, name));
        tvMood = findViewById(R.id.tvMood);
        List<AppEntry> apps = new ArrayList<>();
        apps.add(new AppEntry(getString(R.string.system_app_memory), android.R.drawable.ic_menu_edit, null));
        apps.add(new AppEntry(getString(R.string.system_app_choice), android.R.drawable.ic_menu_agenda, null));
        apps.add(new AppEntry(getString(R.string.system_app_time), android.R.drawable.ic_menu_my_calendar, null));

        GridView grid = findViewById(R.id.gvApps);
        grid.setAdapter(new IconAdapter(this, apps));
        grid.setOnItemClickListener((parent, view, position, id) -> {
            AppEntry app = apps.get(position);
            if (app.target == null) {
                Toast.makeText(this, R.string.system_coming_soon, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, app.target));
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        tvMood.setText(getString(R.string.system_mood_label, MoodManager.getName(this)));
    }
}
