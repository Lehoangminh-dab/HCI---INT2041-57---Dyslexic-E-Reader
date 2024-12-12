package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.controller.ColorRuleController;
import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.ColorRule;
import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private UserController controller;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String email = "";
    private String password = "";

    private EditText emailInput;
    private EditText passwordInput;
    private FrameLayout signInButton;
    private FrameLayout createAccButton;
    private ProgressDialog progressDialog;
    private List<ColorRule> ruleList;
    private ColorRuleController colorRuleController;
    private FrameLayout signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        signInBtn = findViewById(R.id.signInBtn);
        colorRuleController = new ColorRuleController(this);
        auth = FirebaseAuth.getInstance();
        controller = new UserController(this);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInBtn);
        createAccButton = findViewById(R.id.createAccBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(jsonRetrieved, type);
        if (user != null) {
            email = user.getEmail();
            password = user.getPassword();
            autoLogin();
        }

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

        signInButton.setOnClickListener(v -> login());

        createAccButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        // Uncomment if needed
        /*signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });*/
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
                            controller.getUser(userId).thenAccept(this::handleSuccessfulLogin);
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void autoLogin() {
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            controller.getUser(userId).thenAccept(this::handleSuccessfulLogin);
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void handleSuccessfulLogin(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
        finish();
    }
}