package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utils.textextractor.DocxTextExtractionStrategy;
import com.example.myapplication.utils.textextractor.EpubTextExtractionStrategy;
import com.example.myapplication.utils.textextractor.TextExtractorUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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


    /**
     * Handles the result of a file selection activity, extracts text from the selected file,
     * and creates a new text file with the extracted content.
     *
     * <p><b>File Storage Details:</b><br>
     * The extracted text is saved to a new file named "extracted_text.txt" in the app's private
     * storage directory (accessed via {@code Context.getFilesDir()}). This directory is:
     * <ul>
     *   <li>Private to the app (not accessible by other apps)</li>
     *   <li>Automatically deleted when the app is uninstalled</li>
     *   <li>Typically located at: {@code /data/data/[package_name]/files/extracted_text.txt}</li>
     * </ul>
     * To access this file programmatically within the app, use:
     * {@code File file = new File(context.getFilesDir(), "extracted_text.txt")}</p>
     *
     * @throws RuntimeException if the request code is unexpected, result code is not OK,
     *                         result data is null, file URI is null, or mime type is unsupported.
     *                         These exceptions are handled by the showError() method.
     * @throws IOException if file creation or writing fails (handled internally with logging)
     *
     * @see #createTextFile(String, String)
     * @see #initializeTextExtractor(String)
     * @see TextExtractorUtil#extractText(Uri)
     * @see android.content.Context#getFilesDir()
     */
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
        if (fileUri == null) {
            showError("Unexpected null file URI");
        }

        Log.d(LOG_TAG, "Chosen file URI: " + fileUri);

        // Extract text from file
        String mimeType = getContentResolver().getType(fileUri);
        if (!Arrays.asList(MIME_TYPES).contains(mimeType)) {
            showError("Unsupported file type: " + mimeType);
        }

        initializeTextExtractor(mimeType);
        String extractedText = textExtractorUtil.extractText(fileUri);
        Log.d(LOG_TAG, "Extracted text: " + extractedText);

        // Get chosen file name
        String fileName = getFileName(fileUri);
        Log.d(LOG_TAG, "Chosen file name: " + fileName);
        // Remove the file name's extension
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        createTextFile(fileNameWithoutExtension + ".txt", extractedText);
    }

    private void showError(String message) {
        Snackbar.make(uploadFileIcon, message, Snackbar.LENGTH_LONG).show();
    }

    private void initializeTextExtractor(String mimeType) {
        if (mimeType == null) {
            showError("Null mime type");
            return;
        }
        switch (mimeType) {
            case DOCX_MIME_TYPE:
                textExtractorUtil = new TextExtractorUtil(new DocxTextExtractionStrategy(this));
                break;
            case EPUB_MIME_TYPE:
                textExtractorUtil = new TextExtractorUtil(new EpubTextExtractionStrategy());
                break;
        }
    }

    private void createTextFile(String fileName, String fileContent) {
        File file = new File(this.getFilesDir(), fileName);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(fileContent);
            fileWriter.close();
            Log.d(LOG_TAG, "Created new file: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileName(Uri fileUri) {
        Cursor returnCursor = getContentResolver().query(fileUri, null, null,
                null, null);
        if (returnCursor == null || !returnCursor.moveToFirst()) {
            Log.e(LOG_TAG, "Error getting file name");
            return "Error getting file name";
        }

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        return returnCursor.getString(nameIndex);
    }
}
