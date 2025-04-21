package com.example.bookbazar.ui.savedListings;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.adapters.BookListingAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SavedListings extends Fragment {

    private RecyclerView recyclerView;
    private BookListingAdapter adapter;
    private List<BookListing> savedList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_saved_list, container, false);

        recyclerView = view.findViewById(R.id.savedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookListingAdapter(getContext(), savedList);
        recyclerView.setAdapter(adapter);

        fetchSavedListings();

        return view;
    }

    private void fetchSavedListings() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (currentUserId == null) return;

        FirebaseFirestore.getInstance()
                .collection("bookListing")
                .whereEqualTo("saved", true)
                .whereEqualTo("userId", currentUserId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    savedList.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        BookListing book = doc.toObject(BookListing.class);
                        if (book != null) {
                            savedList.add(book);
                        }
                    }
                    adapter.setBookListings(savedList);
                });
    }
}

