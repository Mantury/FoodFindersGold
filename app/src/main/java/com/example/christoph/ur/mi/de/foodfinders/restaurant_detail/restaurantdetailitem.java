package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.app.Activity;

import com.example.christoph.ur.mi.de.foodfinders.starting_screen.restaurantitemstart;

import java.util.ArrayList;

/**
 * Created by juli on 12.09.15.
 */
public class restaurantdetailitem  {

    private String image;
    private String name;
    private String address;
    private String number;
    private String rating;
    private String  place_id;
    private boolean openednow;
    private String openweekday;
    //Arraylist für kommentare??
    private ArrayList<String> comments;


    public restaurantdetailitem(String name, String image, String address, String number, String rating, String place_id, boolean openednow, String openweekday, ArrayList comments) {
        this.name=name;
        this.image=image;
        this.address=address;
        this.number=number;
        this.rating=rating;
        this.place_id=place_id;
        this.openednow=openednow;
        this.openweekday=openweekday;
        this.comments=comments;
    }

    public  String getName() {
        return name;
    }

    public String getImage(){
        return image;
    }

    public String getAddress(){
        return address;
    }
    public String getNumber(){
        return number;
    }
    public String getRating(){
        return rating;
    }
    public String getPlace_id(){
        return place_id;
    }
    public boolean getOpennow(){
        return openednow;
    }
    public String getOpenweekday(){
        return openweekday;
    }
    public ArrayList getComments(){
        return comments;
    }
}
