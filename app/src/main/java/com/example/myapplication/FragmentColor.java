package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.adapter.ColorRuleAdapter;
import com.example.myapplication.controller.ColorRuleController;
import com.example.myapplication.model.ColorRule;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FragmentColor extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int containerId;
    private String nameRule;
    private String describeRule;
    private int colorRule;
    private ColorRuleController colorRuleController;

    private ImageView backButton;
    private ListView colorRuleListView;
    private List<ColorRule> ruleList;
    private ColorRuleAdapter adapter;
    private LinearLayout addRuleButton;

    public FragmentColor() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle args = getArguments();
        if (args != null) {
            containerId = args.getInt("containerId", -1);
        }

        colorRuleController = new ColorRuleController(requireActivity());

        backButton = view.findViewById(R.id.backBtn);
        colorRuleListView = view.findViewById(R.id.colorRuleList);
        ruleList = new ArrayList<>();
        adapter = new ColorRuleAdapter(requireActivity(), ruleList);
        colorRuleListView.setAdapter(adapter);
        addRuleButton = view.findViewById(R.id.addRuleBtn);

        backButton.setOnClickListener(v -> {
            FragmentSettingsMenu fragmentSettingsMenu = new FragmentSettingsMenu();
            fragmentSettingsMenu.setArguments(args);
            replaceFragment(fragmentSettingsMenu);
        });

        addRuleButton.setOnClickListener(v -> showAddColorRuleDialog());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleReceivedRule();
    }

    @Override
    public void onStop() {
        super.onStop();
        Gson gson = new Gson();
        String json = gson.toJson(ruleList);
        editor.putString("colorRules", json);
        editor.apply();
    }

    private void showAddColorRuleDialog() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_add_color_rule_layout);

        EditText enterLetter = dialog.findViewById(R.id.enterLetter);
        ImageView cancelDialogButton = dialog.findViewById(R.id.cancelDialogBtn);
        ImageView addNewRuleButton = dialog.findViewById(R.id.addNewRuleBtn);
        addNewRuleButton.setEnabled(false);

        enterLetter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                nameRule = enterLetter.getText().toString();
                if (nameRule.isEmpty()) {
                    addNewRuleButton.setEnabled(false);
                } else {
                    addNewRuleButton.setEnabled(true);
                }
            }
        });

        cancelDialogButton.setOnClickListener(v -> dialog.dismiss());

        addNewRuleButton.setOnClickListener(v -> {
            describeRule = "If you are confusing the letter " + nameRule + ", color it.";
            colorRule = ContextCompat.getColor(requireContext(), R.color.olive);
            ColorRule rule = new ColorRule(nameRule, describeRule, colorRule);
            colorRuleController.addRule(rule);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void handleReceivedRule() {
        colorRuleController.getAllRules().thenApply(rules -> {
            if (!rules.isEmpty()) {
                ruleList.clear();
                ruleList.addAll(rules);
                adapter.notifyDataSetChanged();
            }
            return null;
        });
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
