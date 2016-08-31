package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.graphics.Bitmap;

//This class saves the data for a dish. This data gets also saved in a cloud storage and is then downloaded.
//A "dish_item" saves all the information a user wants to know about a meal. Only Users of the app can create "dish_items"

public class dish_item {

    String nameDish;
    String place_id;
    String dishId;
    int rating;
    String gluten;
    String vegan;
    String comment;
    Bitmap image;

    public dish_item(String nameDish, String place_id, int rating, String gluten, String vegan, String comment, Bitmap image) {
        this.nameDish = nameDish;
        this.place_id = place_id;
        this.rating = rating;
        this.gluten = gluten;
        this.vegan = vegan;
        this.comment = comment;
        this.image = image;
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishId) {
        this.dishId = dishId;
    }
    public String getNameDish() {
        return nameDish;
    }

    public String getPlace_id() {
        return place_id;
    }

    public int getRating() {
        return rating;
    }

    public String getGluten() {
        return gluten;
    }

    public String getVegan() {
        return vegan;
    }

    public String getComment() {
        return comment;
    }

    public Bitmap getImage() {
        return image;
    }
}
