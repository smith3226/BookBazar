package com.example.bookbazar.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.adapters.FeaturedBooksAdapter;
import com.example.bookbazar.ui.home.adapters.PopularCategoriesAdapter;
import com.example.bookbazar.ui.home.models.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView featuredBooksRecyclerView, popularCategoriesRecyclerView;
    private FeaturedBooksAdapter featuredBooksAdapter;
    private PopularCategoriesAdapter popularCategoriesAdapter;

    private List<Book> featuredBooksList = new ArrayList<>();
    private List<Book> popularCategoriesList = new ArrayList<>();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup Featured Books RecyclerView
        featuredBooksRecyclerView = view.findViewById(R.id.featuredBooksRecyclerView);
        featuredBooksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredBooksAdapter = new FeaturedBooksAdapter(getContext(), featuredBooksList, book -> {
            // Handle book click (e.g., Open Book Details)
        });
        featuredBooksRecyclerView.setAdapter(featuredBooksAdapter);

        // Setup Popular Categories RecyclerView
        popularCategoriesRecyclerView = view.findViewById(R.id.popularCategoriesRecyclerView);
        popularCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularCategoriesAdapter = new PopularCategoriesAdapter(getContext(), popularCategoriesList);
        popularCategoriesRecyclerView.setAdapter(popularCategoriesAdapter);

        // Load Books from Firestore
        fetchFeaturedBooks();
       // fetchPopularCategories();

        return view;
    }

    // Fetch featured books directly from Firestore
    private void fetchFeaturedBooks() {
        db.collection("books")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    featuredBooksList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String title = document.getString("title");
                        String author = document.getString("author");
                        String genre = document.getString("genre");
                        Double price = document.getDouble("price");
                        String condition = document.getString("condition");
                        String imageUrl = document.getString("imageUrl");
                        String description = document.getString("description");

                        if (title != null && author != null && genre != null && price != null && condition != null && imageUrl != null) {
                            featuredBooksList.add(new Book(title, author, genre, price, condition, description));
                        }
                        Log.d("HomeFragment", "Book added: " + title);
                    }
                    featuredBooksAdapter.setBooks(featuredBooksList); // Update RecyclerView
                })
                .addOnFailureListener(e -> {
                    // Handle error (e.g., show a Toast message)
                });
    }

    // Fetch popular categories directly from Firestore
//    private void fetchPopularCategories() {
//        db.collection("categories") // Ensure you have this collection in Firestore
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    popularCategoriesList.clear();
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String categoryName = document.getString("name");
//                        String categoryImageUrl = document.getString("imageUrl");
//                        if (categoryName != null && categoryImageUrl != null) {
//                            popularCategoriesList.add(new Book(categoryName, "", "", 0.0, "", categoryImageUrl));
//                        }
//                    }
//                    popularCategoriesAdapter.setBooks(popularCategoriesList); // Update RecyclerView
//                })
//                .addOnFailureListener(e -> {
//                    // Handle error
//                });
    }

