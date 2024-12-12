package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentLibraryViewTitle extends Fragment {
    private ImageView backButton;
    private TextView titleView;

    public FragmentLibraryViewTitle() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize views
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_library_view_title, container,
                false);
        backButton = view.findViewById(R.id.back_arrow);
        titleView = view.findViewById(R.id.view_title);

        // Retrieve arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String viewName = bundle.getString("view_name");
            titleView.setText(viewName);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the entire activity.
                requireActivity().finish();
            }
        });

        return view;
    }
}
