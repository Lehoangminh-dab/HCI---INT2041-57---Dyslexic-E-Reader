package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.ColorRule;

import com.example.myapplication.model.Font;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.TextColorUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FragmentFont extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private UserController controller;

    private User user;
    private Font font;
    private int containerId;
    private String fontName;
    private int size;
    private float lineSpace;
    private int wordSpace;
    private int letterSpace;
    private List<ColorRule> ruleList;

    private ImageView backButton;
    private LinearLayout maliFontButton;
    private TextView maliText;
    private TextView maliSample;
    private TextView dyslexicText;
    private TextView dyslexicSample;
    private LinearLayout dyslexicFontButton;
    private SeekBar sizeSeekbar;
    private SeekBar lineSpaceSeekbar;
    private ImageView wordSpace1Button;
    private ImageView wordSpace2Button;
    private ImageView wordSpace3Button;
    private TextView sampleTextView;

    public FragmentFont() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_font, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle args = getArguments();
        if (args != null) {
            containerId = args.getInt("containerId", -1);
        }

        controller = new UserController(requireActivity());

        backButton = view.findViewById(R.id.backBtn);
        maliFontButton = view.findViewById(R.id.maliFontBtn);
        maliText = view.findViewById(R.id.maliText);
        maliSample = view.findViewById(R.id.maliSample);
        dyslexicFontButton = view.findViewById(R.id.dyslexicFontBtn);
        dyslexicText = view.findViewById(R.id.dyslexicText);
        dyslexicSample = view.findViewById(R.id.dyslexicSample);
        sizeSeekbar = view.findViewById(R.id.sizeSeekbar);
        lineSpaceSeekbar = view.findViewById(R.id.lineSpaceSeekbar);
        wordSpace1Button = view.findViewById(R.id.wordSpace1Btn);
        wordSpace2Button = view.findViewById(R.id.wordSpace2Btn);
        wordSpace3Button = view.findViewById(R.id.wordSpace3Btn);
        sampleTextView = view.findViewById(R.id.sampleTextView);

        backButton.setOnClickListener(v -> {
            FragmentSettingsMenu fragmentSettingsMenu = new FragmentSettingsMenu();
            fragmentSettingsMenu.setArguments(args);
            replaceFragment(fragmentSettingsMenu);
        });

        maliFontButton.setOnClickListener(v -> {
            fontName = "mali";
            font.setName(fontName);
            maliText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            maliSample.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            dyslexicText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            dyslexicSample.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            updateSampleText();
        });

        dyslexicFontButton.setOnClickListener(v -> {
            fontName = "dyslexic";
            font.setName(fontName);
            dyslexicText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            dyslexicSample.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            maliText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            maliSample.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            updateSampleText();
        });

        sizeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                size = progress;
                font.setSize(size);
                updateSampleText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        lineSpaceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                lineSpace = progress / 10.0f;
                font.setLineSpace(lineSpace);
                updateSampleText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        wordSpace1Button.setOnClickListener(v -> {
            wordSpace = 1;
            font.setWordSpace(wordSpace);
            wordSpace1Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            wordSpace2Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            wordSpace3Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            updateSampleText();
        });

        wordSpace2Button.setOnClickListener(v -> {
            wordSpace = 2;
            font.setWordSpace(wordSpace);
            wordSpace1Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            wordSpace2Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            wordSpace3Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            updateSampleText();
        });

        wordSpace3Button.setOnClickListener(v -> {
            wordSpace = 3;
            font.setWordSpace(wordSpace);
            wordSpace1Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            wordSpace2Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            wordSpace3Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            updateSampleText();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);

        font = user.getFont();
        fontName = font.getName();
        size = font.getSize();
        lineSpace = font.getLineSpace();
        wordSpace= font.getWordSpace();
        ruleList = user.getRuleList();

        if (fontName.contains("mali")) {
            maliText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            maliSample.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else if(fontName.contains("dyslexic")) {
            dyslexicText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
            dyslexicSample.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }
        sizeSeekbar.setProgress(size);
        lineSpaceSeekbar.setProgress((int) (lineSpace * 10));
        if (wordSpace == 1) {
            wordSpace1Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
        } else if (wordSpace == 2) {
            wordSpace2Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
        } else {
            wordSpace3Button.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
        }

        updateSampleText();
    }

    private void updateSampleText() {
        int fontId = getResources().getIdentifier(fontName, "font", requireActivity().getPackageName());
        Typeface typeface = ResourcesCompat.getFont(requireActivity(), fontId);
        sampleTextView.setTypeface(typeface);
        sampleTextView.setTextSize(size);
        sampleTextView.setLineSpacing(0, lineSpace);
        String originalText = sampleTextView.getText().toString();
        String updatedText;
        if (wordSpace == 1) {
            updatedText = originalText.replaceAll("\\s+", "  ");
        } else if (wordSpace == 2) {
            updatedText = originalText.replaceAll("\\s+", "    ");
        } else {
            updatedText = originalText.replaceAll("\\s+", "      ");
        }
        sampleTextView.setText(TextColorUtils.applyColorToText(requireContext(), updatedText, ruleList));
    }

    @Override
    public void onPause() {
        super.onPause();
        user.setFont(font);
        controller.updateUser(user);
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();
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
