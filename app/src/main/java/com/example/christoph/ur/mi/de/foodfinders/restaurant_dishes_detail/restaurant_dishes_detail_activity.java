package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.add_dish.add_dish_activity;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.Parse;

/**
 * Created by Christoph on 30.08.15.
 */
public class restaurant_dishes_detail_activity extends Activity {

    private String place_id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_dishes_detail_layout);
        getIntentData();
        setUpButtons();
     //   Parse.enableLocalDatastore(this);
     //   Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");
    }

    private void setUpButtons() {
        Button add_dish =(Button) findViewById(R.id.restaurant_dishes_detail_button);
        TextView restaurant=(TextView) findViewById(R.id.restaurant_dishes_detail_header);
        restaurant.setText(name);
        add_dish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(restaurant_dishes_detail_activity.this, add_dish_activity.class);
                i.putExtra("place_id", place_id);
                startActivity(i);
            }
        });
    }


    private void getIntentData() {
        place_id=getIntent().getStringExtra("place_id");
        name=getIntent().getStringExtra("name");
    }
}
