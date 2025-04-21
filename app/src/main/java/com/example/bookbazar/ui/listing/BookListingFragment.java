package com.example.bookbazar.ui.listing;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.adapters.BookListingAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

public class BookListingFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookListingAdapter adapter;
    private final List<BookListing> listings = new ArrayList<>();

    private FirebaseFirestore db;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_listing, container, false);

        recyclerView = view.findViewById(R.id.bookListingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new BookListingAdapter(getContext(), listings);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchListings();

        return view;
    }


    private void setListingIds(QuerySnapshot querySnapshot) {
        List<BookListing> listings = new ArrayList<>();

        for (QueryDocumentSnapshot doc : querySnapshot) {
            BookListing book = doc.toObject(BookListing.class);
            book.setId(UUID.randomUUID().toString());
            Log.d("FinalIDTest", "ID set for: " + book.getTitle() + " = " + book.getId());
            listings.add(book);
        }

        for (BookListing b : listings) {
            Log.d("ToAdapter", b.getTitle() + " → ID: " + b.getId());
        }

        adapter.setBookListings(listings);
    }



    private void fetchListings() {
        db.collection("bookListing")
                .get()
                .addOnSuccessListener(this::setListingIds)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load listings", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error fetching listings", e);
                });
    }

}