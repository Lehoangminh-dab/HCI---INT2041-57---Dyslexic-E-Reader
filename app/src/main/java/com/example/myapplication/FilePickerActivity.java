package com.example.myapplication;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class FilePickerActivity extends AppCompatActivity {
    private static final int PICK_ALL_FILES = 1;

    public void openFile() {
        super.onCreate(null);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_ALL_FILES);
    }
}
