package com.example.bookbazar.ui.home.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.models.Book;

import java.util.ArrayList;
import java.util.List;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class PopularCategoriesAdapter extends RecyclerView.Adapter<PopularCategoriesAdapter.BookViewHolder> {

    private List<Book> bookList;
    private final Context context;

    public PopularCategoriesAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList != null ? bookList : new ArrayList<>();
    }

    @NonNull
    @Override
    public PopularCategoriesAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_card, parent, false);
        return new PopularCategoriesAdapter.BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularCategoriesAdapter.BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bookTitle.setText(book.getTitle());

        // Load category image using Coil
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            ImageLoader imageLoader = new ImageLoader.Builder(context).build();
            ImageRequest request = new ImageRequest.Builder(context)
                    .data(book.getImageUrl())
                    .crossfade(true)
                    .placeholder(R.drawable.alchemist) // Placeholder image
                    .error(R.drawable.alchemist) // Error image
                    .target(holder.bookImage)
                    .build();
            imageLoader.enqueue(request);
        } else {
            holder.bookImage.setImageResource(R.drawable.alchemist);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // Method to update the list dynamically
    public void setBooks(List<Book> newBooks) {
        this.bookList = newBooks;
        notifyDataSetChanged();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
        }
    }
}
