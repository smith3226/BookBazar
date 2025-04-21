package com.example.bookbazar.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.profile.OrdersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private TextView emptyText;
    private final List<Map<String, Object>> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.ordersRecyclerView);
        emptyText = findViewById(R.id.noOrdersText);

        adapter = new OrdersAdapter(this, orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchOrders();
    }

    private void fetchOrders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    orders.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        orders.add(doc.getData());
                    }
                    adapter.notifyDataSetChanged();
                    emptyText.setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> Log.e("MyOrders", "Failed to load orders", e));
    }
}
