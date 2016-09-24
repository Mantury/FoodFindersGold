package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import com.google.firebase.database.DataSnapshot;


//This class saves the data for a dish. This data gets also saved in a cloud storage and is then downloaded.
//A "dish_item" saves all the information a user wants to know about a meal. Only Users of the app can create "dish_items"

public class dish_item {

    private String nameDish;
    private String place_id;
    private String dishId;
    private String gluten;
    private String vegan;
    private String comment;
    private String image; //http://pmarshall.me/2016/02/20/image-storage-with-firebase.html
    private String author;
    private String authorID;
    private int rating;

    public dish_item(String nameDish, String place_id, int rating, String gluten, String vegan, String comment, String image, String author, String authorID) {
        this.nameDish = nameDish;
        this.place_id = place_id;
        this.rating = rating;
        this.gluten = gluten;
        this.vegan = vegan;
        this.comment = comment;
        this.image = image;
        this.author = author;
        this.authorID = authorID;
    }

    public dish_item(DataSnapshot fireData) {
        this.nameDish = (String) fireData.child("nameDish").getValue();
        this.place_id = (String) fireData.child("place_id").getValue();
        String rating = Long.toString((Long) fireData.child("rating").getValue());
        this.rating = Integer.parseInt(rating);
        this.gluten = (String) fireData.child("gluten").getValue();
        this.vegan = (String) fireData.child("vegan").getValue();
        this.comment = (String) fireData.child("comment").getValue();
        this.image = (String) fireData.child("image").getValue();
        this.author = (String) fireData.child("author").getValue();
        this.authorID = (String) fireData.child("authorID").getValue();
        this.dishId = fireData.getKey();
    }

    public String getDishId() {
        return dishId;
    }

    public void setDishId(String dishIdd) {
        dishId = dishIdd;
    }

    public String getNameDish() {
        return nameDish;
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

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorID() {
        return authorID;
    }

}
