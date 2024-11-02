package com.example.myapplication.utils.textextractor;

import android.net.Uri;
import android.util.Log;

public class TextExtractorUtil {
    private static final String LOG_TAG = "TextExtractorUtil";
    private final TextExtractionStrategy textExtractionStrategy;

    public TextExtractorUtil(TextExtractionStrategy textExtractionStrategy) {
        this.textExtractionStrategy = textExtractionStrategy;
    }

    public String extractText(Uri fileUri) {
        Log.d(LOG_TAG, "Extracting text from file: " + fileUri);
        return textExtractionStrategy.extractText(fileUri);
    }
}
