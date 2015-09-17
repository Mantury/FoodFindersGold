package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.app.Activity;
import android.os.Bundle;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.Parse;

/**
 * Created by Christoph on 30.08.15.
 */
public class add_dish_activity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish_layout);
        //Set up parse
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");


    }
}
