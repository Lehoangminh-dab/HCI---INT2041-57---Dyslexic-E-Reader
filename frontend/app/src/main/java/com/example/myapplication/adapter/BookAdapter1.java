package com.example.myapplication.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BookDetailsActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Book;

import java.util.List;

public class BookAdapter1 extends RecyclerView.Adapter<BookAdapter1.BookAdapter1Holder> {
    private final List<Book> books;
    private Context context;
    public BookAdapter1(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public BookAdapter1Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_book_layout1, parent, false);
        return new BookAdapter1Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter1Holder holder, int position) {
        Book book = books.get(position);
        if(book == null){
            return;
        }
        holder.bookTitle1.setText(book.getTitle());
        holder.bookLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetails(book);
            }
        });
    }

    private void onClickGoToDetails(Book book) {
        Intent intent = new Intent(context, BookDetailsActivity.class);
        intent.putExtra("book", book);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if (books != null) {
            return books.size();
        }
        return 0;
    }

    class BookAdapter1Holder extends RecyclerView.ViewHolder {
        private ConstraintLayout bookLayout1;
        private final TextView bookTitle1;
        private final ImageView bookPoster1;

        public BookAdapter1Holder(@NonNull View itemView) {
            super(itemView);
            bookLayout1 = itemView.findViewById(R.id.bookLayout1);
            bookTitle1 = itemView.findViewById(R.id.bookTitle1);
            bookPoster1 = itemView.findViewById(R.id.bookPoster1);

        }
    }



}
