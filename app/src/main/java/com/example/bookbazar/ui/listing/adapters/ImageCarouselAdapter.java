package com.example.bookbazar.ui.listing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;

import java.util.List;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder> {

    private final Context context;
    private final ImageLoader loader;
    private final List<String> imageUrls;

    public ImageCarouselAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.loader = new ImageLoader.Builder(context).build();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carousel_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        String imageUrl = imageUrls.get(position);
        System.out.println(">>> Carousel loading: " + imageUrl);

        android.util.Log.d("CarouselImage", "Loading: " + imageUrl);
        ImageRequest request = new ImageRequest.Builder(context)
                .data(imageUrl)
                .target(holder.imageView)
                .placeholder(R.drawable.app_icon)
                .error(R.drawable.app_icon)
                .crossfade(true)
                .build();
        loader.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carouselImage);
        }
    }
}
