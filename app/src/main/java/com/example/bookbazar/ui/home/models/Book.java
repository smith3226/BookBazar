package com.example.bookbazar.ui.home.models;

public class Book {
    private String title;
    private String author;
    private String genre;
    private String description;
    private String imageUrl;

    private String workKey;

    public Book() {
    }

    public String getWorkKey() { return workKey; }
    public void setWorkKey(String workKey) { this.workKey = workKey; }

    public Book(String title, String author, String genre,  String imageUrl, String workKey) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.imageUrl = imageUrl;
        this.workKey = workKey;
    }


    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public String getGenre() { return genre; }
    public String getImageUrl() { return imageUrl; }
}
