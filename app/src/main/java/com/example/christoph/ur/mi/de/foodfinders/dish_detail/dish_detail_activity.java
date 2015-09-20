package com.example.christoph.ur.mi.de.foodfinders.dish_detail;

import android.app.Activity;
import android.os.Bundle;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Christoph on 30.08.15.
 */
public class dish_detail_activity extends Activity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_detail_layout);
        //Set up parse
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");
    }
}
