package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FragmentHighlight extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private UserController controller;

    private User user;
    private User initialUser;
    private String highLight;
    private int containerId;
    private Switch highlightSwitch;
    private ImageView highlightWordBtn, highlightRulerBtn, highlightDarkBtn, backBtn;
    private TextView textWord, textRuler, textHole;
    private TextView sampleText;
    private boolean onHighLight;
    private View darkOverlay;
//    private boolean isHighlightEnabled = true;

    enum HighlightMode {
        WORD, RULER, DARK, OFF
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highlight, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle args = getArguments();
        if (args != null) {
            containerId = args.getInt("containerId", -1);
        }

        controller = new UserController(requireActivity());

        highlightSwitch = view.findViewById(R.id.switch1);
        highlightWordBtn = view.findViewById(R.id.highlight_word_btn);
        highlightRulerBtn = view.findViewById(R.id.highlight_ruler_btn);
        highlightDarkBtn = view.findViewById(R.id.highlight_dark_btn);
        backBtn = view.findViewById(R.id.arrow_back_btn);
        textWord = view.findViewById(R.id.textViewWord);
        textRuler = view.findViewById(R.id.textViewRuler);
        textHole = view.findViewById(R.id.textViewDark);
        sampleText = view.findViewById(R.id.textView8);
        darkOverlay = view.findViewById(R.id.dark_overlay);

        backBtn.setOnClickListener(v -> {
            FragmentSettingsMenu fragmentSettingsMenu = new FragmentSettingsMenu();
            fragmentSettingsMenu.setArguments(args);
            replaceFragment(fragmentSettingsMenu);
        });

        highlightSwitch.setChecked(onHighLight);
        highlightSwitch.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

        setupHighlightSwitch();
        setupHighlightButtons();

        applyHighlightMode(HighlightMode.WORD);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);
        initialUser = new User(user);

        highLight = user.getHighLight();

        if (highLight.equals("OFF")) {
            applyHighlightMode(HighlightMode.OFF);
            onHighLight = false;
            resetHighlight();
        } else {
            onHighLight = true;
            highlightSwitch.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

            if (highLight.equals("WORD")) {
                applyHighlightMode(HighlightMode.WORD);
            } else if (highLight.equals("RULER")) {
                applyHighlightMode(HighlightMode.RULER);
            } else if (highLight.equals("DARK")) {
                applyHighlightMode(HighlightMode.DARK);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        user.setHighLight(highLight);
        if (!initialUser.equals(user)) {
            controller.updateUser(user);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            editor.putString("user", json);
            editor.apply();
        }
    }

    private void setupHighlightSwitch() {
        highlightSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onHighLight = isChecked;
            if (isChecked) {
                highlightSwitch.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                applyHighlightMode(HighlightMode.WORD);
            } else {
                applyHighlightMode(HighlightMode.OFF);
                resetHighlight();
            }
        });
    }

    private void setupHighlightButtons() {
        highlightWordBtn.setOnClickListener(v -> {
            if (onHighLight) {
                applyHighlightMode(HighlightMode.WORD);
            }
        });

        highlightRulerBtn.setOnClickListener(v -> {
            if (onHighLight) {
                applyHighlightMode(HighlightMode.RULER);
            }
        });

        highlightDarkBtn.setOnClickListener(v -> {
            if (onHighLight) {
                applyHighlightMode(HighlightMode.DARK);
            }
        });
    }

    private void applyHighlightMode(HighlightMode mode) {
        resetHighlight();
        highLight = mode.name();

        textWord.setTextColor(Color.BLACK);
        textRuler.setTextColor(Color.BLACK);
        textHole.setTextColor(Color.BLACK);

        switch (mode) {
            case WORD:
                darkOverlay.setVisibility(View.GONE);
                highlightWordBtn.setImageResource(R.drawable.ic_highlight_word);
                highlightRulerBtn.setImageResource(R.drawable.ic_highlight_ruler_alpha);
                highlightDarkBtn.setImageResource(R.drawable.ic_highlight_dark_alpha);
                highlightWordBtn.setColorFilter(Color.RED);
                textWord.setTextColor(Color.RED);
                applyWordHighlight();
                break;

            case RULER:
                darkOverlay.setVisibility(View.GONE);
                highlightWordBtn.setImageResource(R.drawable.ic_highlight_word_alpha);
                highlightRulerBtn.setImageResource(R.drawable.ic_highlight_ruler);
                highlightDarkBtn.setImageResource(R.drawable.ic_highlight_dark_alpha);
                highlightRulerBtn.setColorFilter(Color.RED);
                textRuler.setTextColor(Color.RED);
                applyRulerHighlight();
                break;

            case DARK:
                darkOverlay.setVisibility(View.VISIBLE);
                highlightWordBtn.setImageResource(R.drawable.ic_highlight_word_alpha);
                highlightRulerBtn.setImageResource(R.drawable.ic_highlight_ruler_alpha);
                highlightDarkBtn.setImageResource(R.drawable.ic_highlight_dark);
                highlightDarkBtn.setColorFilter(Color.RED);
                textHole.setTextColor(Color.RED);
                applyDarkHighlight();
                break;
        }
    }


    private void applyWordHighlight() {
        SpannableString wordHighlightedText = new SpannableString("This is a sample");
        wordHighlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordHighlightedText.setSpan(new ForegroundColorSpan(Color.GRAY), 5, wordHighlightedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sampleText.setText(wordHighlightedText);
    }

    private void applyRulerHighlight() {
        SpannableString rulerHighlightedText = new SpannableString("This is a sample");
        rulerHighlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        rulerHighlightedText.setSpan(new ForegroundColorSpan(Color.GRAY), 9, rulerHighlightedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sampleText.setText(rulerHighlightedText);
    }

    private void applyDarkHighlight() {
        SpannableString darkHighlightedText = new SpannableString("This is a sample");
        darkHighlightedText.setSpan(new BackgroundColorSpan(Color.parseColor("#F5DEB3")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Nền sáng cho từ "This"
        darkHighlightedText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Màu chữ tối cho "This"
        darkHighlightedText.setSpan(new ForegroundColorSpan(Color.parseColor("#555555")), 5, darkHighlightedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); // Làm mờ các chữ còn lại
        sampleText.setText(darkHighlightedText);
    }

    private void resetHighlight() {
        highlightSwitch.getThumbDrawable().clearColorFilter();
        highlightWordBtn.setColorFilter(null);
        highlightRulerBtn.setColorFilter(null);
        highlightDarkBtn.setColorFilter(null);
        sampleText.setTextColor(Color.BLACK);
        sampleText.setText("This is a sample");
    }

    private void replaceFragment(Fragment newFragment) {
        if (containerId != -1) {
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}

