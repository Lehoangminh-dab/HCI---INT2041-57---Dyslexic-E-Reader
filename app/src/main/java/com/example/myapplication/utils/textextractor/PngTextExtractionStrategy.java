package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class PngTextExtractionStrategy implements TextExtractionStrategy {
    private static final String LOG_TAG = "PngTextExtractionStrategy";
    private final Context context;
    private final TextRecognizer textRecognizer;
    private InputImage image;

    public PngTextExtractionStrategy(Context context) {
        this.context = context;
        this.textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }
    @Override
    public String extractText(Uri fileUri) {
        try {
            image = InputImage.fromFilePath(context, fileUri);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading image file: " + e);
        }

        Task<Text> result = textRecognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text visionText) {
                Log.d(LOG_TAG, "Image text recognition successful. ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(LOG_TAG, "Error extracting text from image: " + e);
            }
        });

        return "Png text extraction not implemented";
    }
}
