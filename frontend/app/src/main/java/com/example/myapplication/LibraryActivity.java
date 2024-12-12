package com.example.myapplication;

import static android.Manifest.permission.CAMERA;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.User;
import com.example.myapplication.utils.textextractor.ImageTextExtractor;
import com.example.myapplication.utils.textextractor.TextExtractionStrategyFactory;
import com.example.myapplication.utils.textextractor.TextExtractor;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView scanBookButton;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;
    private static final int UCROP_REQUEST_CODE = UCrop.REQUEST_CROP;

    private Uri photoUri;

    private UserController controller;
    private User user;
    private User initialUser;
    private List<Book> bookList;

    private TextExtractor textExtractor;
    private ImageTextExtractor imageTextExtractor;
    private ImageView uploadFileIcon;
    private ExecutorService executorService = Executors.newSingleThreadExecutor(); // Used to run image text extraction on background thread.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_library);
        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        controller = new UserController(this);

        bookList = new ArrayList<>();

        handleReceivedBook();

        uploadFileIcon = findViewById(R.id.upload_file_icon);
        imageTextExtractor = new ImageTextExtractor(this);
        setViewBehaviors();

        scanBookButton = findViewById(R.id.scan_photo_ic);

        scanBookButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, CAMERA_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        // Render library view buttons.
        FragmentLibraryView fragmentLibraryView = new FragmentLibraryView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentLibraryView, fragmentLibraryView);
        fragmentTransaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        user.setBookList(bookList);
        if (!initialUser.equals(user)) {
            controller.updateUser(user);
            Gson gson = new Gson();
            String json = gson.toJson(user);
            editor.putString("user", json);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleReceivedBook();
    }

    private void handleReceivedBook() {
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);
        initialUser = new User(user);

        if (user != null && user.getBookList() != null) {
            bookList.clear();
            bookList.addAll(user.getBookList());
        } else {
            bookList.clear();
        }
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

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (resultCode != RESULT_OK) {
            showMessage("Unexpected result code: " + resultCode);
            return;
        }

        if (requestCode == REQUEST_CODE_UPLOAD_FILE) {
            if (resultData == null || resultData.getData() == null) {
                Log.e(LOG_TAG, "Null result data for file selection");
                showMessage("No file selected.");
                return;
            }

            Uri fileUri = resultData.getData();
            Log.d(LOG_TAG, "Chosen file URI: " + fileUri);

            String mimeType = getContentResolver().getType(fileUri);
            if (mimeType != null && (mimeType.equals(PNG_MIME_TYPE) || mimeType.equals(JPEG_MIME_TYPE))) {
                extractTextFromImage(fileUri).thenAccept(this::processExtractedContent);
            } else {
                String extractedText = extractTextFromDocumentFile(fileUri);
                processExtractedContent(extractedText);
            }
        } else if (requestCode == CAMERA_CAPTURE_CODE) {
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(90);
            options.setFreeStyleCropEnabled(true);

            UCrop.of(photoUri, destinationUri)
                    .withAspectRatio(16, 9)
                    .withOptions(options)
                    .start(this);
        } else if (requestCode == UCROP_REQUEST_CODE) {
            if (resultData != null) {
                Uri croppedUri = UCrop.getOutput(resultData);
                if (croppedUri != null) {
                    processImage(croppedUri);
                } else {
                    showMessage("Failed to get cropped image URI.");
                }
            } else {
                Throwable cropError = UCrop.getError(resultData);
                if (cropError != null) {
                    Log.e(LOG_TAG, "Crop error", cropError);
                    showMessage("Crop error: " + cropError.getMessage());
                } else {
                    showMessage("Unknown error occurred during cropping.");
                }
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

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Tạo file để lưu ảnh tạm thời
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_photo.jpg");
        photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_CODE);
    }


//    private void processImage(Uri imageUri) {
//        try {
//            Bitmap bitmap = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
//                    ? ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri))
//                    : MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//
//            InputImage image = InputImage.fromBitmap(bitmap, 0);
//            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//
//            recognizer.process(image)
//                    .addOnSuccessListener(text -> {
//                        String extractedText = text.getText();
//                        if (!extractedText.isEmpty()) {
//                            // Lưu văn bản vào SharedPreferences
//                            editor.putString("content", extractedText);
//                            editor.apply();
//
//                            // Chuyển sang ReadingActivity
//                            Intent intent = new Intent(this, ReadingActivity.class);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(this, "No text detected in the image.", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(e ->
//                            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                    );
//        } catch (IOException e) {
//            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }


    private void processImage(Uri imageUri) {
        try {
            Bitmap bitmap = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    ? ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri))
                    : MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(image)
                    .addOnSuccessListener(text -> {
                        String extractedText = text.getText();
                        processExtractedContent(extractedText);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to scan text.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void processExtractedContent(String content) {
        if (content != null && !content.isEmpty()) {
            Book newBook = new Book(content);
            bookList.add(newBook);
            showBookDetailsDialog(newBook);
        } else {
            Toast.makeText(this, "No content detected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookDetailsDialog(Book book) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_book_details_layout);

        ImageView eraseButton = dialog.findViewById(R.id.eraseBtn);
        TextView wordCountTextView = dialog.findViewById(R.id.wordCountTextView);
        TextView bookTitle = dialog.findViewById(R.id.bookTitle);
        TextView bookAuthor = dialog.findViewById(R.id.bookAuthor);
        TextView bookSum = dialog.findViewById(R.id.bookSum);
        FrameLayout readButton = dialog.findViewById(R.id.readBtn);

        wordCountTextView.setText(String.valueOf(book.getTotalWord()));
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookSum.setText(book.getSum());

        readButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReadingActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }
}
