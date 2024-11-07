package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.LocationTextExtractionStrategy;

import java.io.InputStream;

public class PdfTextExtractionStrategy implements TextExtractionStrategy {
    private static final String LOG_TAG = "PdfTextExtractionStrategy";
    private final Context context;

    public PdfTextExtractionStrategy(Context context) {
        this.context = context;
    }

    @Override
    public String extractText(Uri fileUri) {
        StringBuilder extractedText = new StringBuilder();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputStream));
            for (int pageNum = 1; pageNum <= pdfDocument.getNumberOfPages(); pageNum++) {
                PdfPage page = pdfDocument.getPage(pageNum);
                String pageContent = PdfTextExtractor.getTextFromPage(page,
                        new LocationTextExtractionStrategy());
                extractedText.append(pageContent);
                // Add a new line between pages if it's not the last one.
                if (pageNum < pdfDocument.getNumberOfPages()) {
                    extractedText.append("\n");
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error extracting text from PDF file: " + e.getMessage());
        }
        return extractedText.toString();
    }
}
