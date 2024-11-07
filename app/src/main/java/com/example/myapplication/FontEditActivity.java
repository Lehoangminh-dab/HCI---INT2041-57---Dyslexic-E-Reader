package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FontEditActivity extends AppCompatActivity {
    private Spinner fontSpinner;
    private SeekBar sizeSeekBar, letterSpacingSeekBar, lineSpacingSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_edit);

        fontSpinner = findViewById(R.id.font_spinner);
        sizeSeekBar = findViewById(R.id.size_seekbar);
        letterSpacingSeekBar = findViewById(R.id.letter_spacing_seekbar);
        lineSpacingSeekBar = findViewById(R.id.line_spacing_seekbar);
        Button applyButton = findViewById(R.id.apply_button);

        // Setup Spinner, SeekBars and Button
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, new String[]{"mali", "dyslexic"});
        fontSpinner.setAdapter(adapter);
        // Add font choices to Spinner, configure SeekBar max values and listeners

        applyButton.setOnClickListener(v -> {
            // Lưu cấu hình vào SharedPreferences
            SharedPreferences preferences = getSharedPreferences("FontPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("font", fontSpinner.getSelectedItem().toString());
            editor.putInt("size", sizeSeekBar.getProgress());
            editor.putFloat("letterSpacing", letterSpacingSeekBar.getProgress() / 10.0f);
            editor.putFloat("lineSpacing", lineSpacingSeekBar.getProgress() / 10.0f);
            editor.apply();

            // Chuyển sang FontDisplayActivity
            Intent intent = new Intent(FontEditActivity.this, FontDisplayActivity.class);
            startActivity(intent);
        });
    }
}