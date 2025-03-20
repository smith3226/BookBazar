package com.example.bookbazar.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.adapters.FeaturedBooksAdapter;
import com.example.bookbazar.ui.home.adapters.PopularCategoriesAdapter;
import com.example.bookbazar.ui.home.models.Book;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView featuredBooksRecyclerView;
    private FeaturedBooksAdapter featuredBooksAdapter;

    private RecyclerView popularCategoriesRecyclerView;
    private PopularCategoriesAdapter popularCategoriesAdapter;
    private List<Book> featuredBooksList;

    private List<Book> popularCategoriesList;

    // onCreateView instead of onCreate for fragments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false); // Use your fragment layout here

        // Initialize the RecyclerView and adapter
        featuredBooksRecyclerView = view.findViewById(R.id.featuredBooksRecyclerView);
        featuredBooksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Add sample books
        featuredBooksList = new ArrayList<>();
        featuredBooksList.add(new Book("The Alchemist", "Paulo Coelho", R.drawable.alchemist));
        featuredBooksList.add(new Book("1984", "George Orwell", R.drawable.alchemist));
        featuredBooksList.add(new Book("To Kill a Mockingbird", "Harper Lee", R.drawable.alchemist));
        featuredBooksList.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", R.drawable.alchemist));

        featuredBooksAdapter = new FeaturedBooksAdapter(featuredBooksList);
        featuredBooksRecyclerView.setAdapter(featuredBooksAdapter);


        // Initialize the RecyclerView and adapter for Popular Categories
        popularCategoriesRecyclerView = view.findViewById(R.id.popularCategoriesRecyclerView);
        popularCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Add sample popular categories
        popularCategoriesList = new ArrayList<>();
        popularCategoriesList.add(new Book("The Alchemist", "Paulo Coelho", R.drawable.alchemist));
        popularCategoriesList.add(new Book("The Alchemist", "Paulo Coelho", R.drawable.alchemist));
        popularCategoriesList.add(new Book("The Alchemist", "Paulo Coelho", R.drawable.alchemist));
        popularCategoriesList.add(new Book("The Alchemist", "Paulo Coelho", R.drawable.alchemist));
        popularCategoriesList.add(new Book("The Alchemist", "Paulo Coelho", R.drawable.alchemist));

        // Set up the adapter for Popular Categories
        popularCategoriesAdapter = new PopularCategoriesAdapter(popularCategoriesList);
        popularCategoriesRecyclerView.setAdapter(popularCategoriesAdapter);

        return view; // Return the view that you inflated
    }
}
