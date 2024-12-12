package com.example.myapplication.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.model.Book;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BookController extends FireBaseController {

    private boolean deleted = false;

    public BookController() {
        super();
    }

    public BookController(Context context) {
        super(context);
    }

    public CompletableFuture<List<Book>> getAllBooks() {
        CompletableFuture<List<Book>> future = new CompletableFuture<>();

        database.getReference("Books").get().addOnSuccessListener(dataSnapshot -> {
            List<Book> books = new ArrayList<>();
            for (DataSnapshot data : dataSnapshot.getChildren()) {
                if (data.getValue(Book.class) != null) {
                    Book book = new Book(data.getValue(Book.class));
                    books.add(book);
                }
            }
            future.complete(books);
        });

        return future;
    }

    public void addBook(Book book) {
        if (book == null) {
            return;
        }
        database.getReference("Books").child(String.valueOf(book.getId())).setValue(book)
                .addOnFailureListener(e -> Log.e("Book", "Error saving book data"));
    }


    public boolean deleteBook(Book book) {
        if (book == null) {
            return deleted;
        }
        database.getReference("Books").child(String.valueOf(book.getId())).removeValue()
                .addOnSuccessListener(aVoid -> deleted = true)
                .addOnFailureListener(e -> Log.e("Book", "Error deleting color book data"));;
        return deleted;
    }
}
