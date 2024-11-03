package com.example.myapplication.utils.textextractor;

import android.content.Context;
import android.net.Uri;

import java.io.InputStream;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;

public class EpubTextExtractionStrategy implements TextExtractionStrategy {
    private final Context context;
    public EpubTextExtractionStrategy(Context context) {
        this.context = context;
    }

    @Override
    public String extractText(Uri fileUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                return "Error opening epub file.";
            }

            EpubReader epubReader = new EpubReader();
            Book book = epubReader.readEpub(inputStream);

            StringBuilder textBuilder = new StringBuilder();
            List<SpineReference> spineReferences = book.getSpine().getSpineReferences();
            for (SpineReference spineReference : spineReferences) {
                Resource resource = spineReference.getResource();
                // Get the chapter's content as string
                String content = new String(resource.getData(), "UTF-8");

                // Remove HTML tags and extra spaces
                content = content.replaceAll("<[^>]*>", " ")
                        .replaceAll("\\s+", " ")
                        .trim();
                textBuilder.append(content);

                // Add spacing between chapters
                // Don't add spacing if it's the last chapter.
                if (spineReference != spineReferences.get(spineReferences.size() - 1)) {
                    textBuilder.append("\n\n");
                }
            }

            inputStream.close();
            return textBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting text from epub file.";
        }
    }
}
