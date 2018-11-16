package com.app.server.models;

public class Location {

    public String getId() {
        return id;
    }

    public String getLocationName() {
        return locationName;
    }

    String id=null;
    String locationName;


    public Location( String locationName) {
        this.locationName = locationName;

    }
    public void setId(String id) {
        this.id = id;
    }

}