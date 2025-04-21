package com.example.bookbazar.ui.home;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.adapters.BookListingAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GenreListingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookListingAdapter adapter;
    private List<BookListing> genreListings = new ArrayList<>();
    private FirebaseFirestore db;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String genre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_listing);

        genre = getIntent().getStringExtra("genre");

        Toolbar toolbar = findViewById(R.id.genreToolbar);
        toolbar.setTitle(genre);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable back button
        }

        recyclerView = findViewById(R.id.genreListingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookListingAdapter(this, genreListings);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.genreSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> fetchListingsByGenre(genre));

        db = FirebaseFirestore.getInstance();
        fetchListingsByGenre(genre);
    }

    private void fetchListingsByGenre(String genre) {
        swipeRefreshLayout.setRefreshing(true);
        db.collection("bookListing")
                .whereEqualTo("availability", true)
                .whereEqualTo("genre", genre)
                .get()
                .addOnSuccessListener(snapshot -> {
                    genreListings.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        BookListing book = doc.toObject(BookListing.class);
                        if (book != null) {
                            genreListings.add(book);
                        }
                    }
                    adapter.setBookListings(genreListings);
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load listings", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Go back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
