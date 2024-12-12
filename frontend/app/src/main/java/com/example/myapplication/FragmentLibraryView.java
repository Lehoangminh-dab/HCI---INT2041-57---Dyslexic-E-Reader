package com.example.myapplication;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adapter.LibraryViewAdapter;
import com.example.myapplication.controller.UserController;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.LibraryView;
import com.example.myapplication.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentLibraryView extends Fragment {
    private SharedPreferences sharedPreferences;
    private ListView libraryListView;
    private LibraryViewAdapter libraryViewAdapter;
    private List<LibraryView> libraryViews;
    private UserController userController;
    private User user;
    private List<Book> allBooks;
    public FragmentLibraryView() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Render the fragment
        super.onCreateView(inflater, container, savedInstanceState);
        // Get data from firebase
        sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonRetrieved = sharedPreferences.getString("user", null);
        Type type = new TypeToken<User>() {}.getType();
        user = gson.fromJson(jsonRetrieved, type);
        allBooks = new ArrayList<>();
        if (user != null && user.getBookList() != null) {
            allBooks.addAll(user.getBookList());
        }
        // Render view
        View view = inflater.inflate(R.layout.fragment_library_view, container, false);
        libraryListView = view.findViewById(R.id.list_library_view);
        libraryViews = createLibraryViews();
        libraryViewAdapter = new LibraryViewAdapter(requireActivity(), libraryViews);
        libraryListView.setAdapter(libraryViewAdapter);
        // Set item click listener
        libraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LibraryView selectedLibraryView = libraryViews.get(position);

                // Start activity for ActivityLibraryView
                Intent intent = new Intent(requireActivity(), ActivityLibraryView.class);
                intent.putExtra("view_name", selectedLibraryView.getName());
                intent.putExtra("books", (Serializable) selectedLibraryView.getBooks());
                startActivity(intent);
            }
        });

        return view;
    }

    private List<LibraryView> createLibraryViews() {
        List<LibraryView> libraryViews = new ArrayList<>();
        libraryViews.add(createYourBooksView());
        libraryViews.add(createFavouritesView());
        libraryViews.add(createReadingView());
        libraryViews.add(createCompletedView());

        return libraryViews;
    }

    // Modify these methods to use the class's allBooks
    private LibraryView createYourBooksView() {
        Drawable yourBooksIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_your_books, null);
        return new LibraryView("Your Books", new ArrayList<>(allBooks), yourBooksIcon);
    }

    private LibraryView createFavouritesView() {
        // Filter favourites if needed
        List<Book> favouriteBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getIsFavourite().equals("true")) {
                favouriteBooks.add(book);
            }
        }
        Drawable favouritesIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favourites, null);
        return new LibraryView("Favourites", favouriteBooks, favouritesIcon);
    }

    private LibraryView createReadingView() {
        // Filter reading books if needed
        List<Book> readingBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getIsComplete().equals("false")) {
                readingBooks.add(book);
            }
        }

        Drawable readingIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_reading, null);
        return new LibraryView("Reading", readingBooks, readingIcon);
    }

    private LibraryView createCompletedView() {
        // Filter completed books if needed
        List<Book> completedBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getIsComplete().equals("true")) {
                completedBooks.add(book);
            }
        }
        Drawable completedIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_completed, null);
        return new LibraryView("Completed", completedBooks, completedIcon);
    }
}

