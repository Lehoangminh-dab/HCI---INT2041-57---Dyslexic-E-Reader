package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DocxTextExtractionStrategy implements TextExtractionStrategy {
    private final Context context;

    public DocxTextExtractionStrategy(Context context) {
        this.context = context;
    }

    @Override
    public String extractText(Uri fileUri) {
        DocumentConverter documentConverter = new DocumentConverter();
        InputStream inputStream;
        String textExtracted;
        try {
            inputStream = context.getContentResolver().openInputStream(fileUri);
            Result<String> extractionResult = documentConverter.extractRawText(inputStream);
            textExtracted = extractionResult.getValue();
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException(fnfe);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return textExtracted;
    }
}
