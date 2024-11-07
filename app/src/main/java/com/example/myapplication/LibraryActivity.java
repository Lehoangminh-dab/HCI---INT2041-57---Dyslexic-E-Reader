package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.utils.textextractor.ImageTextExtractor;
import com.example.myapplication.utils.textextractor.TextExtractionStrategyFactory;
import com.example.myapplication.utils.textextractor.TextExtractor;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private TextExtractor textExtractor;
    private ImageTextExtractor imageTextExtractor;
    private ImageView uploadFileIcon;
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // Used to run image text extraction on background thread.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library);
        uploadFileIcon = findViewById(R.id.upload_file_icon);
        imageTextExtractor = new ImageTextExtractor(this);
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
     * @see TextExtractionStrategyFactory#createStrategy(Context, String)
     * @see TextExtractor#extractText(Uri)
     * @see android.content.Context#getFilesDir()
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        // Verify response
        if (requestCode != REQUEST_CODE_UPLOAD_FILE) {
            showMessage("Unexpected request code: " + requestCode);
        }
        if (resultCode != LibraryActivity.RESULT_OK) {
            showMessage("Unexpected result code: " + resultCode);
        }
        if (resultData == null) {
            showMessage("Unexpected null result data");
        }

        Uri fileUri = getFileUri(resultData);
        if (fileUri == null) {
            showMessage("Unexpected null file URI");
        }
        Log.d(LOG_TAG, "Chosen file URI: " + fileUri);

        String mimeType = getContentResolver().getType(fileUri);
        if (mimeType.equals(PNG_MIME_TYPE) || mimeType.equals(JPEG_MIME_TYPE)) {
            // Extract text from image on a separate thread.
            extractTextFromImage(fileUri).thenAccept(
                    extractedText -> {
                        if (extractedText != null) {
                            createTextFile(fileUri, extractedText);
                        } else {
                            showMessage("Image text content cannot be found.");
                        }
                    }
            );
        } else {
            // Extract text from document on the main thread.
            String extractedText = extractTextFromDocumentFile(fileUri);
            if (extractedText != null) {
                createTextFile(fileUri, extractedText);
            } else {
                showMessage("Error extracting text from file");
            }
        }
    }

    private Uri getFileUri(Intent data) {
        return data.getData();
    }

    private String extractTextFromDocumentFile(Uri fileUri) {
        String mimeType = getContentResolver().getType(fileUri);
        if (!Arrays.asList(MIME_TYPES).contains(mimeType)) {
            showMessage("Unsupported file type: " + mimeType);
        }

        textExtractor = new TextExtractor(TextExtractionStrategyFactory.createStrategy(this,
                mimeType));
        String extractedText = textExtractor.extractText(fileUri);
        Log.d(LOG_TAG, "Extracted text: " + extractedText);
        return extractedText;
    }

    private CompletableFuture<String> extractTextFromImage(Uri fileUri) {
        return imageTextExtractor.extractText(fileUri);
    }

    private void createTextFile(Uri fileUri, String fileContent) {
        // Remove the file name's extension
        String fileName = getFileName(fileUri);
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        String textFileName = fileNameWithoutExtension + ".txt";

        // Create the file with content in the app's private storage directory
        File file = new File(this.getFilesDir(), textFileName);
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

        showMessage("File uploaded successfully!");
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

    private void showMessage(String message) {
        Snackbar.make(uploadFileIcon, message, Snackbar.LENGTH_LONG).show();
    }
}
