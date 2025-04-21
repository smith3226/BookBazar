package com.example.bookbazar.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bookbazar.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Map<String, Object>> orders;

    public OrdersAdapter(Context context, List<Map<String, Object>> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Map<String, Object> order = orders.get(position);

        holder.title.setText(String.valueOf(order.get("bookTitle")));
        holder.status.setText("Status: " + order.get("paymentStatus"));
        holder.price.setText("$" + String.valueOf(order.get("price")));

        Object timestamp = order.get("timestamp");
        if (timestamp instanceof com.google.firebase.Timestamp) {
            Date date = ((com.google.firebase.Timestamp) timestamp).toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.date.setText(sdf.format(date));
        }

        String imageUrl = (String) order.get("imageUrl");
        if (imageUrl != null) {
            ImageLoader loader = new ImageLoader.Builder(context).build();
            ImageRequest request = new ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .target(holder.imageView)
                    .build();
            loader.enqueue(request);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, price, date, status;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.orderImage);
            title = itemView.findViewById(R.id.orderTitle);
            price = itemView.findViewById(R.id.orderPrice);
            date = itemView.findViewById(R.id.orderDate);
            status = itemView.findViewById(R.id.orderStatus);
        }
    }
}
