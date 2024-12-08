package com.example.myapplication.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.ColorRule;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ColorRuleController extends FireBaseController {

    private boolean deleted = false;

    public ColorRuleController() {
        super();
    }

    public ColorRuleController(Context context) {
        super(context);
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
                .addOnFailureListener(e -> Log.e("Color Rule", "Error saving color rule data"));
    }


    public boolean deleteRule(ColorRule event) {
        if (event == null) {
            return deleted;
        }
        database.getReference("Rules").child(String.valueOf(event.getId())).removeValue()
                .addOnSuccessListener(aVoid -> deleted = true)
                .addOnFailureListener(e -> Log.e("Color Rule", "Error deleting color rule data"));;
        return deleted;
    }
}
