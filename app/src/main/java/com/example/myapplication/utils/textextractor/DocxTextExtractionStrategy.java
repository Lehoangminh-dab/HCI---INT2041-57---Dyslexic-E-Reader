package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.net.Uri;

public class DocxTextExtractionStrategy implements TextExtractionStrategy {
    private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument" +
            ".wordprocessingml.document";

    private final Context context;

    public DocxTextExtractionStrategy(Context context) {
        this.context = context;
    }

    @Override
    public String extractText(Uri fileUri) {
        return "Docx extract text not implemented";
    }
}
