package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;

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
    //private static final String RATING = "rating";
    private static final String ID="place_id";


    private ArrayList<restaurantitemstart> list;

    public JSONtoObjectConverter(String JSONResponse) {
        Log.d("Set up converter");
        this.JSONResponse = JSONResponse;
    }

    public ArrayList<restaurantitemstart> convertJSONTorestaurantitemstart() {

        try {

            JSONObject jsonOb = new JSONObject(JSONResponse);
            Log.d(String.valueOf(jsonOb));
            JSONArray jsonArray =jsonOb.getJSONArray("results");


            Log.d("converter" + jsonArray.length());

            list = new ArrayList<restaurantitemstart>();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                JSONObject locationobject = jsonObject.getJSONObject("geometry");
                JSONObject latlngobject = locationobject.getJSONObject("location");
                long lng = latlngobject.getLong(LAT);
                long lat = latlngobject.getLong(LNG);



                String name = jsonObject.getString(NAME);

           //   JSONObject openobject = jsonObject.getJSONObject("opening_hours");
           //   boolean open = openobject.getBoolean(OPEN);

                String id = jsonObject.getString(ID);
                String address = jsonObject.getString(ADDRESS);

                Log.d(name);


                restaurantitemstart item = new restaurantitemstart(name, lat, lng, id, true, address);
                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }



}
