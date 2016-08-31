package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurants.restaurant;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.restaurant_dishes_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.download;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

//This activity displays a selected restaurant from the "starting_screen_activity" with more details.
//The screen provides openning hours, address, phone number, google comments, and  access to the app-only-dish data.

public class restaurant_detail_activity extends Activity implements download.OnRestaurantDetailDataProviderListener {

    private download data;
    private String place_id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_layout);
        getIntentdata();
        setUpDownload();
        setUpUi();
    }

    private void setUpUi() {
        LinearLayout newDish = (LinearLayout) findViewById(R.id.restaurant_detail_dishlayout);
        Button addButton =(Button) findViewById(R.id.restaurant_detail_dishaddbutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(restaurant_detail_activity.this, restaurant_dishes_detail_activity.class);
                in.putExtra("place_id", place_id);
                in.putExtra("name", name);
                startActivity(in);
            }
        });
        newDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//starts restaurant_dishes_detail_activity
                Intent in = new Intent(restaurant_detail_activity.this, restaurant_dishes_detail_activity.class);
                in.putExtra("place_id", place_id);
                in.putExtra("name", name);
                startActivity(in);
            }
        });
        setDishesCounter();
    }

    //Downloading all the data for the specific restaurant. THe data is downloaded again to keep it up-to-date.
    private void setUpDownload() {
        data = new download();
        data.getrestaurantdata(place_id);
        data.setOnRestaurantDetailDataProviderListener(this);
    }

    private void getIntentdata() {
        place_id = getIntent().getStringExtra("name");
    }

    //Display all the data for the specific restaurant
    @Override
    public void onRestaurantDetailDataReceived(restaurant res) {
        Log.d(String.valueOf(res));
        name = res.getName();

        if ("no Image!!" != res.getImage()) {
            data.getRestaurantPicturefromURL(res.getImage());
        } else {
            ImageView image = (ImageView) findViewById(R.id.restaurant_detail_imageview);
            image.setVisibility(View.GONE);
        }
        TextView name = (TextView) findViewById(R.id.restaurant_detail_textview_name);
        name.setText(res.getName());
        TextView öffnungzeiten = (TextView) findViewById(R.id.restaurant_detail_textview_openinghours);
        if (res.getOpenweekday().equals("notfound")) {
            öffnungzeiten.setText("Keine Öffnungszeiten verfügbar!");
        } else {
            öffnungzeiten.setText(parseOpenninghours(res.getOpenweekday()));
        }
        TextView number = (TextView) findViewById(R.id.restaurant_detail_textview_telephonenumber);
        number.setText("Telefon: " + res.getNumber());
        TextView address = (TextView) findViewById(R.id.restaurant_detail_textview_address);
        address.setText(res.getAddress());
        ListView disheslist = (ListView) findViewById(R.id.restaurant_detail_commentlistview);//Kommentare??? arraylist(String) aus item!!!
        Adapter aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, res.getComments());
        disheslist.setAdapter((ListAdapter) aa);
    }

    private String parseOpenninghours(String openWeekday) {
        String s = openWeekday;
        s = s.substring(1, s.length() - 1);
        String delims = "[\"]+";
        String[] tokens = s.split(delims);
        String parsedweekday = tokens[1] + "\n" + tokens[3] + "\n" + tokens[5] + "\n" + tokens[7] + "\n" + tokens[9] + "\n" + tokens[11] + "\n" + tokens[13];
        return parsedweekday;
    }

    //Sets the header picture.
    @Override
    public void onRestaurantDetailPictureReceived(Bitmap result) {
        ImageView image = (ImageView) findViewById(R.id.restaurant_detail_imageview);
        image.setImageBitmap(result);
    }

    //Adds up all in-app-dishes and displays it.
    private void setDishesCounter() {
        final int[] number = {0};
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gericht");
        query.whereEqualTo("restaurant_id", place_id);
        query.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> list, ParseException e) {
                                       if (e == null) {
                                           TextView dishcounter = (TextView) findViewById(R.id.restaurant_detail_dishcounter);
                                           int dishes = list.size();
                                           dishcounter.setText(dishes + " eingetragene Gerichte");
                                       } else {
                                           Log.d("Error: " + e.getMessage());

                                       }
                                   }
                               }

        );
    }
}
