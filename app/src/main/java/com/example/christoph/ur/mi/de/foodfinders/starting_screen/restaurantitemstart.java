package com.example.christoph.ur.mi.de.foodfinders.starting_screen;


public class restaurantitemstart {
    private String name;
    private long latitude;
    private long longitude;
    private int phonenumber;
    private boolean openednow;
    private String address;


    public restaurantitemstart (String name, long latitude, long longitude, int phonenumber, boolean openednow, String address){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phonenumber = phonenumber;
        this.openednow = openednow;
        this.address = address;
    }

    public long getLongitude() {
        return longitude;
    }

    public boolean isOpenednow() {
        return openednow;
    }

    public int getPhonenumber() {
        return phonenumber;
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




