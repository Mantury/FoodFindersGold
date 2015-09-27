package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;


import com.example.christoph.ur.mi.de.foodfinders.starting_screen.restaurantitemstart;

import java.util.ArrayList;

//This class saves all the information for the detail view of a restaurant.
//Since all the data is redownloaded the class was made independent from "restaurantitemstart".

public class restaurantdetailitem extends restaurantitemstart {

    private String image;
    private String name;
    private String address;
    private String number;
    private String rating;
    private String place_id;
    private int openednow;
    private String openweekday;
    private ArrayList<String> comments;

    public restaurantdetailitem(String name, double latitude,double longitude,String place_id,int openednow,String address, String image, String number, String rating, String openweekday, ArrayList comments) {
        super(name,latitude,longitude, place_id, openednow, address);
        this.name = name;
        this.image = image;
        this.address = address;
        this.number = number;
        this.rating = rating;
        this.place_id = place_id;
        this.openednow = openednow;
        this.openweekday = openweekday;
        this.comments = comments;
    }

    public String getImage() {
        return image;
    }

    public String getNumber() {
        return number;
    }

    public String getRating() {
        return rating;
    }

    public String getOpenweekday() {
        return openweekday;
    }

    public ArrayList getComments() {
        return comments;
    }
}
