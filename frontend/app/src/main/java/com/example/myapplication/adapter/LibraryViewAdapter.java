package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.model.LibraryView;

import java.util.List;

public class LibraryViewAdapter extends ArrayAdapter<LibraryView> {
    private final Context context;
    private final List<LibraryView> views;

    public LibraryViewAdapter(Context context, List<LibraryView> views) {
        super(context, 0, views);
        this.context = context;
        this.views = views;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_library_view_layout, parent,
                    false);
        }

        // Render library view button
        LibraryView viewItem = views.get(position);

        TextView viewName = view.findViewById(R.id.library_view_name);
        viewName.setText(viewItem.getName());

        ImageView viewIcon = view.findViewById(R.id.library_view_icon);
        viewIcon.setImageDrawable(viewItem.getViewIcon());

        return view;
    }
}