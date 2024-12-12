package com.example.myapplication.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.ReadingActivity;
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
        holder.bookLayout1.setOnClickListener(v -> showBookDetailsDialog(book));
    }

    private void showBookDetailsDialog(Book book) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_book_details_layout);

        ImageView eraseButton = dialog.findViewById(R.id.eraseBtn);
        TextView wordCountTextView = dialog.findViewById(R.id.wordCountTextView);
        TextView bookTitle = dialog.findViewById(R.id.bookTitle);
        TextView bookAuthor = dialog.findViewById(R.id.bookAuthor);
        TextView bookSum = dialog.findViewById(R.id.bookSum);
        FrameLayout readButton = dialog.findViewById(R.id.readBtn);

        wordCountTextView.setText(String.valueOf(book.getTotalWord()));
        bookTitle.setText(book.getTitle());
        bookAuthor.setText(book.getAuthor());
        bookSum.setText(book.getSum());

        readButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReadingActivity.class);
            intent.putExtra("book", book);
            context.startActivity(intent);
        });

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
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