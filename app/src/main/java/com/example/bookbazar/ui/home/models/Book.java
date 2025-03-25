package com.example.bookbazar.ui.home.models;

public class Book {
    private String title;
    private String author;
    private String genre;
    private double price;
    private String condition;
    private String imageUrl;

    public Book() {
        // Empty constructor required for Firestore
    }

    public Book(String title, String author, String genre, double price, String condition, String imageUrl) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.condition = condition;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public double getPrice() { return price; }
    public String getCondition() { return condition; }
    public String getImageUrl() { return imageUrl; }
}
