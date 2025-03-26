package com.example.bookbazar.ui.listing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookbazar.R;
import com.example.bookbazar.ui.home.models.Book;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddListing extends AppCompatActivity {

    private EditText etBookName, etAuthorName, etBookPrice, etBookDescription , etBookGenre;
    private RadioGroup rgBookCondition;
    private Button btnSubmitListing;
    private ImageView imgUpload1, imgUpload2, imgUpload3;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri1, imageUri2, imageUri3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);

        // Initialize Views
        etBookName = findViewById(R.id.etBookName);
        etAuthorName = findViewById(R.id.etAuthorName);
        etBookPrice = findViewById(R.id.etBookPrice);
        etBookGenre = findViewById(R.id.etBookGenre);
        etBookDescription = findViewById(R.id.etBookDescription);
        rgBookCondition = findViewById(R.id.rgBookCondition);
        btnSubmitListing = findViewById(R.id.btnSubmitListing);
        imgUpload1 = findViewById(R.id.imgUpload1);
        imgUpload2 = findViewById(R.id.imgUpload2);
        imgUpload3 = findViewById(R.id.imgUpload3);

        // Submit Button Click
        btnSubmitListing.setOnClickListener(view -> {
            String bookName = etBookName.getText().toString().trim();
            String authorName = etAuthorName.getText().toString().trim();
            String bookPrice = etBookPrice.getText().toString().trim();
            String bookDescription = etBookDescription.getText().toString().trim();
            String bookGenre = etBookGenre.getText().toString().trim();

            // Get Selected Book Condition
            int selectedConditionId = rgBookCondition.getCheckedRadioButtonId();
            String bookCondition = "";

            if (selectedConditionId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedConditionId);
                bookCondition = selectedRadioButton.getText().toString();
            } else {
                Toast.makeText(this, "Please select book condition", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate Inputs
            if (bookName.isEmpty() || authorName.isEmpty() || bookPrice.isEmpty() || bookDescription.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate Image Selection
//            if (imageUri1 == null && imageUri2 == null && imageUri3 == null) {
//                Toast.makeText(this, "Please upload at least one image", Toast.LENGTH_SHORT).show();
//                return;
//            }

            double bookPriceStr = 0;
            try {
                bookPriceStr = Double.parseDouble(bookPrice);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Book Object
            Book book = new Book(bookName, authorName, bookGenre, bookPriceStr, bookDescription, bookCondition);

            //send data to database
            saveBookToDB(book);
            //send data to database
            Log.d("AddListing", "Book Name: " + bookName  + " Author Name: " + authorName + " Book Price: " + bookPrice + " Book Description: " + bookDescription + " Book Condition: " + bookCondition);

            // Handle saving to Firebase or Database (Next Step)
            Toast.makeText(this, "Book Listing Submitted", Toast.LENGTH_SHORT).show();
        });

        // Set OnClickListener for image upload (using tags to differentiate between slots)
//        imgUpload1.setOnClickListener(view -> openImagePicker(view));
//        imgUpload2.setOnClickListener(view -> openImagePicker(view));
//        imgUpload3.setOnClickListener(view -> openImagePicker(view));
    }

    private void saveBookToDB(Book book) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //create reference
        CollectionReference booksRef = db.collection("books");

        booksRef.add(book)
                 .addOnSuccessListener(documentReference -> {
            // If the data is successfully saved, show a toast message
            Toast.makeText(AddListing.this, "Book added successfully", Toast.LENGTH_SHORT).show();
        })
                .addOnFailureListener(e -> {
                    // If an error occurs, show a toast message with the error
                    Toast.makeText(AddListing.this, "Error adding book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    // Handle the result when image is selected from gallery or taken from camera
    //@Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            if (imageUri != null) {
//                switch (requestCode) {
//                    case 1:
//                        imgUpload1.setImageURI(imageUri);
//                        imageUri1 = imageUri;
//                        break;
//                    case 2:
//                        imgUpload2.setImageURI(imageUri);
//                        imageUri2 = imageUri;
//                        break;
//                    case 3:
//                        imgUpload3.setImageURI(imageUri);
//                        imageUri3 = imageUri;
//                        break;
//                }
//            }
//        }
//    }

//    private void openImagePicker(View view) {
//        // Get the tag of the clicked ImageView to identify which slot is clicked
//        int imageSlot = Integer.parseInt(view.getTag().toString());
//
//        // Open image picker (gallery)
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, imageSlot);
//    }
}
