package com.example.bookbazar.ui.listing.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import coil.ImageLoader;
import coil.request.ImageRequest;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.BookListingFragment;
import com.example.bookbazar.ui.listing.ListingDetailsActivity;
import com.example.bookbazar.ui.listing.models.BookListing;

import java.util.ArrayList;
import java.util.List;

public class BookListingAdapter extends RecyclerView.Adapter<BookListingAdapter.ViewHolder> {

    private final Context context;
    private List<BookListing> bookListings;

    public BookListingAdapter(Context context, List<BookListing> bookListings) {
        this.context = context;
        this.bookListings = bookListings;
    }

    public void setBookListings(List<BookListing> newList) {
        for (int i = 0; i < newList.size(); i++) {
            BookListing b = newList.get(i);
            if (b.getId() == null) {
                Log.e("BookListingAdapter", "Missing ID at position " + i + ": " + b.getTitle());
            } else {
                Log.d("BookListingAdapter", "Received ID: " + b.getId() + " for " + b.getTitle());
            }
        }
        this.bookListings = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookListingAdapter.ViewHolder holder, int position) {
        BookListing book = bookListings.get(position);
        Log.d("AdapterDebug", "Position: " + position +
                " | Title: " + book.getTitle() +
                " | ID: " + book.getId());
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.price.setText("$" + book.getPrice());
        holder.condition.setText(book.getCondition());

        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Log.d("Image URL:", book.getImageUrl());
            ImageLoader loader = new ImageLoader.Builder(context).build();
            ImageRequest request = new ImageRequest.Builder(context)
                    .data(book.getImageUrl())
                    .target(holder.imageView)
                    .crossfade(true)
                    .build();
            loader.enqueue(request);
        } else {
            holder.imageView.setImageResource(R.drawable.alchemist);
        }

        holder.itemView.setOnClickListener(v -> {
            String bookID = book.getId();
            if (bookID != null) {
                Log.d("listingID", "Book ID passed to intent: " + bookID);
                Intent intent = new Intent(context, ListingDetailsActivity.class);
                intent.putExtra("documentId", bookID);
                context.startActivity(intent);
            } else {
                Log.e("listingID", "book.getId() is null! Check fetchListings()");
                Toast.makeText(context, "Book ID missing", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookListings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price, condition;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listingTitle);
            author = itemView.findViewById(R.id.listingAuthor);
            price = itemView.findViewById(R.id.listingPrice);
            condition = itemView.findViewById(R.id.listingCondition);
            imageView = itemView.findViewById(R.id.listingImage);
        }
    }
}
