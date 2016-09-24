package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurants.restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//This class downloads all information needed from the Google-APIs.

public class download {

    private ArrayList<restaurant> restaurants;
    private OnRestaurantDataProviderListener onrestaurantDataProviderListener;
    private OnRestaurantDetailDataProviderListener onRestaurantDetailDataProviderListener;
    private final String RESTAURANDETAILURL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private final String RESTAURANTDETAILIMGURL= "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    private final String RESTAURANTRADIUSURL = "&language=de&key=AIzaSyCOHM5VRlRjToNU48ncifgtSOcD5TpMTjA";

    //For the starting_screen_activity.
    public void setRestaurantDataProviderListener(OnRestaurantDataProviderListener onrestaurantDataProviderListener) {
        this.onrestaurantDataProviderListener = onrestaurantDataProviderListener;
    }

    //For the restaurant_detail_activity.
    public void setOnRestaurantDetailDataProviderListener(OnRestaurantDetailDataProviderListener onRestaurantDetailDataProviderListener) {
        this.onRestaurantDetailDataProviderListener = onRestaurantDetailDataProviderListener;
    }

    //For the starting_screen_activity.
    //Gets the data in an Arraylist from converter.convertJSONToMensaDishList();.

    public void getCloseRestaurantsdata(String request) {
        new RestaurantAsyncTask().execute(request);

    }

    public void getrestaurantdata(String request) {
        new RestaurantDetailsAsyncTask().execute(request);
    }

    public void getRestaurantPicturefromURL(String URL) {
        new RestaurantDetailPictureAsyncTask().execute(URL);
    }

    //For the starting_screen_activity.
    private class RestaurantAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String jsonString = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        jsonString += line;
                    }
                    br.close();
                    is.close();
                    conn.disconnect();
                } else {
                    throw new IllegalStateException("HTTP response: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        //Creates an ArrayList with JSONtoObjectConverter
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONtoObjectConverter converter = new JSONtoObjectConverter(result);
            restaurants = new ArrayList<restaurant>();
            restaurants = converter.convertJSONTorestaurant();
            onrestaurantDataProviderListener.onRestaurantDataReceived(restaurants);
        }
    }

    //For the restaurant_detail_activity.
    private class RestaurantDetailsAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String jsonString = "";
            try {
                URL url = new URL(RESTAURANDETAILURL + params[0] + RESTAURANTRADIUSURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = br.readLine()) != null) {
                        jsonString += line;
                    }
                    br.close();
                    is.close();
                    conn.disconnect();
                } else {
                    throw new IllegalStateException("HTTP response: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(jsonString);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONtoObjectConverter converter = new JSONtoObjectConverter(result);
            restaurant restaurant = converter.convertToDetailedRestaurant();
            onRestaurantDetailDataProviderListener.onRestaurantDetailDataReceived(restaurant);
        }
    }

    private class RestaurantDetailPictureAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap picture = null;
            try {
                URL url = new URL(RESTAURANTDETAILIMGURL+ params[0] + RESTAURANTRADIUSURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                picture = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return picture;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            onRestaurantDetailDataProviderListener.onRestaurantDetailPictureReceived(result);
        }
    }

    public interface OnRestaurantDataProviderListener {
        public void onRestaurantDataReceived(ArrayList<restaurant> restaurants);
    }

    public interface OnRestaurantDetailDataProviderListener {

        public void onRestaurantDetailDataReceived(restaurant item);

        public void onRestaurantDetailPictureReceived(Bitmap result);
    }
}
