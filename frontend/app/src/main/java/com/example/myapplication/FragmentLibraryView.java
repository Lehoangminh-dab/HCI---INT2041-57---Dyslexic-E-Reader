package com.example.myapplication;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adapter.LibraryViewAdapter;
import com.example.myapplication.model.Book;
import com.example.myapplication.model.LibraryView;

import java.util.ArrayList;
import java.util.List;

public class FragmentLibraryView extends Fragment {
    private ListView libraryListView;
    private LibraryViewAdapter libraryViewAdapter;
    private List<LibraryView> libraryViews;
    public FragmentLibraryView() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_library_view, container, false);
        libraryListView = view.findViewById(R.id.list_library_view);
        libraryViews = createLibraryViews();
        libraryViewAdapter = new LibraryViewAdapter(requireActivity(), libraryViews);
        libraryListView.setAdapter(libraryViewAdapter);
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

    // TODO: Retrieve data from firebase.
    private LibraryView createYourBooksView() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Harry Pot", 100, "J.K. Rowling", "A magical world",
                "This is a magical world"));
        Drawable yourBooksIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_your_books,
                null);
        return new LibraryView("Your Books", allBooks, yourBooksIcon);
    }

    // TODO: Retrieve data from firebase.
    private LibraryView createFavouritesView() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Harry Pot", 100, "J.K. Rowling", "A magical world",
                "This is a magical world"));
        Drawable favouritesIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_favourites,
                null);
        return new LibraryView("Favourites", allBooks, favouritesIcon);
    }

    // TODO: Retrieve data from firebase.
    private LibraryView createReadingView() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Harry Pot", 100, "J.K. Rowling", "A magical world",
                "This is a magical world"));
        Drawable readingIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_reading,
                null);
        return new LibraryView("Reading", allBooks, readingIcon);
    }

    // TODO: Retrieve data from firebase.
    private LibraryView createCompletedView() {
        List<Book> allBooks = new ArrayList<>();
        allBooks.add(new Book("Harry Pot", 100, "J.K. Rowling", "A magical world",
                "This is a magical world"));
        Drawable completedIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_completed,
                null);
        return new LibraryView("Completed", allBooks, completedIcon);
    }
}
