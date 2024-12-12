package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adapter.BookAdapter2;
import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.LibraryView;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentLibraryBooksView extends Fragment {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private UserController controller;
    private User user;

    private BookAdapter2 bookAdapter;

    private ListView bookListView;
    private List<Book> bookList;
    private List<Book> thisBookList;
    private String nameList;

    public FragmentLibraryBooksView() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Render the fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_library_books_view, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        controller = new UserController(requireActivity());

        bookListView = view.findViewById(R.id.book_list_view);

        bookList = new ArrayList<>();
        thisBookList = new ArrayList<>();

        bookAdapter = new BookAdapter2(requireActivity(), thisBookList);

        Bundle bundle = getArguments();
        if (bundle != null) {
            nameList = bundle.getString("view_name");
        }

        handleReceivedBook();

        bookListView.setAdapter(bookAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showBookDetailsDialog(bookList.get(i));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handleReceivedBook();
    }

    private void handleReceivedBook() {
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);

        if (user != null && user.getBookList() != null) {
            bookList.clear();
            bookList.addAll(user.getBookList());

            thisBookList.clear();
            if (nameList.equals("Your Books")) {
                thisBookList.addAll(bookList);
                if (!thisBookList.isEmpty()) {
                    Log.e("000000", "00000000");
                }
            } else if (nameList.equals("Favourites")) {
                for (Book book : bookList) {
                    if (book.getIsFavourite().equals("true")) {
                        thisBookList.add(book);
                    }
                }
            } else if (nameList.equals("Reading")) {
                for (Book book : bookList) {
                    if (book.getIsComplete().equals("false")) {
                        thisBookList.add(book);
                    }
                }
            } else if (nameList.equals("Completed")) {
                for (Book book : bookList) {
                    if (book.getIsComplete().equals("true")) {
                        thisBookList.add(book);
                    }
                }
            }
        } else {
            thisBookList.clear();
            bookList.clear();
        }

        bookAdapter.notifyDataSetChanged();
    }

    private void showBookDetailsDialog(Book book) {
        Dialog dialog = new Dialog(requireActivity());
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
            Intent intent = new Intent(requireActivity(), ReadingActivity.class);
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
}
