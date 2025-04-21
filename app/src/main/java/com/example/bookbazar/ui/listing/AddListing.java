package com.example.bookbazar.ui.listing;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookbazar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



import android.content.Intent;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.firestore.FieldValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddListing extends AppCompatActivity {

    private ImageView imgUpload1, imgUpload2, imgUpload3;

    private Spinner spinnerGenre;
    private RadioButton rbInterestedForSwap;
    private List<String> genreList = new ArrayList<>();

    private Uri[] imageUris = new Uri[3];
    private int selectedImageIndex = 0;

    private EditText etBookName, etAuthorName, etBookGenre, etBookPrice, etBookDescription;
    private RadioGroup rgBookCondition;
    private Button btnSubmit;

    private FirebaseFirestore firestore;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        imageUris[selectedImageIndex] = uri;
                        showThumbnail(uri, selectedImageIndex);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        imgUpload1 = findViewById(R.id.imgUpload1);
        imgUpload2 = findViewById(R.id.imgUpload2);
        imgUpload3 = findViewById(R.id.imgUpload3);

        etBookName = findViewById(R.id.etBookName);
        etAuthorName = findViewById(R.id.etAuthorName);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        rbInterestedForSwap = findViewById(R.id.rbInterestedForSwap);
        loadGenresFromFirestore();
        etBookPrice = findViewById(R.id.etBookPrice);
        etBookDescription = findViewById(R.id.etBookDescription);
        rgBookCondition = findViewById(R.id.rgBookCondition);
        btnSubmit = findViewById(R.id.btnSubmitListing);



        firestore = FirebaseFirestore.getInstance();

        imgUpload1.setOnClickListener(v -> openGallery(0));
        imgUpload2.setOnClickListener(v -> openGallery(1));
        imgUpload3.setOnClickListener(v -> openGallery(2));

        btnSubmit.setOnClickListener(v -> uploadImagesToCloudinary());
    }

    private void openGallery(int imageIndex) {
        selectedImageIndex = imageIndex;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showThumbnail(Uri uri, int slot) {
        ImageView target = (slot == 0) ? imgUpload1 : (slot == 1) ? imgUpload2 : imgUpload3;
        target.setImageURI(uri);
    }

    private void uploadImagesToCloudinary() {
        List<String> imageUrls = new ArrayList<>();
        List<Uri> selectedUris = new ArrayList<>();
        for (Uri uri : imageUris) {
            if (uri != null) selectedUris.add(uri);
        }

        if (selectedUris.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < imageUris.length; i++) {
            Uri uri = imageUris[i];
            if (uri != null) {
                final int index = i;
                uploadSingleImage(uri, imageUrls, getSelectedCount(), index);
            }
        }

    }

    private int getSelectedCount() {
        int count = 0;
        for (Uri uri : imageUris) {
            if (uri != null) count++;
        }
        return count;
    }


    private void uploadSingleImage(Uri imageUri, List<String> imageUrls, int totalCount, int originalIndex){

        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Compress the image to JPEG with 75% quality
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] imageBytes = baos.toByteArray();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", "image.jpg",
                            RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                    .addFormDataPart("upload_preset", "unsigned_preset")
                    .build();

            Request request = new Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dyr1bvnke/image/upload?folder=bookImages")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(AddListing.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        try {
                            JSONObject obj = new JSONObject(json);
                            String imageUrl = obj.getString("secure_url");
                            imageUrls.add(imageUrl);
                            if (imageUrls.size() == totalCount) {
                                runOnUiThread(() -> saveToFirestore(imageUrls));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(AddListing.this, "Upload failed.", Toast.LENGTH_SHORT).show());
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGenresFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("genres")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    genreList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        if (name != null) genreList.add(name);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenre.setAdapter(adapter);
                });
    }


    private void saveToFirestore(List<String> imageUrls) {
        String title = etBookName.getText().toString();
        String genre = spinnerGenre.getSelectedItem().toString();
        String author = etAuthorName.getText().toString();
        String description = etBookDescription.getText().toString();
        String price = etBookPrice.getText().toString();

        int selectedConditionId = rgBookCondition.getCheckedRadioButtonId();
        String condition = selectedConditionId != -1 ? ((RadioButton) findViewById(selectedConditionId)).getText().toString() : "Unknown";

        Map<String, Object> bookData = new HashMap<>();
        bookData.put("id", UUID.randomUUID().toString());
        bookData.put("title", title);
        bookData.put("author", author);
        bookData.put("genre", genre);
        bookData.put("description", description);
        bookData.put("price", Double.parseDouble(price));
        bookData.put("condition", condition);
        bookData.put("imageUrls", imageUrls);
        bookData.put("imageUrl", imageUrls.get(0));
        bookData.put("userId", currentUser.getUid());
        bookData.put("userEmail", currentUser.getEmail());
        bookData.put("userName", currentUser.getDisplayName());
        bookData.put("userPhoto", currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null);
        boolean isInterestedForSwap = rbInterestedForSwap.isChecked();
        bookData.put("interestedForSwap", isInterestedForSwap);
        bookData.put("availability", true); // default true
        bookData.put("timestamp", FieldValue.serverTimestamp());


        firestore.collection("bookListing")
                .add(bookData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Listing submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to submit listing: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
