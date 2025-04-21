package com.example.bookbazar.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.GenreListingActivity;
import com.example.bookbazar.ui.home.models.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.GenreViewHolder> {

    private final Context context;
    private List<Genre> genreList;

    public GenresAdapter(Context context, List<Genre> genreList) {
        this.context = context;
        this.genreList = genreList != null ? genreList : new ArrayList<>();
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_card, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);
        holder.genreName.setText(genre.getName());

        // Handle click to navigate to GenreListingActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreListingActivity.class);
            intent.putExtra("genre", genre.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public void setGenres(List<Genre> newGenres) {
        this.genreList = newGenres;
        notifyDataSetChanged();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreName;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.bookTitle);
        }
    }
}
