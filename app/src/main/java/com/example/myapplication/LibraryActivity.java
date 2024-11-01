package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.behaviors.UploadFileIconBehavior;

public class LibraryActivity extends AppCompatActivity {
    ImageView uploadFileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        this.uploadFileIcon = findViewById(R.id.upload_file_icon);
        setViewBehaviors();
    }

    private void setViewBehaviors() {
        setupUploadFileIconBehavior();
    }

    private void setupUploadFileIconBehavior () {
        UploadFileIconBehavior uploadFileIconBehavior = new UploadFileIconBehavior();
        uploadFileIcon.setOnClickListener(v -> {
            uploadFileIconBehavior.onClicked();
        });
    }
}
