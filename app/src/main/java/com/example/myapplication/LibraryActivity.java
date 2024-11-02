package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class LibraryActivity extends AppCompatActivity {
    private static final String[] MIME_TYPES = {
            "application/epub+zip",           // EPUB
            "application/pdf",                // PDF
            "image/jpeg",                     // JPEG/JPG
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
            "application/vnd.ms-powerpoint",  // PPT
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", // PPTX
            "image/png",                       // PNG
    };
    private static final int REQUEST_CODE_UPLOAD_FILE = 1;
    private static final String LOG_TAG = "LibraryActivity";
    private ImageView uploadFileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        uploadFileIcon = findViewById(R.id.upload_file_icon);
        setViewBehaviors();
    }

    private void setViewBehaviors() {
        setupUploadFileIconBehavior();
    }

    private void setupUploadFileIconBehavior () {
        uploadFileIcon.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Upload file icon clicked");
            openFile();
        });
    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        // Restricts the type of files that can be opened to those specified in MIME_TYPES.
        intent.putExtra(Intent.EXTRA_MIME_TYPES, MIME_TYPES);
        startActivityForResult(intent, REQUEST_CODE_UPLOAD_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // Get fileUri
        Uri fileUri;
        if (requestCode != REQUEST_CODE_UPLOAD_FILE) {
            showError("Unexpected request code: " + requestCode);
        }
        if (resultCode != LibraryActivity.RESULT_OK) {
            showError("Unexpected result code: " + resultCode);
        }
        if (resultData == null) {
            showError("Unexpected null result data");
        }
        fileUri = resultData.getData();

        // Extract text from file
        Log.d(LOG_TAG, "Chosen file URI: " + fileUri);
    }

    private void showError(String message) {
        Snackbar.make(uploadFileIcon, message, Snackbar.LENGTH_LONG).show();
    }
}
