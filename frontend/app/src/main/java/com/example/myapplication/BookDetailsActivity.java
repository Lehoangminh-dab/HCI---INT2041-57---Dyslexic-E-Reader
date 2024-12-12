package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Book;

public class BookDetailsActivity extends AppCompatActivity {

    private Button readBtn, wordCountBtn;
    private ImageView bookPoster, eraseBtn;
    private TextView bookTitle, bookAuthor, bookSum, yesBtn, noBtn;
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_details);

        readBtn = findViewById(R.id.readBtn);
        wordCountBtn = findViewById(R.id.wordCountBtn);
        bookPoster = findViewById(R.id.bookPoster);
        eraseBtn = findViewById(R.id.eraseBtn);
        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookSum = findViewById(R.id.bookSum);

        //Receive data from previous activity
        Intent receivedIntent = getIntent();
        Book currentBook = (Book) receivedIntent.getSerializableExtra("book");

        wordCountBtn.setText(String.valueOf(String.format("%d word", currentBook.getTotalWord())));
        bookTitle.setText(currentBook.getTitle());
        bookAuthor.setText(currentBook.getAuthor());
        bookSum.setText(currentBook.getSum());


        Dialog eraseFragment = new Dialog(BookDetailsActivity.this);
        eraseFragment.setContentView(R.layout.fragment_erase);
        yesBtn = eraseFragment.findViewById(R.id.yesBtn);
        noBtn = eraseFragment.findViewById(R.id.noBtn);

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, ReadingActivity.class);
                intent.putExtra("book", currentBook);
                startActivity(intent);
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, MainMenuActivity.class);
                startActivity(intent);
                eraseFragment.dismiss();
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eraseFragment.dismiss();
            }
        });

        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eraseFragment.show();
            }
        });
    }
}