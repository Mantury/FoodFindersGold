package com.example.christoph.ur.mi.de.foodfinders.starting_screen;


public class restaurantitemstart {

    private String name;
    private double latitude;
    private double longitude;
    private String  place_id;
    private boolean openednow;
    private String address;


    public restaurantitemstart (String name, double latitude, double longitude, String place_id, boolean openednow, String address){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_id = place_id;
        this.openednow = openednow;
        this.address = address;
    }



    public boolean isOpenednow() {
        return openednow;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {

        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}




