package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.model.Book;

import java.util.List;

public class BookAdapter2 extends ArrayAdapter<Book> {

    private final Context context;
    private final List<Book> books;

    public BookAdapter2(Context context, List<Book> books) {
        super(context, 0, books);
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_book_layout2, parent, false);
        }

        Book book = books.get(position);

        TextView bookTitle2 = view.findViewById(R.id.bookTitle2);
        TextView totalWord2= view.findViewById(R.id.totalWord2);
        ImageView bookPoster2 = view.findViewById(R.id.bookPoster2);

        bookTitle2.setText(book.getTitle());
        totalWord2.setText(String.format("%d", book.getTotalWord()));


        return view;
    }
}
