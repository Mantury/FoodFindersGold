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
import com.example.christoph.ur.mi.de.foodfinders.dish_detail.dish_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

//This activity shows all the dishes from a restaurant found in the cloud storage. The user also has the possibility to add own dishes and rate.
public class restaurant_dishes_detail_activity extends Activity implements dish_item_ArrayAdapter.OnDetailRequestedListener {

    private String place_id;
    private String name;
    private dish_item_ArrayAdapter adapter;
    private ArrayList<dish_item> dishes = new ArrayList<dish_item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_dishes_detail_layout);
        getIntentData();
        setUpButtons();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        initAdapter();
        initDishList();
    }


    private void initAdapter() {
        ListView list = (ListView) findViewById(R.id.restaurant_dishes_detail_list);
        adapter = new dish_item_ArrayAdapter(restaurant_dishes_detail_activity.this, dishes);
        adapter.setOnDetailRequestedListener((dish_item_ArrayAdapter.OnDetailRequestedListener) this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initDishList() {
        dishes.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gericht");
        query.whereEqualTo("restaurant_id", place_id);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    parseListToArraylist(list);
                } else {
                    Log.d("Error: " + e.getMessage());
                }
            }

        });
        adapter.notifyDataSetChanged();

    }

    private void parseListToArraylist(List<ParseObject> list) {
        for (int i = 0; i < list.size(); i++) {
            ParseObject dish = list.get(i);
            String name = dish.getString("Name");
            String comment = dish.getString("comment");
            String parse_id = dish.getObjectId();
            String vegan = dish.getString("vegan");
            String gluten = dish.getString("gluten");
            int rating = dish.getInt("rating");
            ParseFile imagefile = dish.getParseFile("image");
            Bitmap bitmap = null;
            if (imagefile != null) { //image Bytes to bitmap!!!!
                try {
                    byte[] in = imagefile.getData();
                    bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
                    Log.d("bitmap ready");
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }
            }
            dish_item item = new dish_item(name, place_id, parse_id, rating, gluten, vegan, comment, bitmap);
            dishes.add(item);
            adapter.notifyDataSetChanged();
        }
    }

    private void setUpButtons() {
        Button add_dish = (Button) findViewById(R.id.restaurant_dishes_detail_button);
        TextView restaurant = (TextView) findViewById(R.id.restaurant_dishes_detail_header);
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
        place_id = getIntent().getStringExtra("place_id");
        name = getIntent().getStringExtra("name");
    }

    @Override
    public void onDetailRequested(String parse_id) {
        Intent in = new Intent(restaurant_dishes_detail_activity.this, dish_detail_activity.class);
        in.putExtra("parse_id", parse_id);
        startActivity(in);
        Log.d(parse_id);
    }
}
