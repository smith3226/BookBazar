package com.example.bookbazar.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.BookDetailActivity;
import com.example.bookbazar.ui.home.models.Book;

import java.util.ArrayList;
import java.util.List;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class FeaturedBooksAdapter extends RecyclerView.Adapter<FeaturedBooksAdapter.BookViewHolder> {

    private List<Book> bookList;
    private final Context context;
    private final OnBookClickListener bookClickListener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public FeaturedBooksAdapter(Context context, List<Book> bookList, OnBookClickListener listener) {
        this.context = context;
        this.bookList = bookList != null ? bookList : new ArrayList<>();
        this.bookClickListener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_card, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());
        holder.bookAuthor.setText(book.getAuthor());

        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            ImageLoader imageLoader = new ImageLoader.Builder(context).build();
            ImageRequest request = new ImageRequest.Builder(context)
                    .data(book.getImageUrl())
                    .crossfade(true)
                    .target(holder.bookImage)
                    .build();
            imageLoader.enqueue(request);
        } else {
            holder.bookImage.setImageResource(R.drawable.alchemist);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookDetailActivity.class);
            intent.putExtra("title", book.getTitle());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("imageUrl", book.getImageUrl());
            intent.putExtra("description", book.getGenre()); // fallback
            intent.putExtra("workKey", book.getWorkKey());
            context.startActivity(intent);
        });


    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // Method to update books dynamically
    public void setBooks(List<Book> newBooks) {
        this.bookList = newBooks;
        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle, bookAuthor;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
        }
    }
}
