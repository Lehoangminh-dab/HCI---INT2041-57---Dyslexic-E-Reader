package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class FragmentSettingsMenu extends Fragment {

    private ConstraintLayout accountButton;
    private ConstraintLayout highlightButton;
    private ConstraintLayout fontButton;
    private ConstraintLayout colorButton;
    private ConstraintLayout notificationButton;
    private int containerId;

    public FragmentSettingsMenu() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_menu, container, false);

        Bundle args = getArguments();
        if (args != null) {
            containerId = args.getInt("containerId", -1);
        }

        accountButton = view.findViewById(R.id.accountBtn);
        highlightButton = view.findViewById(R.id.highlightBtn);
        fontButton = view.findViewById(R.id.fontBtn);
        colorButton = view.findViewById(R.id.colorBtn);
        notificationButton = view.findViewById(R.id.notificationBtn);

        accountButton.setOnClickListener(v -> {
            FragmentAccount fragmentAccount = new FragmentAccount();
            fragmentAccount.setArguments(args);
            replaceFragment(fragmentAccount);
        });
        highlightButton.setOnClickListener(v -> {
            FragmentHighlight fragmentHighlight = new FragmentHighlight();
            fragmentHighlight.setArguments(args);
            replaceFragment(fragmentHighlight);
        });
        fontButton.setOnClickListener(v -> {
            FragmentFont fragmentFont = new FragmentFont();
            fragmentFont.setArguments(args);
            replaceFragment(fragmentFont);
        });
        colorButton.setOnClickListener(v -> {
            FragmentColor fragmentColor = new FragmentColor();
            fragmentColor.setArguments(args);
            replaceFragment(fragmentColor);
        });
        notificationButton.setOnClickListener(v -> {
            FragmentNotification fragmentNotification = new FragmentNotification();
            fragmentNotification.setArguments(args);
            replaceFragment(fragmentNotification);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
