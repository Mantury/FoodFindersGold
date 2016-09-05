package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.google.firebase.database.DataSnapshot;


//This class saves the data for a dish. This data gets also saved in a cloud storage and is then downloaded.
//A "dish_item" saves all the information a user wants to know about a meal. Only Users of the app can create "dish_items"

//TODO picture überarbeiten nur bitmap?!
public class dish_item {

    String nameDish;
    String place_id;
    String dishId;
    int rating;
    String gluten;
    String vegan;
    String comment;
    String image; //http://pmarshall.me/2016/02/20/image-storage-with-firebase.html //TODO string to Bitmap? möglich?
    //TODO Autor mit id (vor-und nachname)

    public dish_item(String nameDish, String place_id, int rating, String gluten, String vegan, String comment, String image) {
        this.nameDish = nameDish;
        this.place_id = place_id;
        this.rating = rating;
        this.gluten = gluten;
        this.vegan = vegan;
        this.comment = comment;
        this.image = image;
    }

    //TODO firebase umschreiben
    public dish_item(DataSnapshot fireData){
        Log.d("data",fireData.toString());
        this.nameDish = (String) fireData.child("nameDish").getValue();
        this.place_id = (String) fireData.child("place_id").getValue();
        String rating= Long.toString((Long) fireData.child("rating").getValue());
        this.rating = Integer.parseInt(rating);
        this.gluten = (String) fireData.child("gluten").getValue();
        this.vegan = (String) fireData.child("vegan").getValue();
        this.comment= (String) fireData.child("comment").getValue();
        this.image= (String) fireData.child("image").getValue();
        this.dishId= fireData.getKey();

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

    public String getImage() {
        return image;
    }


 //   public Bitmap getImageBitmap() {
 //       byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
 //       Bitmap bit =  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
 //      return bit;
 //   }
}
