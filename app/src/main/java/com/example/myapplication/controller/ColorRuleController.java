package com.example.myapplication.controller;

import android.content.Context;
import android.widget.Toast;

import com.example.myapplication.model.ColorRule;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ColorRuleController {

    private FirebaseDatabase database;
    private Context context;
    private boolean deleted = false;

    public ColorRuleController() {
        database = FirebaseDatabase.getInstance();
    }

    public ColorRuleController(Context context) {
        database = FirebaseDatabase.getInstance();
        this.context = context;
    }

    public CompletableFuture<List<ColorRule>> getAllRules() {
        CompletableFuture<List<ColorRule>> future = new CompletableFuture<>();

        database.getReference("Rules").get().addOnSuccessListener(dataSnapshot -> {
            List<ColorRule> rules = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                ColorRule rule = (ColorRule) data.getValue(ColorRule.class);
                if (rule != null) {
                    rules.add(rule);
                }
            }
            future.complete(rules);
        });

        return future;
    }

    public void addRule(ColorRule rule) {
        if (rule == null) {
            return;
        }
        database.getReference("Rules").child(String.valueOf(rule.getId())).setValue(rule)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Thêm thành công", Toast.LENGTH_LONG))
                .addOnFailureListener(e -> Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_LONG));
    }


    public boolean deleteRule(ColorRule event) {
        if (event == null) {
            return deleted;
        }
        database.getReference("Rules").child(String.valueOf(event.getId())).removeValue()
                .addOnSuccessListener(aVoid -> deleted = true);
        return deleted;
    }
}
