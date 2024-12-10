package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FragmentSettingsMenu extends Fragment {

    private FirebaseAuth auth;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TextView nameTextView;
    private ConstraintLayout accountButton;
    private ConstraintLayout highlightButton;
    private ConstraintLayout fontButton;
    private ConstraintLayout colorButton;
    private ConstraintLayout notificationButton;
    private ImageView logOutButton;
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

        auth = FirebaseAuth.getInstance();

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        nameTextView = view.findViewById(R.id.nameTextView);
        accountButton = view.findViewById(R.id.accountBtn);
        highlightButton = view.findViewById(R.id.highlightBtn);
        fontButton = view.findViewById(R.id.fontBtn);
        colorButton = view.findViewById(R.id.colorBtn);
        notificationButton = view.findViewById(R.id.notificationBtn);
        logOutButton = view.findViewById(R.id.logOutBtn);

        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        User user = gson.fromJson(jsonRetrieved, type);
        nameTextView.setText(user.getName());

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

        logOutButton.setOnClickListener(v -> {
            editor.remove("user");
            editor.apply();
            auth.signOut();

            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
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
