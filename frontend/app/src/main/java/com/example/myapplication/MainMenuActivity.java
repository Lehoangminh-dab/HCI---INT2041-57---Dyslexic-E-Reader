package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainMenuActivity extends AppCompatActivity {

    private ConstraintLayout book1, book2, book3, book4, book5, book6, book7, book8, book9, book10, book11, book12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        // các button sách
        book1 = findViewById(R.id.book1);
        book2 = findViewById(R.id.book2);
        book3 = findViewById(R.id.book3);
        book4 = findViewById(R.id.book4);
        book5 = findViewById(R.id.book5);
        book6 = findViewById(R.id.book6);
        book7 = findViewById(R.id.book7);
        book8 = findViewById(R.id.book8);
        book9 = findViewById(R.id.book9);
        book10 = findViewById(R.id.book10);
        book11 = findViewById(R.id.book11);
        book12 = findViewById(R.id.book12);




        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());


        book1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });

        book12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
