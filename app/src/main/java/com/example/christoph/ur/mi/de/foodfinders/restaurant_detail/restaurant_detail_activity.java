package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.app.Activity;
import android.os.Bundle;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.download;

/**
 * Created by Christoph on 30.08.15.
 */
public class restaurant_detail_activity extends Activity implements download.OnRestaurantDetailDataProviderListener{

    private String name;
    private download data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_layout);
        getIntentdata();
        setUpDownload();

    }

    private void setUpDownload() {
        data=new download();
        data.getrestaurantdata(name);
        data.setOnRestaurantDetailDataProviderListener(this);


    }


    private void getIntentdata(){
        name=getIntent().getStringExtra("name");
        Log.d(name+"place_id");
    }

    @Override
    public void onRestautantDetailDataReceived(restaurantdetailitem item) {
        //set up all information
    }
}
