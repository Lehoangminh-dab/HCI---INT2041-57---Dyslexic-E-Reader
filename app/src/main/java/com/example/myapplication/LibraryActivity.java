package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.textextractor.DocxTextExtractionStrategy;
import com.example.myapplication.utils.textextractor.TextExtractorUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

public class LibraryActivity extends AppCompatActivity {
    private static final String EPUB_MIME_TYPE = "application/epub+zip";
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String JPEG_MIME_TYPE = "image/jpeg";
    private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String PPT_MIME_TYPE = "application/vnd.ms-powerpoint";
    private static final String PPTX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    private static final String PNG_MIME_TYPE = "image/png";
    private static final String[] MIME_TYPES = {
            EPUB_MIME_TYPE,
            PDF_MIME_TYPE,
            JPEG_MIME_TYPE,
            DOCX_MIME_TYPE,
            PPT_MIME_TYPE,
            PPTX_MIME_TYPE,
            PNG_MIME_TYPE
    };
    private static final int REQUEST_CODE_UPLOAD_FILE = 1;
    private static final String LOG_TAG = "LibraryActivity";

    private TextExtractorUtil textExtractorUtil;
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
        Log.d(LOG_TAG, "Chosen file URI: " + fileUri);

        // Extract text from file
        String mimeType = getContentResolver().getType(fileUri);
        if (!Arrays.asList(MIME_TYPES).contains(mimeType)) {
            showError("Unsupported file type: " + mimeType);
        }

        switch (mimeType) {
            case DOCX_MIME_TYPE:
                textExtractorUtil = new TextExtractorUtil(new DocxTextExtractionStrategy(this));
                break;
        }

        String extractedText = textExtractorUtil.extractText(fileUri);
        Log.d(LOG_TAG, "Extracted text: " + extractedText);
    }

    private void showError(String message) {
        Snackbar.make(uploadFileIcon, message, Snackbar.LENGTH_LONG).show();
    }
}
