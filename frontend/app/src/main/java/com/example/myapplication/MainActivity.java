package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.adapter.ColorRuleAdapter;
import com.example.myapplication.controller.ColorRuleController;
import com.example.myapplication.model.ColorRule;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TextView forgotPassButton;
    private TextView startButton;
    private ProgressDialog progressDialog;
    private List<ColorRule> ruleList;
    private ColorRuleController colorRuleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        colorRuleController = new ColorRuleController(this);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        forgotPassButton = findViewById(R.id.forgotPassBtn);
        startButton = findViewById(R.id.startBtn);

        ruleList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        forgotPassButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    SpannableString content = new SpannableString(forgotPassButton.getText());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    forgotPassButton.setText(content);
                    return true;

                case MotionEvent.ACTION_UP:
                    content = new SpannableString(forgotPassButton.getText().toString());
                    forgotPassButton.setText(content);
                    return true;
            }
            return false;
        });

        startButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    SpannableString content = new SpannableString(startButton.getText());
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    startButton.setText(content);
                    return true;

                case MotionEvent.ACTION_UP:
                    progressDialog.show();
                    handleReceivedRule();
                    return true;
            }
            return false;
        });
    }

    private void handleReceivedRule() {
        colorRuleController.getAllRules().thenApply(rules -> {
            if (!rules.isEmpty()) {
                ruleList.addAll(rules);
                Gson gson = new Gson();
                String json = gson.toJson(ruleList);
                editor.putString("colorRules", json);
                editor.apply();
            }

            progressDialog.dismiss();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

            return null;
        });
    }
}