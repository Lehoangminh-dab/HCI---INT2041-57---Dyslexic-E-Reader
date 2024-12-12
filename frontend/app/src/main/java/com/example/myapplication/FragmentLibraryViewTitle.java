package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.model.LibraryView;

import java.io.Serializable;

public class FragmentLibraryViewTitle extends Fragment {
    private ImageView backButton;
    private ImageView iconView;
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
        iconView = view.findViewById(R.id.view_logo);
        titleView = view.findViewById(R.id.view_title);

        // Retrieve arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            LibraryView libraryView = (LibraryView) bundle.getSerializable("library_view");
            iconView.setImageDrawable(libraryView.getViewIcon());
            titleView.setText(libraryView.getName());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the title fragment to the old library title one.

                // Pop the back stack.
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
