package com.example.christoph.ur.mi.de.foodfinders.restaurants;

import java.util.ArrayList;


public class restaurant {

    private String name;
    private double latitude;
    private double longitude;
    private String place_id;
    private int open;
    private String address;
    private String image;
    private String number;
    private String rating;
    private String openweekday;
    private ArrayList<String> comments;

    public restaurant (String name, double latitude, double longitude, String place_id, int open, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_id = place_id;
        this.open = open;
        this.address = address;
    }

    public void setDetails(String image, String number, String rating, String openweekday, ArrayList<String> comments) {
        this.image = image;
        this.number = number;
        this.rating = rating;
        this.openweekday = openweekday;
        this.comments = comments;
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

    public String getImage() {
        return image;
    }

    public String getOpenweekday() {
        return openweekday;
    }

    public String getNumber() {
        return number;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public String getAddress() {
        return address;
    }
}
