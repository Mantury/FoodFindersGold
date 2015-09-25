package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_detail.restaurantdetailitem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class download {

    private ArrayList<restaurantitemstart> restaurants;
    private OnRestaurantDataProviderListener onrestaurantDataProviderListener;
    private OnRestaurantDetailDataProviderListener onRestaurantDetailDataProviderListener;
    private final String restaurantdetailurl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private final String restaurantdetailphotourl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    private final String restaurantsearchparametersurl = "&language=de&key=AIzaSyCOHM5VRlRjToNU48ncifgtSOcD5TpMTjA";

    //for Starting_screen_activity
    public void setRestaurantDataProviderListener(OnRestaurantDataProviderListener onrestaurantDataProviderListener) {
        this.onrestaurantDataProviderListener = onrestaurantDataProviderListener;
    }

    //for restaurant_detail_activity
    public void setOnRestaurantDetailDataProviderListener(OnRestaurantDetailDataProviderListener onRestaurantDetailDataProviderListener) {
        this.onRestaurantDetailDataProviderListener = onRestaurantDetailDataProviderListener;
    }

    //for Starting_screen_activity
    // gets the data in an Arraylist from converter.convertJSONToMensaDishList();
    public void getlocationdata(String request) {
        new DataAsyncTask().execute(request);

    }

    public void getrestaurantdata(String request) {
        new RestaurantAsyncTask().execute(request);
    }

    public void getRestaurantPicturefromURL(String URL) {
        new RestaurantDetailPictureAsyncTask().execute(URL);
    }

    ////for Starting_screen_activity
    private class DataAsyncTask extends AsyncTask<String, Integer, String> {

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
            Log.d(jsonString);
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {//Creates an ArrayList with JSONtoObjectConverter
            super.onPostExecute(result);
            JSONtoObjectConverter converter = new JSONtoObjectConverter(result);
            restaurants = new ArrayList<restaurantitemstart>();
            restaurants = converter.convertJSONTorestaurantitemstart();
            onrestaurantDataProviderListener.onRestaurantDataReceived(restaurants);
        }
    }

    //for restaurant_detail_activity
    private class RestaurantAsyncTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String jsonString = "";
            try {
                URL url = new URL(restaurantdetailurl + params[0] + restaurantsearchparametersurl);
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
            restaurantdetailitem restaurant = converter.convertToRestaurantDetailItem();
            onRestaurantDetailDataProviderListener.onRestaurantDetailDataReceived(restaurant);
        }
    }

    private class RestaurantDetailPictureAsyncTask extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap picture = null;
            try {
                URL url = new URL(restaurantdetailphotourl + params[0] + restaurantsearchparametersurl);
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
        public void onRestaurantDataReceived(ArrayList<restaurantitemstart> restaurants);
    }

    public interface OnRestaurantDetailDataProviderListener {
        public void onRestaurantDetailDataReceived(restaurantdetailitem item);

        public void onRestaurantDetailPictureReceived(Bitmap result);
    }
}
