package com.app.server.models;

public class Review {
    String id = null;
    String textContent;
    Integer ratings;
    String bookingId;
    String userId;

    public Review(String textContent,Integer ratings, String userId, String bookingId) {
        this.textContent = textContent;
        this.ratings = ratings;
        this.userId = userId;

    }
    public void setId(String id) {
        this.id = id;
    }
    public void setBookingId(String bookingId) {this.bookingId = bookingId;}
}
