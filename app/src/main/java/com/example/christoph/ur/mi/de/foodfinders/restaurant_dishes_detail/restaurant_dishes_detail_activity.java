package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.add_dish.add_dish_activity;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 30.08.15.
 */
public class restaurant_dishes_detail_activity extends Activity {

    private String place_id;
    private String name;
    private dish_item_ArrayAdapter adapter;
    private ArrayList<dish_item> dishes=new ArrayList<dish_item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_dishes_detail_layout);
        getIntentData();
        setUpButtons();
        initAdapter();
        initDishList();
     //   Parse.enableLocalDatastore(this);
     //   Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");
    }

    @Override
    protected void onResume(){
        super.onResume();
        initAdapter();
        initDishList();


    }

    private void initAdapter() {
        ListView list = (ListView) findViewById(R.id.restaurant_dishes_detail_list);
        adapter=new dish_item_ArrayAdapter(restaurant_dishes_detail_activity.this, dishes);
     //   adapter.setOnDetailRequestedListener((dish_item_ArrayAdapter.OnDetailRequestedListener) this);//???
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initDishList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gericht");
        query.whereEqualTo("restaurant_id", place_id);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    Log.d("Restarrant:  "+place_id+"  gerichte: " + list.size() + "parsiert biatch :) ");
                    parseListToArraylist(list);
                } else {
                    Log.d("Error: " + e.getMessage());
                }
            }

        });
        adapter.notifyDataSetChanged();

    }

    private void parseListToArraylist(List<ParseObject> list) {

        for(int i=0;i<list.size();i++){
            ParseObject dish=list.get(i);
            String name=dish.getString("Name");
            String comment=dish.getString("comment");
            String parse_id=dish.getString("objectId");
            String vegan=dish.getString("vegan");
            String gluten=dish.getString("gluten");
            int rating=dish.getInt("rating");
            ParseFile imagefile=dish.getParseFile("image");
            //image Bytes to bitmap!!!!
            Bitmap bitmap=null;

         //   try {
           //     byte[] in=imagefile.getData();
           //     bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
           //     Log.d("bitmap ready");
          //  } catch (com.parse.ParseException e) {
          //      e.printStackTrace();
          //  }

           dish_item item=new dish_item(name,place_id,parse_id,rating,gluten,vegan,comment,bitmap);
            dishes.add(item);
            adapter.notifyDataSetChanged();
        }

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
