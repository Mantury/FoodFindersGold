package com.example.christoph.ur.mi.de.foodfinders.starting_screen;


public class restaurantitemstart {

    private String name;
    private long latitude;
    private long longitude;
    private String  place_id;
    private boolean openednow;
    private String address;


    public restaurantitemstart (String name, long latitude, long longitude, String place_id, boolean openednow, String address){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_id = place_id;
        this.openednow = openednow;
        this.address = address;
    }

    public long getLongitude() {
        return longitude;
    }

    public boolean isOpenednow() {
        return openednow;
    }

    public String getPhonenumber() {
        return place_id;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {

        return name;
    }

    public long getLatitude() {
        return latitude;
    }
}




