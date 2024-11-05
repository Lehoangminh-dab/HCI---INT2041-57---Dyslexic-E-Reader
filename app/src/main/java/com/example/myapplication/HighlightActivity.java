package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HighlightActivity extends AppCompatActivity {

    private Switch highlightSwitch;
    private ImageView highlightWordBtn, highlightRulerBtn, highlightDarkBtn, backBtn;
    private TextView textWord, textRuler, textHole;
    private TextView sampleText;
    private boolean isHighlightEnabled = true; // Start with highlight enabled

    enum HighlightMode {
        WORD, RULER, DARK, OFF
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight);

        highlightSwitch = findViewById(R.id.switch1);
        highlightWordBtn = findViewById(R.id.highlight_word_btn);
        highlightRulerBtn = findViewById(R.id.highlight_ruler_btn);
        highlightDarkBtn = findViewById(R.id.highlight_dark_btn);
        backBtn = findViewById(R.id.arrow_back_btn);
        textWord = findViewById(R.id.textViewWord);
        textRuler = findViewById(R.id.textViewRuler);
        textHole = findViewById(R.id.textViewDark);
        sampleText = findViewById(R.id.textView8);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HighlightActivity.this, ReadingActivity.class);
            startActivity(intent);
            finish(); // Kết thúc HighlightActivity
        });

        // Set the switch to be on and colored red
        highlightSwitch.setChecked(true);
        highlightSwitch.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        setupHighlightSwitch();
        setupHighlightButtons();

        // Apply initial highlight mode to "highlight_word"
        applyHighlightMode(HighlightMode.WORD);
    }

    private void setupHighlightSwitch() {
        highlightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isHighlightEnabled = isChecked;
            if (isChecked) {
                highlightSwitch.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                applyHighlightMode(HighlightMode.WORD); // Default to WORD mode when enabled
            } else {
                highlightSwitch.getThumbDrawable().clearColorFilter();
                applyHighlightMode(HighlightMode.OFF); // Thêm dòng này để lưu chế độ OFF
                resetHighlight();
            }
        });
    }

    private void setupHighlightButtons() {
        highlightWordBtn.setOnClickListener(v -> {
            if (isHighlightEnabled) {
                applyHighlightMode(HighlightMode.WORD);
            }
        });

        highlightRulerBtn.setOnClickListener(v -> {
            if (isHighlightEnabled) {
                applyHighlightMode(HighlightMode.RULER);
            }
        });

        highlightDarkBtn.setOnClickListener(v -> {
            if (isHighlightEnabled) {
                applyHighlightMode(HighlightMode.DARK);
            }
        });
    }

    private void applyHighlightMode(HighlightMode mode) {
        resetHighlight();
        saveHighlightMode(mode); // Lưu chế độ highlight đã chọn

        View darkOverlay = findViewById(R.id.dark_overlay); // View lớp phủ tối

        // Reset màu chữ về màu đen
        textWord.setTextColor(Color.BLACK);
        textRuler.setTextColor(Color.BLACK);
        textHole.setTextColor(Color.BLACK);

        switch (mode) {
            case WORD:
                darkOverlay.setVisibility(View.GONE); // Ẩn lớp phủ
                highlightWordBtn.setImageResource(R.drawable.ic_highlight_word);
                highlightRulerBtn.setImageResource(R.drawable.ic_highlight_ruler_alpha);
                highlightDarkBtn.setImageResource(R.drawable.ic_highlight_dark_alpha);
                highlightWordBtn.setColorFilter(Color.RED);
                textWord.setTextColor(Color.RED); // Đổi màu chữ dưới "Word" thành đỏ
                applyWordHighlight();
                break;

            case RULER:
                darkOverlay.setVisibility(View.GONE); // Ẩn lớp phủ
                highlightWordBtn.setImageResource(R.drawable.ic_highlight_word_alpha);
                highlightRulerBtn.setImageResource(R.drawable.ic_highlight_ruler);
                highlightDarkBtn.setImageResource(R.drawable.ic_highlight_dark_alpha);
                highlightRulerBtn.setColorFilter(Color.RED);
                textRuler.setTextColor(Color.RED); // Đổi màu chữ dưới "Ruler" thành đỏ
                applyRulerHighlight();
                break;

            case DARK:
                darkOverlay.setVisibility(View.VISIBLE); // Hiển thị lớp phủ
                highlightWordBtn.setImageResource(R.drawable.ic_highlight_word_alpha);
                highlightRulerBtn.setImageResource(R.drawable.ic_highlight_ruler_alpha);
                highlightDarkBtn.setImageResource(R.drawable.ic_highlight_dark);
                highlightDarkBtn.setColorFilter(Color.RED);
                textHole.setTextColor(Color.RED); // Đổi màu chữ dưới "Hole" thành đỏ
                applyDarkHighlight();
                break;
        }
    }


    private void applyWordHighlight() {
        // Only highlight the word "This" and dim the rest of the text
        SpannableString wordHighlightedText = new SpannableString("This is a sample");
        wordHighlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordHighlightedText.setSpan(new ForegroundColorSpan(Color.GRAY), 5, wordHighlightedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sampleText.setText(wordHighlightedText);
    }

    private void applyRulerHighlight() {
        // Keep "This is a" visible and dim other parts if needed
        SpannableString rulerHighlightedText = new SpannableString("This is a sample");
        rulerHighlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rulerHighlightedText.setSpan(new ForegroundColorSpan(Color.GRAY), 9, rulerHighlightedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sampleText.setText(rulerHighlightedText);
    }

    private void applyDarkHighlight() {
        // Tô sáng từ "This" với nền sáng, sử dụng SpannableString.
        SpannableString darkHighlightedText = new SpannableString("This is a sample");
        darkHighlightedText.setSpan(new BackgroundColorSpan(Color.parseColor("#F5DEB3")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Nền sáng cho từ "This"
        darkHighlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Màu chữ tối cho "This"
        darkHighlightedText.setSpan(new ForegroundColorSpan(Color.parseColor("#555555")), 5, darkHighlightedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Làm mờ các chữ còn lại

        sampleText.setText(darkHighlightedText);
    }


    private void resetHighlight() {
        highlightWordBtn.setColorFilter(null);
        highlightRulerBtn.setColorFilter(null);
        highlightDarkBtn.setColorFilter(null);

        // Reset sampleText styling to original state
        sampleText.setTextColor(Color.BLACK);
        sampleText.setText("This is a sample");
    }

    private void saveHighlightMode(HighlightMode mode) {
        SharedPreferences sharedPreferences = getSharedPreferences("HighlightPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("highlight_mode", mode.name()); // Lưu tên của chế độ highlight
        editor.apply();
    }

}

