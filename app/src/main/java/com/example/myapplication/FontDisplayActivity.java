package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FontDisplayActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_display);

        TextView displayText = findViewById(R.id.display_text);

        // Lấy cấu hình từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("FontPrefs", MODE_PRIVATE);
        String fontName = preferences.getString("font", "roboto_regular");
        int size = preferences.getInt("size", 16);
        float letterSpacing = preferences.getFloat("letterSpacing", 0);
        float lineSpacing = preferences.getFloat("lineSpacing", 1);

        // Lấy font từ thư mục res/font dựa trên fontName
        int fontId = getResources().getIdentifier(fontName, "font", getPackageName());
        Typeface typeface = ResourcesCompat.getFont(this, fontId);

        // Áp dụng font và các cài đặt khác cho TextView
        displayText.setTypeface(typeface);
        displayText.setTextSize(size);
        displayText.setLetterSpacing(letterSpacing);
        displayText.setLineSpacing(0, lineSpacing);
    }
}
