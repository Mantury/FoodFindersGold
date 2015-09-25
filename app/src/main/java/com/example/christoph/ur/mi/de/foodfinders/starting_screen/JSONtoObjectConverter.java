package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_detail.restaurantdetailitem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by juli on 09.09.15.
 */
public class JSONtoObjectConverter {

    private String JSONResponse;

    private static final String NAME = "name";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String OPEN = "open_now";
    private static final String ADDRESS = "vicinity";
    private static final String ID = "place_id";
    private ArrayList<restaurantitemstart> list;
    private ArrayList<String> commentlist;
    public int open;

    public JSONtoObjectConverter(String JSONResponse) {
        Log.d("Set up converter");
        this.JSONResponse = JSONResponse;
    }

    public ArrayList<restaurantitemstart> convertJSONTorestaurantitemstart() {
        try {
            JSONObject jsonOb = new JSONObject(JSONResponse);
            Log.d(String.valueOf(jsonOb));
            JSONArray jsonArray = jsonOb.getJSONArray("results");
            Log.d("converter" + jsonArray.length());
            list = new ArrayList<restaurantitemstart>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject locationobject = jsonObject.getJSONObject("geometry");
                JSONObject latlngobject = locationobject.getJSONObject("location");
                double lng = latlngobject.getDouble(LNG);
                double lat = latlngobject.getDouble(LAT);
                String name = jsonObject.getString(NAME);
                JSONObject openobject = jsonObject.optJSONObject("opening_hours");
                if (openobject != null) {
                    if (openobject.getBoolean("open_now")) {
                        open = 1;
                    } else {
                        open = 2;
                    }

                } else {
                    open = 0;
                }
                String id = jsonObject.getString(ID);
                String address = jsonObject.getString(ADDRESS);
                restaurantitemstart item = new restaurantitemstart(name, lat, lng, id, open, address);
                list.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    //Creates a restaurantdetailitem by redownloading the data.
    public restaurantdetailitem convertToRestaurantDetailItem() {
        restaurantdetailitem restaurantdetail = null;
        try {
            JSONObject jsonOb = new JSONObject(JSONResponse);
            Log.d(String.valueOf(jsonOb));
            JSONObject jsonrestaurant = jsonOb.getJSONObject("result");
            String address = "nicht in google vorhanden";
            address = jsonrestaurant.optString("formatted_address");
            String number = jsonrestaurant.getString("formatted_phone_number");
            String name = jsonrestaurant.getString(NAME);
            String id = jsonrestaurant.getString(ID);
            JSONObject openinghours = jsonrestaurant.optJSONObject("opening_hours");
            boolean open = false;
            String openweekday = "notfound";
            if (openinghours != null) {
                open = openinghours.getBoolean("open_now");
                openweekday = openinghours.getString("weekday_text");
            }

            JSONArray comments = jsonrestaurant.optJSONArray("reviews");
            commentlist = new ArrayList<String>();
            if(comments!=null) {
              //  commentlist = new ArrayList<String>();
                for (int i = 0; i < comments.length(); i++) {
                    JSONObject jsonObject = comments.getJSONObject(i);
                    String comment = jsonObject.getString("text");
                    commentlist.add(comment);
                }
            }else{
                commentlist.add("Sorry no comments!");
            }

            String rating = "no ratings";
            rating = jsonrestaurant.optString("user_ratings_total");
            String image_ref = "no Image!!";
            JSONArray jsonImage = jsonrestaurant.optJSONArray("photos");
            if (jsonImage != null) {
                JSONObject image1 = jsonImage.getJSONObject(0);
                image_ref = image1.getString("photo_reference");
            }
            restaurantdetail = new restaurantdetailitem(name, image_ref, address, number, rating, id, open, openweekday, commentlist);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurantdetail;
    }
}
