package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView openBookButton;
    private String content = "My school is my favorite place. I have" +
            " many friends in my school who always help me. My teachers" +
            " are very friendly and take care of my parents. Our school" +
            " is very beautiful. It has many classrooms, a playground, a" +
            " garden, and canteen. Our school is very big and famous. People" +
            " living in our city send their children to study here. Our" +
            " school also provides free education to poor children. Every" +
            " student studying here is supports and plays with us. Our seniors" +
            " are very friendly as well. Our school also does social services" +
            " like planting trees every month. I am proud of my school, and" +
            " love it very much. Engage your kid into diverse thoughts and" +
            " motivate them to improve their English with our Essay for Class" +
            " 1 and avail the Simple Essays suitable for them.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        create();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void create() {
        openBookButton = findViewById(R.id.openBookBtn);

        openBookButton.setOnClickListener(v -> {
            editor.putString("content", content);
            editor.apply();
            Intent intent = new Intent(this, ReadingActivity.class);
            startActivity(intent);
        });

        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());
    }

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}