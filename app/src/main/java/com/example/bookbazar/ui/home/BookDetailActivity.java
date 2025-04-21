package com.example.bookbazar.ui.home;


import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.adapters.BookListingAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import coil.ImageLoader;
import coil.request.ImageRequest;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookDetailActivity extends AppCompatActivity {

    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookDescription;
    private RecyclerView relatedListingsRecyclerView;

    private BookListingAdapter adapter;
    private List<BookListing> relatedListings = new ArrayList<>();
    private FirebaseFirestore db;

    private String clientSecret;

    private String listingId, book_title, imageUrl, userId;
    private double bookPrice;
    private String extractedTitle = "";
    private String extractedAuthor = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);


        bookImage = findViewById(R.id.bookImage);
        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookDescription = findViewById(R.id.bookDescription);
        relatedListingsRecyclerView = findViewById(R.id.relatedListingsRecyclerView);

        relatedListingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookListingAdapter(this, relatedListings);
        relatedListingsRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        String workKey = getIntent().getStringExtra("workKey");
        String authorName = getIntent().getStringExtra("author");

        Log.d("workID", "Work Key " + workKey);

        if (workKey != null) {
            fetchBookDetails(workKey,authorName);
        }
    }

    private void fetchBookDetails(String workKey, String authorName) {
        String url = "https://openlibrary.org" + workKey + ".json";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("BookDetail", "Fetch failed", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    try {
                        JSONObject bookJson = new JSONObject(json);
                        extractedTitle = bookJson.optString("title", "Untitled");

                        String description = "";
                        if (bookJson.has("description")) {
                            Object descObj = bookJson.get("description");
                            if (descObj instanceof JSONObject) {
                                description = ((JSONObject) descObj).optString("value");
                            } else {
                                description = descObj.toString();
                            }
                        }

                        String imageUrl = null;
                        if (bookJson.has("covers")) {
                            JSONArray covers = bookJson.getJSONArray("covers");
                            if (covers.length() > 0) {
                                int coverId = covers.getInt(0);
                                imageUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
                            }
                        }



                        extractedAuthor = authorName;


                        String finalDesc = description;
                        String finalImageUrl = imageUrl;

                        runOnUiThread(() -> {
                            bookTitle.setText(extractedTitle);
                            bookAuthor.setText(extractedAuthor);
                            bookDescription.setText(finalDesc);

                            if (finalImageUrl != null) {
                                ImageLoader loader = new ImageLoader.Builder(BookDetailActivity.this).build();

                                ImageRequest request = new ImageRequest.Builder(BookDetailActivity.this)
                                        .data(finalImageUrl)
                                        .crossfade(true)
                                        .target(bookImage)
                                        .build();
                                loader.enqueue(request);
                            }

                           fetchRelatedListings(extractedTitle);
                        });

                    } catch (JSONException e) {
                        Log.e("BookDetail", "Parse error", e);
                    }
                }
            }
        });
    }




    private void fetchRelatedListings(String query) {
        FirebaseFirestore.getInstance()
                .collection("bookListing")
                .whereEqualTo("availability", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    relatedListings.clear();

                    for (DocumentSnapshot doc : querySnapshot) {
                        BookListing book = doc.toObject(BookListing.class);
                        if (book != null) {

                            boolean matches =
                                    (book.getTitle() != null && book.getTitle().toLowerCase().contains(query.toLowerCase())) ||
                                            (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(query.toLowerCase())) ||
                                            (book.getDescription() != null && book.getDescription().toLowerCase().contains(query.toLowerCase()));

                            if (matches) {
                                relatedListings.add(book);
                            }
                        }
                    }

                    adapter.setBookListings(relatedListings);
                });
    }


}
