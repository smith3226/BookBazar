package com.example.bookbazar.ui.home.models;

public class Book {
    private String title;
    private String author;
    private String genre;
    private double price;
    private String condition;
    private String description;
    private String imageUrl;

    public Book() {
        // Empty constructor required for Firestore
    }

    // Constructor with all fields, including imageUrl
    public Book(String title, String author, String genre, double price, String description, String condition) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.condition = condition;
        this.description = description;
        //this.imageUrl = imageUrl;  // Ensure imageUrl is set in the constructor
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public double getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getImageUrl() { return imageUrl; }
}
