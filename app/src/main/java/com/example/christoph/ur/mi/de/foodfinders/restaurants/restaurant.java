package com.example.christoph.ur.mi.de.foodfinders.restaurants;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;

import java.util.ArrayList;


public class restaurant {

    private String name;
    private double latitude;
    private double longitude;
    private String place_id;
    private int open;
    private String address;
    private ArrayList<String> images;
    private String number;
    private String rating;
    private String openweekday;

    public restaurant (String name, double latitude, double longitude, String place_id, int open, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_id = place_id;
        this.open = open;
        this.address = address;
    }

    public void setDetails(ArrayList<String> images, String number, String rating, String openweekday) {
        this.images = images;
        this.number = number;
        this.rating = rating;
        this.openweekday = openweekday;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public int getOpen() {
        return open;
    }

    public double getLongitude() {

        return longitude;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getOpenweekday() {
        return openweekday;
    }

    public String getNumber() {
        return number;
    }


    public String getAddress() {
        return address;
    }
}
