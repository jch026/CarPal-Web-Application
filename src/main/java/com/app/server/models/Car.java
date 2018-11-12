package com.app.server.models;

public class Car {

    public Car() {
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() { return ownerId; }

    public String getCarManufacturer() {
        return carManufacturer;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getCarType() { return carType; }

    public String getCarYear() { return carYear; }

    public String getCarRegistration() { return carRegistration; }

    public String getCostOfCar() { return costOfCar; }

    public String getCarLocation() { return carLocation; }

    String id=null;
    String ownerId;
    String carManufacturer;
    String carModel;
    String carType;
    String carYear;
    String carRegistration;
    String costOfCar;
    String carLocation;



    public Car(String carManufacturer, String carModel, String carType, String carYear, String carRegistration, String costOfCar, String carLocation) {
        this.carManufacturer = carManufacturer;
        this.carModel = carModel;
        this.carType = carType;
        this.carYear = carYear;
        this.carRegistration = carRegistration;
        this.costOfCar = costOfCar;
        this.carLocation = carLocation;

    }
    public void setId(String id) {
        this.id = id;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}