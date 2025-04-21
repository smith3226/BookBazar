package com.example.bookbazar.ui.listing;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.listing.adapters.ImageCarouselAdapter;
import com.example.bookbazar.ui.listing.models.BookListing;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import coil.ImageLoader;
import coil.request.ImageRequest;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Callback;


public class ListingDetailsActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;

    private String clientSecret;
    private String documentId;
    private double bookPrice;
    private String bookTitle, imageUrl;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_details);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        PaymentConfiguration.init(getApplicationContext(), "pk_test_51REYj6Qo5ByqMaaOnzZxdmOq5mhP6PGXdol1UnQhrP0ZRYQw2kLfkMqUbLRbLEmyTK6ysoAiJnrU6BgjG1gbDRQY00cSH0734P");
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);

        documentId = getIntent().getStringExtra("documentId");
        ViewPager2 carousel = findViewById(R.id.imageCarousel);
        TextView title = findViewById(R.id.bookTitle);
        TextView price = findViewById(R.id.bookPrice);
        TextView availability = findViewById(R.id.availability);
        //TextView summary = findViewById(R.id.bookSummary);
        TextView description = findViewById(R.id.bookDescription);
        TextView postedBy = findViewById(R.id.bookPostedBy);
        Button addToCart = findViewById(R.id.btnAddToCart);
        Button swap = findViewById(R.id.btnSwap);
        ImageView btnSave = findViewById(R.id.btnSaveListing);
        ImageView profilePic = findViewById(R.id.userProfileImage);

        Button addToSwapList = findViewById(R.id.btnAddToSwapList);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;



        if (documentId != null) {
            FirebaseFirestore.getInstance()
                    .collection("bookListing")
                    .whereEqualTo("id", documentId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                BookListing book = doc.toObject(BookListing.class);
                                if (book != null) {
                                    String listingUserId = book.getUserId();

                                    // Determine button visibility
                                    if (currentUserId != null && currentUserId.equals(listingUserId)) {
                                        // User is owner
                                        addToCart.setVisibility(View.GONE);
                                        swap.setVisibility(View.GONE);
                                        addToSwapList.setVisibility(View.VISIBLE);
                                    } else {
                                        // User is not owner
                                        addToCart.setVisibility(View.VISIBLE);
                                        swap.setVisibility(View.VISIBLE);
                                        addToSwapList.setVisibility(View.GONE);
                                    }
                                }
                                Boolean saved = doc.getBoolean("saved");
                                if (saved != null && saved) {
                                    btnSave.setImageResource(R.drawable.ic_heart_filled);
                                    btnSave.setTag(true);
                                } else {
                                    btnSave.setImageResource(R.drawable.ic_heart_outline);
                                    btnSave.setTag(false);
                                }
                            }
                        }
                    });
        }

        btnSave.setOnClickListener(v -> {
            boolean isSaved = v.getTag() != null && (boolean) v.getTag();

            if (documentId == null) {
                Toast.makeText(this, "No document ID provided", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSave.setImageResource(isSaved ? R.drawable.ic_heart_outline : R.drawable.ic_heart_filled);
            v.setTag(!isSaved);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("bookListing")
                    .whereEqualTo("id", documentId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                doc.getReference()
                                        .update("saved", !isSaved)
                                        .addOnSuccessListener(unused ->
                                                Toast.makeText(this, isSaved ? "Removed from saved" : "Saved!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Error updating save", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(this, "Listing not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Query failed", Toast.LENGTH_SHORT).show());
        });





        Log.d("ListingDetails", "Received documentId: " + documentId);


        if (documentId != null) {
            FirebaseFirestore.getInstance()
                    .collection("bookListing")
                    .whereEqualTo("id", documentId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                BookListing book = doc.toObject(BookListing.class);
                                Log.d("ListingBook", "Received listing: " + book.getAvailability());

                                if (book != null) {
                                    this.bookPrice = book.getPrice();
                                    price.setText(String.format("$%.2f", bookPrice));
                                    this.bookTitle=book.getTitle();
                                    title.setText(book.getTitle());
                                    this.imageUrl = book.getImageUrl();

                                    availability.setText(book.getAvailability() ? "Available" : "Sold");

                                    if (!book.getAvailability()) {
                                        availability.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                        addToCart.setEnabled(false);
                                        swap.setEnabled(false);
                                        addToCart.setAlpha(0.5f);
                                        swap.setAlpha(0.5f);

                                    } else {
                                        availability.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                    }

                                    //originalPrice.setText("Original price: $10.00");
                                    //summary.setText(book.getDescription());
                                    description.setText(book.getDescription());

                                    if (book.getImageUrls() != null && !book.getImageUrls().isEmpty()) {
                                        ImageCarouselAdapter adapter = new ImageCarouselAdapter(this, book.getImageUrls());
                                        carousel.setAdapter(adapter);
                                    }

                                    String userInfo = book.getUserName() != null ? book.getUserName() : book.getUserEmail();
                                    if (book.getTimestamp() != null) {
                                        String relativeTime = DateUtils.getRelativeTimeSpanString(
                                                book.getTimestamp().toDate().getTime(),
                                                System.currentTimeMillis(),
                                                DateUtils.MINUTE_IN_MILLIS
                                        ).toString();
                                        postedBy.setText("Posted by: " + userInfo + " • " + relativeTime);
                                    } else {
                                        postedBy.setText("Posted by: " + userInfo);
                                    }

                                    if (book.getUserPhoto() != null && !book.getUserPhoto().isEmpty()) {
                                        ImageLoader loader = new ImageLoader.Builder(this).build();
                                        ImageRequest request = new ImageRequest.Builder(this)
                                                .data(book.getUserPhoto())
                                                .target(profilePic)
                                                .crossfade(true)
                                                .placeholder(R.drawable.ic_profile)
                                                .error(R.drawable.ic_profile)
                                                .build();
                                        loader.enqueue(request);
                                    } else {
                                        profilePic.setImageResource(R.drawable.ic_profile);
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(this, "No listing found for this ID", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to load listing", Toast.LENGTH_SHORT).show()
                    );
        }


        addToCart.setOnClickListener(v -> {
            if (documentId != null) {
                FirebaseFirestore.getInstance()
                        .collection("bookListing")
                        .whereEqualTo("id", documentId)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                BookListing book = doc.toObject(BookListing.class);

                                if (book != null) {
                                    this.bookPrice = book.getPrice();
                                    this.bookTitle = book.getTitle();
                                    this.imageUrl = book.getImageUrl();
                                    createPaymentIntent();
                                }
                            }
                        });
            }
        });

        swap.setOnClickListener(v -> Toast.makeText(this, "Swap clicked!", Toast.LENGTH_SHORT).show());
    }

    private void createPaymentIntent() {
        OkHttpClient client = new OkHttpClient();
        int amountInCents = (int) (bookPrice * 100);
        Log.d("Price of book", "Price: " + bookPrice);

        JSONObject json = new JSONObject();
        try {
            json.put("listingId", documentId);
            json.put("title", bookTitle);
            json.put("imageUrl", imageUrl);
            json.put("amount", amountInCents);
            json.put("userId", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url("https://stripe-backend-zmp1.onrender.com/create-payment-intent")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(ListingDetailsActivity.this, "Failed to start payment", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("StripeResponse", "RAW: " + responseData);

                try {
                    JSONObject res = new JSONObject(responseData);
                    clientSecret = res.getString("clientSecret");

                    runOnUiThread(() -> presentPaymentSheet(clientSecret));
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(ListingDetailsActivity.this, "Error parsing payment intent", Toast.LENGTH_LONG).show()
                    );
                }
            }


        });
    }

    private void presentPaymentSheet(String clientSecret) {
        paymentSheet.presentWithPaymentIntent(
                this.clientSecret,
                new PaymentSheet.Configuration("BookBazar")
        );
    }

    private void onPaymentResult(PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
            saveOrderToFirestore("completed");
            finish(); // or refresh
        } else if (result instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrderToFirestore(String status) {
        Map<String, Object> order = new HashMap<>();
        order.put("bookTitle", bookTitle);
        order.put("price", bookPrice);
        order.put("imageUrl", imageUrl);
        order.put("paymentStatus", status);
        order.put("timestamp", Timestamp.now());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            order.put("userId", user.getUid());
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("orders")
                .add(order)
                .addOnSuccessListener(ref -> {
                    Log.d("StripeOrder", "Order stored!");

                    if (documentId != null) {
                        db.collection("bookListing")
                                .whereEqualTo("id", documentId)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (!snapshot.isEmpty()) {
                                        DocumentSnapshot doc = snapshot.getDocuments().get(0);
                                        doc.getReference().update("availability", false)
                                                .addOnSuccessListener(u -> Log.d("Listing", "Availability set to false"))
                                                .addOnFailureListener(e -> Log.e("Listing", "Failed to update availability", e));
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("StripeOrder", "Order failed: ", e));
    }


}

