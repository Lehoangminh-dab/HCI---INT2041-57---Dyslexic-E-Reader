package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.adapter.ColorRuleAdapter;
import com.example.myapplication.controller.ColorRuleController;
import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.ColorRule;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentColor extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private UserController controller;

    private int containerId;
    private String nameRule;
    private String describeRule;
    private int color;

    private User user;
    private User initialUser;

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

        controller = new UserController(requireActivity());

        backButton = view.findViewById(R.id.backBtn);
        colorRuleListView = view.findViewById(R.id.colorRuleList);
        ruleList = new ArrayList<>();
        adapter = new ColorRuleAdapter(requireActivity(), ruleList);

        handleReceivedRule();

        colorRuleListView.setAdapter(adapter);

        addRuleButton = view.findViewById(R.id.addRuleBtn);

        backButton.setOnClickListener(v -> {
            FragmentSettingsMenu fragmentSettingsMenu = new FragmentSettingsMenu();
            fragmentSettingsMenu.setArguments(args);
            replaceFragment(fragmentSettingsMenu);
        });

        colorRuleListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            Handler handler = new Handler();
            Runnable runnable;

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                runnable = () -> showChangeColorRuleDialog(position);
                handler.postDelayed(runnable, 2000);
                return true;
            }
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
        user.setRuleList(ruleList);
        if (!initialUser.equals(user)) {
            controller.updateUser(user);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            editor.putString("user", json);
            editor.apply();
        }
    }

    private void showAddColorRuleDialog() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_add_color_rule_layout);

        int red = ContextCompat.getColor(requireContext(), R.color.red);
        int opacity_red = ContextCompat.getColor(requireContext(), R.color.opacity_red);
        int blue = ContextCompat.getColor(requireContext(), R.color.blue);
        int opacity_blue = ContextCompat.getColor(requireContext(), R.color.opacity_blue);
        int green = ContextCompat.getColor(requireContext(), R.color.green);
        int opacity_green = ContextCompat.getColor(requireContext(), R.color.opacity_green);
        int pink = ContextCompat.getColor(requireContext(), R.color.pink);
        int opacity_pink = ContextCompat.getColor(requireContext(), R.color.opacity_pink);
        int olive = ContextCompat.getColor(requireContext(), R.color.olive);
        int opacity_olive = ContextCompat.getColor(requireContext(), R.color.opacity_olive);
        int violet = ContextCompat.getColor(requireContext(), R.color.violet);
        int opacity_violet = ContextCompat.getColor(requireContext(), R.color.opacity_violet);

        ImageView closeButton = dialog.findViewById(R.id.closeBtn);
        EditText enterLetter = dialog.findViewById(R.id.enterLetter);
        ImageView redButton = dialog.findViewById(R.id.redBtn);
        ImageView blueButton = dialog.findViewById(R.id.blueBtn);
        ImageView greenButton = dialog.findViewById(R.id.greenBtn);
        ImageView pinkButton = dialog.findViewById(R.id.pinkBtn);
        ImageView oliveButton = dialog.findViewById(R.id.oliveBtn);
        ImageView violetButton = dialog.findViewById(R.id.violetBtn);
        ImageView cancelDialogButton = dialog.findViewById(R.id.cancelDialogBtn);
        ImageView addNewRuleButton = dialog.findViewById(R.id.addNewRuleBtn);
        addNewRuleButton.setEnabled(false);

        closeButton.setOnClickListener(v -> dialog.dismiss());

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

        redButton.setOnClickListener(v -> {
            color = red;
            redButton.setColorFilter(red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        blueButton.setOnClickListener(v -> {
            color = blue;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        greenButton.setOnClickListener(v -> {
            color = green;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        pinkButton.setOnClickListener(v -> {
            color = pink;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        oliveButton.setOnClickListener(v -> {
            color = olive;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(olive);
            violetButton.setColorFilter(opacity_violet);
        });
        violetButton.setOnClickListener(v -> {
            color = violet;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(violet);
        });

        cancelDialogButton.setOnClickListener(v -> dialog.dismiss());

        addNewRuleButton.setOnClickListener(v -> {
            describeRule = "If you are confusing the letter " + nameRule + ", color it.";
            ColorRule rule = new ColorRule(nameRule, describeRule, color);
            ruleList.add(rule);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void showChangeColorRuleDialog(int position) {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.dialog_change_color_rule_layout);
        ColorRule thisRule = ruleList.get(position);
        String nameThisRule = thisRule.getName();
        int colorThisRule = thisRule.getColor();
        color = colorThisRule;

        int red = ContextCompat.getColor(requireContext(), R.color.red);
        int opacity_red = ContextCompat.getColor(requireContext(), R.color.opacity_red);
        int blue = ContextCompat.getColor(requireContext(), R.color.blue);
        int opacity_blue = ContextCompat.getColor(requireContext(), R.color.opacity_blue);
        int green = ContextCompat.getColor(requireContext(), R.color.green);
        int opacity_green = ContextCompat.getColor(requireContext(), R.color.opacity_green);
        int pink = ContextCompat.getColor(requireContext(), R.color.pink);
        int opacity_pink = ContextCompat.getColor(requireContext(), R.color.opacity_pink);
        int olive = ContextCompat.getColor(requireContext(), R.color.olive);
        int opacity_olive = ContextCompat.getColor(requireContext(), R.color.opacity_olive);
        int violet = ContextCompat.getColor(requireContext(), R.color.violet);
        int opacity_violet = ContextCompat.getColor(requireContext(), R.color.opacity_violet);

        ImageView closeButton = dialog.findViewById(R.id.closeBtn);
        EditText enterLetter = dialog.findViewById(R.id.enterLetter);
        ImageView redButton = dialog.findViewById(R.id.redBtn);
        ImageView blueButton = dialog.findViewById(R.id.blueBtn);
        ImageView greenButton = dialog.findViewById(R.id.greenBtn);
        ImageView pinkButton = dialog.findViewById(R.id.pinkBtn);
        ImageView oliveButton = dialog.findViewById(R.id.oliveBtn);
        ImageView violetButton = dialog.findViewById(R.id.violetBtn);
        ImageView cancelDialogButton = dialog.findViewById(R.id.cancelDialogBtn);
        FrameLayout changeColorButton = dialog.findViewById(R.id.changeColorBtn);
        TextView changeColorTextView = dialog.findViewById(R.id.changeColorTextView);

        enterLetter.setText(nameThisRule);

        if (colorThisRule == red) redButton.setColorFilter(red);
        else if (colorThisRule == blue) blueButton.setColorFilter(blue);
        else if (colorThisRule == green) greenButton.setColorFilter(green);
        else if (colorThisRule == pink) pinkButton.setColorFilter(pink);
        else if (colorThisRule == olive) oliveButton.setColorFilter(olive);
        else if (colorThisRule == violet) violetButton.setColorFilter(violet);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        enterLetter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                nameRule = enterLetter.getText().toString().trim();
                changeColorTextView.setText("Change");
            }
        });

        redButton.setOnClickListener(v -> {
            changeColorTextView.setText("Change");
            color = red;
            redButton.setColorFilter(red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        blueButton.setOnClickListener(v -> {
            changeColorTextView.setText("Change");
            color = blue;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        greenButton.setOnClickListener(v -> {
            changeColorTextView.setText("Change");
            color = green;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        pinkButton.setOnClickListener(v -> {
            changeColorTextView.setText("Change");
            color = pink;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(opacity_violet);
        });
        oliveButton.setOnClickListener(v -> {
            changeColorTextView.setText("Change");
            color = olive;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(olive);
            violetButton.setColorFilter(opacity_violet);
        });
        violetButton.setOnClickListener(v -> {
            changeColorTextView.setText("Change");
            color = violet;
            redButton.setColorFilter(opacity_red);
            blueButton.setColorFilter(opacity_blue);
            greenButton.setColorFilter(opacity_green);
            pinkButton.setColorFilter(opacity_pink);
            oliveButton.setColorFilter(opacity_olive);
            violetButton.setColorFilter(violet);
        });

        cancelDialogButton.setOnClickListener(v -> dialog.dismiss());

        changeColorButton.setOnClickListener(v -> {
            if (changeColorTextView.getText().equals("Delete")) {
                ruleList.remove(position);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            } else if (changeColorTextView.getText().equals("Change")) {
                if (!thisRule.getName().equals(nameRule)) {
                    describeRule = "If you are confusing the letter " + nameRule + ", color it.";
                    thisRule.setName(nameRule);
                    thisRule.setDescribe(describeRule);
                }
                if (thisRule.getColor() != color) {
                    thisRule.setColor(color);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void handleReceivedRule() {
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);
        initialUser = new User(user);

        if (user != null && user.getRuleList() != null) {
            ruleList.clear();
            ruleList.addAll(user.getRuleList());
        } else {
            ruleList.clear();
        }

        adapter.notifyDataSetChanged();
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
