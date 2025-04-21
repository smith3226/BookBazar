package com.example.bookbazar.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.adapters.FeaturedBooksAdapter;
import com.example.bookbazar.ui.home.adapters.GenresAdapter;
import com.example.bookbazar.ui.home.adapters.TextBooksAdapter;
import com.example.bookbazar.ui.home.models.Book;
import com.example.bookbazar.ui.home.models.Genre;
import com.example.bookbazar.ui.listing.adapters.BookListingAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private RecyclerView featuredBooksRecyclerView, textBooksRecyclerView, popularCategoriesRecyclerView, bookListingRecyclerView;
    private FeaturedBooksAdapter featuredBooksAdapter;
    private GenresAdapter genresAdapter;
    private BookListingAdapter bookListingAdapter;

    private TextBooksAdapter textBooksAdapter;

    private ProgressBar trendingLoading, textbookLoading;
    private TextView noListingsText;


    private final List<Book> featuredBooksList = new ArrayList<>();
    private final List<Book> textBooksList = new ArrayList<>();
    private final List<Genre> genreList = new ArrayList<>();
    private final List<BookListing> bookListings = new ArrayList<>();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        // Setup Featured Books
        trendingLoading = view.findViewById(R.id.trendingLoading);
        featuredBooksRecyclerView = view.findViewById(R.id.featuredBooksRecyclerView);
        featuredBooksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        featuredBooksAdapter = new FeaturedBooksAdapter(getContext(), featuredBooksList, book -> {});
        featuredBooksRecyclerView.setAdapter(featuredBooksAdapter);

        // Setup Text Books
        textbookLoading = view.findViewById(R.id.textbookLoading);
        textBooksRecyclerView = view.findViewById(R.id.textBooksRecyclerView);
        textBooksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        textBooksAdapter = new TextBooksAdapter(getContext(), textBooksList, book -> {});

        textBooksRecyclerView.setAdapter(textBooksAdapter);

        // Setup Genres
        popularCategoriesRecyclerView = view.findViewById(R.id.popularCategoriesRecyclerView);
        popularCategoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        genresAdapter = new GenresAdapter(getContext(), genreList);
        popularCategoriesRecyclerView.setAdapter(genresAdapter);

        // Setup Book Listings
        noListingsText = view.findViewById(R.id.noListingsText);

        bookListingRecyclerView = view.findViewById(R.id.bookListingRecyclerView);
        bookListingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bookListingAdapter = new BookListingAdapter(getContext(), bookListings);
        bookListingRecyclerView.setAdapter(bookListingAdapter);

        fetchTrendingBooks();
        fetchGenres();
        fetchBookListings();
        fetchTextBooks();

        return view;
    }

    private void fetchTrendingBooks() {
        if (!isAdded()) return;
        trendingLoading.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://openlibrary.org/trending/daily.json?offset=10")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HomeFragment", "Trending fetch failed", e);
                if (isAdded()) {
                    new Handler(Looper.getMainLooper()).post(() -> trendingLoading.setVisibility(View.GONE));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (isAdded()) {
                    new Handler(Looper.getMainLooper()).post(() -> trendingLoading.setVisibility(View.GONE));
                }

                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject root = new JSONObject(json);
                        JSONArray works = root.getJSONArray("works");

                        List<Book> trendingList = new ArrayList<>();

                        for (int i = 0; i < Math.min(works.length(), 10); i++) {
                            JSONObject item = works.getJSONObject(i);
                            String title = item.optString("title", "Untitled");
                            String workKey = item.optString("key");

                            // Author
                            String author = "Unknown";
                            JSONArray authorNames = item.optJSONArray("author_name");
                            if (authorNames != null && authorNames.length() > 0) {
                                author = authorNames.optString(0);
                            }

                            // Cover image
                            String imageUrl = item.has("cover_i")
                                    ? "https://covers.openlibrary.org/b/id/" + item.getInt("cover_i") + "-M.jpg"
                                    : null;

                            Log.d("imageofBook", "image URL: " + imageUrl);

                            trendingList.add(new Book(title, author, "Trending", imageUrl, workKey));
                        }

                        if (isAdded()) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                featuredBooksList.clear();
                                featuredBooksList.addAll(trendingList);
                                featuredBooksAdapter.setBooks(featuredBooksList);
                            });
                        }

                    } catch (JSONException e) {
                        Log.e("HomeFragment", "JSON parse error", e);
                    }
                }
            }
        });
    }

    private void fetchTextBooks() {
        if (!isAdded()) return;
        textbookLoading.setVisibility(View.VISIBLE);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://openlibrary.org/subjects/textbooks.json?offset=10")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HomeFragment", "Textbook fetch failed", e);
                if (isAdded()) {
                    new Handler(Looper.getMainLooper()).post(() -> textbookLoading.setVisibility(View.GONE));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (isAdded()) {
                    new Handler(Looper.getMainLooper()).post(() -> textbookLoading.setVisibility(View.GONE));
                }

                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject root = new JSONObject(json);
                        JSONArray works = root.getJSONArray("works");

                        List<Book> textBookList = new ArrayList<>();

                        for (int i = 0; i < Math.min(works.length(), 10); i++) {
                            JSONObject item = works.getJSONObject(i);
                            String title = item.optString("title", "Untitled");
                            String workKey = item.optString("key");

                            // Author
                            String author = "Unknown";
                            JSONArray authorArray = item.optJSONArray("authors");

                            if (authorArray != null && authorArray.length() > 0) {
                                JSONObject authorObj = authorArray.optJSONObject(0);
                                if (authorObj != null) {
                                    String name = authorObj.optString("name");
                                    if (!name.isEmpty()) {
                                        author = name;
                                    }
                                }
                            }


                            // Cover image
                            String imageUrl = item.has("cover_id")
                                    ? "https://covers.openlibrary.org/b/id/" + item.getInt("cover_id") + "-M.jpg"
                                    : null;

                            Log.d("imageofTextBook", "image URL: " + imageUrl);

                            textBookList.add(new Book(title, author, "Trending", imageUrl, workKey));
                        }

                        if (isAdded()) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                textBooksList.clear();
                                textBooksList.addAll(textBookList);
                                textBooksAdapter.setBooks(textBookList);
                            });
                        }

                    } catch (JSONException e) {
                        Log.e("HomeFragment", "JSON parse error", e);
                    }
                }
            }
        });
    }



    private void fetchGenres() {
        db.collection("genres")

                .get()

                .addOnSuccessListener(snapshot -> {
                    genreList.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Genre genre = doc.toObject(Genre.class);
                        genreList.add(genre);
                    }
                    genresAdapter.setGenres(genreList);
                });
    }

    private void fetchBookListings() {
        db.collection("bookListing")
                .whereEqualTo("availability", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    bookListings.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        BookListing book = doc.toObject(BookListing.class);
                        bookListings.add(book);
                    }
                    bookListingAdapter.setBookListings(bookListings);

                    if (bookListings.isEmpty()) {
                        noListingsText.setVisibility(View.VISIBLE);
                    } else {
                        noListingsText.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> Log.e("HomeFragment", "Failed to load listings", e));
    }

}
