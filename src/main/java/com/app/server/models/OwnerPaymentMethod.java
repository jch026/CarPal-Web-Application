package com.app.server.models;

public class OwnerPaymentMethod {

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public String getBookingId() {
        return bookingId;
    }


    String id=null;
    String ownerId;
    String bankAccount;
    String bookingId;

    public OwnerPaymentMethod(String bankAccount, String bookingId) {
        this.bankAccount = bankAccount;
        this.bookingId = bookingId;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = id;
    }
    public void setBookingId(String bookingId) {
        this.bookingId = id;
    }

}