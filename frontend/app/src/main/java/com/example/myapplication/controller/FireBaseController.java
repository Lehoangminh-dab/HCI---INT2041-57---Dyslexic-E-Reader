package com.example.myapplication.controller;

import android.content.Context;

import com.google.firebase.database.FirebaseDatabase;

public abstract class FireBaseController {
    protected FirebaseDatabase database;
    protected Context context;

    public FireBaseController() {
        database = FirebaseDatabase.getInstance();
    }

    public FireBaseController(Context context) {
        database = FirebaseDatabase.getInstance();
        this.context = context;
    }
}
