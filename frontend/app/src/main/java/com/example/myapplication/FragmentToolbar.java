package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class FragmentToolbar extends Fragment {

    private FrameLayout homeButton;
    private FrameLayout libraryButton;
    private FrameLayout settingsButton;
    private ImageView homeIcon;
    private ImageView libraryIcon;
    private ImageView settingsIcon;

    public FragmentToolbar() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toolbar, container, false);
        homeButton = view.findViewById(R.id.homeBtn);
        libraryButton = view.findViewById(R.id.libraryBtn);
        settingsButton = view.findViewById(R.id.settingsBtn);
        homeIcon = view.findViewById(R.id.homeIcon);
        libraryIcon = view.findViewById(R.id.libraryIcon);
        settingsIcon = view.findViewById(R.id.settingsIcon);


        if (getActivity() instanceof MainMenuActivity) {
            homeIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            homeButton.setEnabled(false);
        } else if (getActivity() instanceof LibraryActivity || getActivity() instanceof ActivityLibraryView) {
            libraryIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            libraryButton.setEnabled(false);
        } else {
            settingsIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            settingsButton.setEnabled(false);
        }

        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
            startActivity(intent);
        });

        libraryButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LibraryActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
