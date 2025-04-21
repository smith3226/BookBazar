package com.example.bookbazar.ui.search;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.adapters.BookListingAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private BookListingAdapter adapter;
    private List<BookListing> fullList = new ArrayList<>();
    private List<BookListing> filteredList = new ArrayList<>();
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    private TextView noResultsText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        noResultsText = view.findViewById(R.id.noResultsText);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 🔁 changed from GridLayoutManager
        adapter = new BookListingAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        fetchAllListings();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> filterResults(s.toString());
                searchHandler.postDelayed(searchRunnable, 300); // Debounce: 300ms delay
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchAllListings() {
        FirebaseFirestore.getInstance()
                .collection("bookListing")
                .whereEqualTo("availability", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    fullList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        BookListing book = doc.toObject(BookListing.class);
                        if (book != null) {
                            fullList.add(book);
                        }
                    }
                    filteredList.clear();
                    filteredList.addAll(fullList);
                    adapter.setBookListings(filteredList);
                    updateNoResultsVisibility();
                });
    }

    private void filterResults(String query) {
        filteredList.clear();
        for (BookListing book : fullList) {
            if ((book.getTitle() != null && book.getTitle().toLowerCase().contains(query.toLowerCase())) ||
                    (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(query.toLowerCase())) ||
                    (book.getDescription() != null && book.getDescription().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(book);
            }
        }
        adapter.setBookListings(filteredList);
        updateNoResultsVisibility();
    }

    private void updateNoResultsVisibility() {
        if (noResultsText != null) {
            noResultsText.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
