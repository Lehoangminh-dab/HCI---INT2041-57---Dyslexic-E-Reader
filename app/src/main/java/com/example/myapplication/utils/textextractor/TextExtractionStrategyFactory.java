package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.util.Log;

public class TextExtractionStrategyFactory {
    private static final String LOG_TAG = "TextExtractionStrategyFactory";
    private static final String EPUB_MIME_TYPE = "application/epub+zip";
    private static final String PDF_MIME_TYPE = "application/pdf";
    private static final String JPEG_MIME_TYPE = "image/jpeg";
    private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String PPT_MIME_TYPE = "application/vnd.ms-powerpoint";
    private static final String PPTX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    private static final String PNG_MIME_TYPE = "image/png";

    public static TextExtractionStrategy createStrategy(Context context, String mimeType) {
        if (mimeType == null) {
            Log.e(LOG_TAG, "Null mime type");
            return null;
        }
        switch (mimeType) {
            case DOCX_MIME_TYPE:
                return new DocxTextExtractionStrategy(context);
            case EPUB_MIME_TYPE:
                return new EpubTextExtractionStrategy(context);
            case PDF_MIME_TYPE:
                return new PdfTextExtractionStrategy(context);
        }

        Log.e(LOG_TAG, "Unsupported mime type: " + mimeType);
        return null;
    }
}
