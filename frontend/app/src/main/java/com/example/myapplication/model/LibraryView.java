package com.example.myapplication.model;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.List;

public class LibraryView implements Serializable {
    private String name;
    private List<Book> books;
    private Drawable viewIcon;

    public LibraryView(String name, List<Book> books, Drawable viewIcon) {
        this.name = name;
        this.books = books;
        this.viewIcon = viewIcon;
    }

    public String getName() {
        return name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Drawable getViewIcon() {
        return viewIcon;
    }
}
