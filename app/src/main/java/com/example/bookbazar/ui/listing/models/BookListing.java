package com.example.bookbazar.ui.listing.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class BookListing {

    private String id;
    private String title;
    private String author;
    private String genre;
    private double price;
    private String condition;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
    private String userId;
    private String userName;
    private String userEmail;
    private String userPhoto;
    private Timestamp timestamp;

    private Boolean availability;

    public BookListing() {
        // Firestore needs empty constructor
    }

    public BookListing(String id, String title, String author, String genre, double price,
                       String condition, String description, String imageUrl, String userId, boolean availability) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.condition = condition;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.availability=availability;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Boolean getAvailability(){
        return availability;
    }

    public void setAvailability(Boolean availability){
        this.availability=availability;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public double getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public List<String> getImageUrls() { return imageUrls; }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getUserPhoto() { return userPhoto; }
    public Timestamp getTimestamp() { return timestamp; }
}
