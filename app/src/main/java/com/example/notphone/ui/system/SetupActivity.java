package com.example.notphone.ui.system;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.notphone.R;

public class SetupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (getSharedPreferences("notphone", MODE_PRIVATE).getBoolean("setup_done", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_setup);
        Spinner spMood = findViewById(R.id.spMood);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.system_moods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMood.setAdapter(adapter);

        EditText etUsername = findViewById(R.id.etUsername);
        findViewById(R.id.btnStart).setOnClickListener(v -> {
            String name = etUsername.getText().toString().trim();
            if (name.isEmpty()) {
                etUsername.setError(getString(R.string.system_error_no_name));
                return;                               // stop here, nothing saved
            }
            // Member B should replace this
            getSharedPreferences("notphone", MODE_PRIVATE).edit()
                    .putString("username", name)
                    .putBoolean("setup_done", true)
                    .apply();
            startActivity(new Intent(this, HomeActivity.class));
            finish();                                 // Back will not return to Setup
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}