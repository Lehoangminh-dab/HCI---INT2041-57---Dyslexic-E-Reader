package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.adapter.ColorRuleAdapter;
import com.example.myapplication.controller.ColorRuleController;
import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.ColorRule;
import com.example.myapplication.model.User;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private UserController controller;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String email;
    private String password;

    private EditText emailInput;
    private EditText passwordInput;
    private FrameLayout signInButton;
    private TextView forgotPassButton;
    private TextView startButton;
    private FrameLayout createAccButton;
    private ProgressDialog progressDialog;
//    private List<ColorRule> ruleList;
//    private ColorRuleController colorRuleController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        controller = new UserController(this);

//        colorRuleController = new ColorRuleController(this);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInBtn);
        forgotPassButton = findViewById(R.id.forgotPassBtn);
        startButton = findViewById(R.id.startBtn);
        createAccButton = findViewById(R.id.createAccBtn);

//        ruleList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        signInButton.setOnClickListener(v -> login());
        createAccButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                email = emailInput.getText().toString().trim();
            }
        });

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = passwordInput.getText().toString().trim();
            }
        });

//        forgotPassButton.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    SpannableString content = new SpannableString(forgotPassButton.getText());
//                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
//                    forgotPassButton.setText(content);
//                    return true;
//
//                case MotionEvent.ACTION_UP:
//                    content = new SpannableString(forgotPassButton.getText().toString());
//                    forgotPassButton.setText(content);
//                    return true;
//            }
//            return false;
//        });
    }

    private void login() {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            controller.getUser(userId).thenAccept(user -> handleSuccessfulLogin(user));
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSuccessfulLogin(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

//    private void handleReceivedRule() {
//        colorRuleController.getAllRules().thenApply(rules -> {
//            if (!rules.isEmpty()) {
//                ruleList.addAll(rules);
//                Gson gson = new Gson();
//                String json = gson.toJson(ruleList);
//                editor.putString("colorRules", json);
//                editor.apply();
//            }
//
//            progressDialog.dismiss();
//
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
//
//            return null;
//        });
//    }
}