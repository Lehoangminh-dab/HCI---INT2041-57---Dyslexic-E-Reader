package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ImageTextExtractor {
    private static final String LOG_TAG = "ImageTextExtractionStrategy";
    private final Context context;
    private final TextRecognizer textRecognizer;

    public ImageTextExtractor(Context context) {
        this.context = context;
        this.textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    public CompletableFuture<String> extractText(Uri fileUri) {
        CompletableFuture<String> future = new CompletableFuture<>();

        try {
            InputImage image = InputImage.fromFilePath(context, fileUri);
            textRecognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String extractedText = visionText.getText();
                        if (extractedText.isEmpty()) {
                            Log.w(LOG_TAG, "Extracted text is empty");
                        } else {
                            Log.d(LOG_TAG, "Successfully extracted text: " + extractedText);
                        }
                        future.complete(extractedText);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(LOG_TAG, "Error extracting text from image: " + e);
                        future.completeExceptionally(e);
                    });
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading image file: " + e);
            future.completeExceptionally(e);
        }

        return future;
    }
}