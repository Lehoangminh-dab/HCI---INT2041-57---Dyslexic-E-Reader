package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adapter.BookAdapter2;
import com.example.myapplication.adapter.LibraryViewAdapter;
import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class FragmentLibraryBooksView extends Fragment {
    private ListView bookListView;
    private List<Book> books;
    private ImageView backButton;

    public FragmentLibraryBooksView() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Render the fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_library_books_view, container, false);
        bookListView = view.findViewById(R.id.book_list_view);
        backButton = view.findViewById(R.id.back_button);
        // Get books from argument
        Bundle bundle = getArguments();
        if (bundle != null) {
            books = (List<Book>) bundle.getSerializable("books");
        }

        BookAdapter2 bookAdapter = new BookAdapter2(requireActivity(), books);
        bookListView.setAdapter(bookAdapter);

        // Set back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }
}
