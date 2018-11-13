package com.app.server.models;

public class Booking {

    public Booking() {
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() { return ownerId; }

    public String getRenterId() {
        return renterId;
    }

    public String getCarId() {
        return carId;
    }

    public String getCostOfCar() { return costOfCar; }

    public String getDate() { return date; }

    public String getStartTime() { return startTime; }

    public String getEndTime() { return endTime; }

    public String getPickupAddress() { return pickupAddress; }

    public String getStatus() { return status; }

    String id=null;
    String ownerId;
    String renterId;
    String carId;
    String costOfCar;
    String date;
    String startTime;
    String endTime;
    String pickupAddress;
    String status;



    public Booking( String carId, String costOfCar, String date, String startTime, String endTime, String pickupAddress, String status) {
        this.carId = carId;
        this.costOfCar = costOfCar;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pickupAddress = pickupAddress;
        this.status = status;

    }
    public void setId(String id) {
        this.id = id;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }
    public void setCarId(String carId) {
        this.carId = carId;
    }
}