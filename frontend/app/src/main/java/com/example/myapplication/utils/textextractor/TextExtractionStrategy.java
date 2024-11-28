package com.example.myapplication.utils.textextractor;

import android.net.Uri;

public interface TextExtractionStrategy {
    /** Reads an android file content uri and extracts text from it. */
    String extractText(Uri fileUri);
}
