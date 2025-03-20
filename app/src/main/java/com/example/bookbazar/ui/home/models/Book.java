package com.example.bookbazar.ui.home.models;

public class Book {
    private String title;
    private String author;
    private int imageResId; // Drawable resource ID for book cover

    public Book(String title, String author, int imageResId) {
        this.title = title;
        this.author = author;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getImageResId() {
        return imageResId;
    }
}
