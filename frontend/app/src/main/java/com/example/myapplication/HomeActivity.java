package com.example.myapplication;

import static android.Manifest.permission.CAMERA;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;



import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private ImageView openBookButton;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_CODE = 101;
    private static final int UCROP_REQUEST_CODE = UCrop.REQUEST_CROP;

    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        openBookButton = findViewById(R.id.openBookBtn);

        openBookButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, CAMERA_REQUEST_CODE);
            } else {
                openCamera();
            }
        });

        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Tạo file để lưu ảnh tạm thời
        File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_photo.jpg");
        photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_CODE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
//            // Gọi UCrop để cắt ảnh
//            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));
//            UCrop.of(photoUri, destinationUri)
//                    .start(this);
//        } else if (requestCode == UCROP_REQUEST_CODE && resultCode == RESULT_OK) {
//            // Lấy ảnh đã cắt
//            Uri croppedUri = UCrop.getOutput(data);
//            if (croppedUri != null) {
//                processImage(croppedUri);
//            }
//        } else if (requestCode == UCROP_REQUEST_CODE) {
//            // UCrop thất bại
//            Throwable cropError = UCrop.getError(data);
//            if (cropError != null) {
//                Toast.makeText(this, "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_CODE && resultCode == RESULT_OK) {
            // Gọi UCrop để cắt ảnh
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(90);
            options.setFreeStyleCropEnabled(true); // Cho phép người dùng thay đổi khung cắt tự do
            options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));

            // Thiết lập tỷ lệ khung cắt (nếu cần cố định)
            UCrop.of(photoUri, destinationUri)
                    .withAspectRatio(16, 9) // Tỷ lệ khung cắt 16:9
                    .withOptions(options)
                    .start(this);
        } else if (requestCode == UCROP_REQUEST_CODE && resultCode == RESULT_OK) {
            // Lấy ảnh đã cắt
            Uri croppedUri = UCrop.getOutput(data);
            if (croppedUri != null) {
                processImage(croppedUri);
            }
        } else if (requestCode == UCROP_REQUEST_CODE) {
            // UCrop thất bại
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, "Crop error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


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
                        if (!extractedText.isEmpty()) {
                            // Lưu văn bản vào SharedPreferences
                            editor.putString("content", extractedText);
                            editor.apply();

                            // Chuyển sang ReadingActivity
                            Intent intent = new Intent(this, ReadingActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "No text detected in the image.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
}
