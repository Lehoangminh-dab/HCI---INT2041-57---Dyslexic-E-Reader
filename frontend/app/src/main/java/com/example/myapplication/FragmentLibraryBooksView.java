package com.example.myapplication;

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
import com.example.myapplication.model.Book;

import java.util.List;

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
        // Get books from argument
        Bundle bundle = getArguments();
        if (bundle != null) {
            books = (List<Book>) bundle.getSerializable("books");
        }

        BookAdapter2 bookAdapter = new BookAdapter2(requireActivity(), books);
        bookListView.setAdapter(bookAdapter);

        return view;
    }
}
