package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        create();
    }

    private void create() {
        FragmentSettingsMenu fragmentSettingsMenu = new FragmentSettingsMenu();

        Bundle args = new Bundle();
        args.putInt("containerId", R.id.fragmentSetting);
        fragmentSettingsMenu.setArguments(args);

        loadFragment(R.id.fragmentSetting, fragmentSettingsMenu);
        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());
    }

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}