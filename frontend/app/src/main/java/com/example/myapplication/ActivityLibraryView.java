package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.model.Book;
import com.example.myapplication.model.LibraryView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActivityLibraryView extends AppCompatActivity {
    private FragmentLibraryViewTitle fragmentLibraryViewTitle;
    private FragmentLibraryBooksView fragmentLibraryBooksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_view);

        Intent intent = getIntent();
        String viewName = intent.getStringExtra("view_name");
        List<Book> books = new ArrayList<>();
        books.add(new Book("Harry Pot", "J.K. Rowling", "A magical world", "This is a magical world"));
        // Init fragments
        fragmentLibraryViewTitle = new FragmentLibraryViewTitle();
        fragmentLibraryBooksView = new FragmentLibraryBooksView();

        // Pass libraryView into fragments.
        Bundle bundle = new Bundle();
        bundle.putString("view_name", viewName);
        bundle.putSerializable("books", (Serializable) books);
        fragmentLibraryViewTitle.setArguments(bundle);
        fragmentLibraryBooksView.setArguments(bundle);

        replaceFragment(R.id.fragmentLibraryViewTitle, fragmentLibraryViewTitle);
        replaceFragment(R.id.fragmentLibraryBooksView, fragmentLibraryBooksView);
    }

    private void replaceFragment(int containerId, Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
