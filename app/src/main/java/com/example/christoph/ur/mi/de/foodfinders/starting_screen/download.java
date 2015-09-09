package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.os.AsyncTask;

import com.example.christoph.ur.mi.de.foodfinders.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by juli on 08.09.15.
 */
public class download{

    private ArrayList<restaurantitemstart> restaurants;
    private OnRestaurantDataProviderListener onrestaurantDataProviderListener;

    public void setRestaurantDataProviderListener(OnRestaurantDataProviderListener onrestaurantDataProviderListener) {
        this.onrestaurantDataProviderListener = onrestaurantDataProviderListener;
    }

    public void getlocationdata(String request) {

        new DataAsyncTask().execute(request);
        // gets the data in an Arraylist from converter.convertJSONToMensaDishList();

    }

    private class DataAsyncTask  extends AsyncTask<String, Integer, String> {


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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("result   "+result);
            JSONtoObjectConverter converter = new JSONtoObjectConverter(result);
            restaurants = new ArrayList<restaurantitemstart>();
            restaurants = converter.convertJSONTorestaurantitemstart();

            Log.d(String.valueOf(restaurants.size()));
            onrestaurantDataProviderListener.onRestaurantDataReceived(restaurants);

            Log.d("alle restaurantitemstartobjecte");
            Log.d(String.valueOf(restaurants));

        }
    }

    public interface OnRestaurantDataProviderListener {

        public void onRestaurantDataReceived(ArrayList<restaurantitemstart> restaurants);

    }

}
