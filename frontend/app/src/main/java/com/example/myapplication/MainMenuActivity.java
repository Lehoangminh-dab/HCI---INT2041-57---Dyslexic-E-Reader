package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.BookAdapter1;
import com.example.myapplication.adapter.BookAdapter2;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private User user;

    private String userName;
    private List<Book> bookList;
    private List<Book> favouriteList, recommendedList;
    private BookAdapter1 favouriteAdapter, recommendedAdapter;
    private BookAdapter2  currentlyAdapter;

    private TextView menuTitle;
    private RecyclerView favouriteListView, recommendedListView;
    private ListView  currentlyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        menuTitle = findViewById(R.id.menuTitle);
        favouriteListView = findViewById(R.id.favouriteListView);
        LinearLayoutManager favouriteLayoutManager = new LinearLayoutManager(MainMenuActivity.this, LinearLayoutManager.HORIZONTAL, false);
        favouriteListView.setLayoutManager(favouriteLayoutManager);
        recommendedListView = findViewById(R.id.recommendedListView);
        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(MainMenuActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recommendedListView.setLayoutManager(recommendedLayoutManager);
        currentlyListView = findViewById(R.id.currentlyListView);


        favouriteList = new ArrayList<>();
        recommendedList = new ArrayList<>();
        bookList = new ArrayList<>();

        favouriteAdapter = new BookAdapter1(MainMenuActivity.this, favouriteList);
        recommendedAdapter = new BookAdapter1(MainMenuActivity.this, recommendedList);
        currentlyAdapter = new BookAdapter2(MainMenuActivity.this, bookList);

        handleReceivedBook();

        menuTitle.setText("Hello " + userName);

        favouriteListView.setAdapter(favouriteAdapter);
        recommendedListView.setAdapter(recommendedAdapter);
        currentlyListView.setAdapter(currentlyAdapter);

        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());

        currentlyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showBookDetailsDialog(bookList.get(i));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleReceivedBook();
    }

    private void handleReceivedBook() {
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);

        if (user != null && user.getBookList() != null) {
            userName = user.getName();
            bookList.clear();
            bookList.addAll(user.getBookList());

            favouriteList.clear();
            recommendedList.clear();
            for (Book book : bookList) {
                if (book.getIsFavourite().equals("true")) {
                    favouriteList.add(book);
                }
                if (book.getIsOurBook().equals("true")) {
                    recommendedList.add(book);
                }
            }
        } else {
            bookList.clear();
        }

        favouriteAdapter.notifyDataSetChanged();
        recommendedAdapter.notifyDataSetChanged();
        currentlyAdapter.notifyDataSetChanged();
    }

    private void showBookDetailsDialog(Book book) {
        Dialog dialog = new Dialog(this);
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
            Intent intent = new Intent(this, ReadingActivity.class);
            intent.putExtra("book", book);
            startActivity(intent);
        });

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            dialog.getWindow().setAttributes(params);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
