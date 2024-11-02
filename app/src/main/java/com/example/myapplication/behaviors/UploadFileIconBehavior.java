package com.example.myapplication.behaviors;

import android.util.Log;

import com.example.myapplication.FilePickerActivity;

public class UploadFileIconBehavior {
    private static final String LOG_TAG = "UploadFileIconBehavior";
    private FilePickerActivity filePickerActivity;

    public void onClicked() {
        Log.d(LOG_TAG, "Upload file icon clicked");
        this.filePickerActivity = new FilePickerActivity();
        filePickerActivity.openFile();
        Log.d(LOG_TAG, "File picker opened");
    }
}
