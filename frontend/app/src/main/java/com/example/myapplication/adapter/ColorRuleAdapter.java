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
import com.example.myapplication.model.ColorRule;

import java.util.List;

public class ColorRuleAdapter extends ArrayAdapter<ColorRule> {

    private final Context context;
    private final List<ColorRule> rules;

    public ColorRuleAdapter(Context context, List<ColorRule> rules) {
        super(context, 0, rules);
        this.context = context;
        this.rules = rules;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_color_rule_layout, parent, false);
        }

        ColorRule rule = rules.get(position);

        TextView nameRule = view.findViewById(R.id.nameRule);
        TextView describeRule = view.findViewById(R.id.describeRule);
        ImageView changeColorButton = view.findViewById(R.id.changeColorBtn);

        nameRule.setText(rule.getName());
        nameRule.setTextColor(rule.getColor());

        describeRule.setText(rule.getDescribe());
        changeColorButton.setColorFilter(rule.getColor());

        changeColorButton.setOnClickListener(v -> {

        });

        return view;
    }
}
