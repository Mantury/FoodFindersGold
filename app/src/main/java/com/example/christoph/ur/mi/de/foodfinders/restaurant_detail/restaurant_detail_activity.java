package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.add_dish.add_dish_activity;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.download;

import java.util.ArrayList;

/**
 * Created by Christoph on 30.08.15.
 */
public class restaurant_detail_activity extends Activity implements download.OnRestaurantDetailDataProviderListener{

    private download data;
    private String place_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_layout);
        getIntentdata();
        setUpDownload();
        setUpUi();

    }

    private void setUpUi() {
        Button newDish =(Button) findViewById(R.id.restaurant_detail_dishaddbutton);
        newDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open restaurant add_dish_activity!
                Intent i = new Intent(restaurant_detail_activity.this, add_dish_activity.class);
                i.putExtra("name", place_id);
                startActivity(i);
            }
        });
        TextView dishcounter=(TextView) findViewById(R.id.restaurant_detail_dishcounter);

        int dishes=0;
        dishcounter.setText(dishes+" eingetragene Gerichte");
    }


    private void setUpDownload() {
        data=new download();
        data.getrestaurantdata(place_id);
        data.setOnRestaurantDetailDataProviderListener(this);
    }


    private void getIntentdata(){
        place_id=getIntent().getStringExtra("name");
        Log.d(place_id+" place_id");
    }



    @Override
    public void onRestaurantDetailDataReceived(restaurantdetailitem item) {
     Log.d("got item");
        Log.d(item.getName() + item.getNumber()+item.getRating());

        TextView name=(TextView) findViewById(R.id.restaurant_detail_textview_name);
        name.setText(item.getName());
        TextView öffnungzeiten=(TextView)findViewById(R.id.restaurant_detail_textview_openinghours);
        if(item.getOpenweekday()==null){
            öffnungzeiten.setText("Keine Öffnungszeiten verfügbar!!!!!!");
        }else {
            öffnungzeiten.setText(item.getOpenweekday());
        }

        TextView number=(TextView)findViewById(R.id.restaurant_detail_textview_telephonenumber);
        number.setText(item.getNumber());

        TextView address=(TextView)findViewById(R.id.restaurant_detail_textview_address);
        address.setText(item.getAddress());

        //Kommentare??? arraylist(String) aus item!!!
        ListView disheslist=(ListView) findViewById(R.id.restaurant_detail_commentlistview);
        Adapter aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, item.getComments());
        disheslist.setAdapter((ListAdapter) aa);


    }
}
