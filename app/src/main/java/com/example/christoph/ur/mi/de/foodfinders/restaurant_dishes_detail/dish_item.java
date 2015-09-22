package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.graphics.Bitmap;

/**
 * Created by juli on 22.09.15.
 */
public class dish_item {

    String nameDish;
    String place_id;
    String parse_id;
    int rating;
    String gluten;
    String vegan;
    String comment;
    Bitmap image;

    public dish_item(String nameDish,String place_id, String parse_id, int rating, String gluten,String vegan,String comment, Bitmap image){
        this.nameDish=nameDish;
        this.place_id=place_id;
        this.parse_id=parse_id;
        this.rating=rating;
        this.gluten=gluten;
        this.vegan=vegan;
        this.comment=comment;
        this.image=image;
    }

    public String getNameDish(){
        return nameDish;
    }
    public String getPlace_id(){
        return place_id;
    }
    public String getParse_id(){
        return parse_id;
    }
    public int getRating(){
        return rating;
    }
    public String getGluten(){
        return gluten;
    }
    public String getVegan(){
        return vegan;
    }
    public String getComment(){
        return comment;
    }
    public Bitmap getImage(){
        return image;
    }
}
