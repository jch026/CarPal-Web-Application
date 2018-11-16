package com.app.server.models;

public class RenterPaymentMethod {

    public String getId() {
        return id;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public String getCreditCardNo() {
        return creditCardNo;
    }

    public String getRenterId(){return renterId;}

    String id=null;
    String renterId;
    String paymentMode;
    String billingAddress;
    String creditCardNo;

    public RenterPaymentMethod(String paymentMode, String billingAddress, String creditCardNo) {
        this.paymentMode = paymentMode;
        this.billingAddress = billingAddress;
        this.creditCardNo = creditCardNo;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }
}
