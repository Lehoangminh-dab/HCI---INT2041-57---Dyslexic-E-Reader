package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.BookAdapter1;
import com.example.myapplication.adapter.BookAdapter2;
import com.example.myapplication.model.Book;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends AppCompatActivity {

//    private ConstraintLayout book1, book2, book3, book4, book5, book6, book7, book8, book9, book10, book11, book12;

    private RecyclerView favouriteListView, recommendedListView;
    private ListView  currentlyListView;
    private List<Book> favouriteList, recommendedList, currentlyList;
    private BookAdapter1 favouriteAdapter, recommendedAdapter;
    private BookAdapter2  currentlyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);


        favouriteListView = findViewById(R.id.favouriteListView);
        recommendedListView = findViewById(R.id.recommendedListView);
        currentlyListView = findViewById(R.id.currentlyListView);

        favouriteList = new ArrayList<>();
        recommendedList = new ArrayList<>();
        currentlyList = new ArrayList<>();


        //dữ liệu tạm
        favouriteList.add(new Book("The Alchemist1", 1,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        favouriteList.add(new Book("The Alchemist2", 2,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        favouriteList.add(new Book("The Alchemist3", 3,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        currentlyList.add(new Book("The Alchemist4", 4, "DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        currentlyList.add(new Book("The Alchemist5", 5,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        currentlyList.add(new Book("The Alchemist6", 6,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        recommendedList.add(new Book("The Alchemist7", 7,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        recommendedList.add(new Book("The Alchemist8", 8,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));
        recommendedList.add(new Book("The Alchemist9", 9,"DoThaiSon", "sum", "mot ngay nang da len vui di ta lo gi"));


        favouriteAdapter = new BookAdapter1(MainMenuActivity.this, favouriteList);
        recommendedAdapter = new BookAdapter1(MainMenuActivity.this, recommendedList);
        currentlyAdapter = new BookAdapter2(MainMenuActivity.this, currentlyList);

        LinearLayoutManager favouriteLayoutManager = new LinearLayoutManager(MainMenuActivity.this, LinearLayoutManager.HORIZONTAL, false);
        favouriteListView.setLayoutManager(favouriteLayoutManager);

        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(MainMenuActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recommendedListView.setLayoutManager(recommendedLayoutManager);


        favouriteListView.setAdapter(favouriteAdapter);
        recommendedListView.setAdapter(recommendedAdapter);
        currentlyListView.setAdapter(currentlyAdapter);


        loadFragment(R.id.fragmentToolbar, new FragmentToolbar());

        currentlyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainMenuActivity.this, BookDetailsActivity.class);
                // Truyền thông tin sách vào intent
                intent.putExtra("book", currentlyList.get(i));
                startActivity(intent);

            }
        });


    }

    private void loadFragment(int containerId, Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
