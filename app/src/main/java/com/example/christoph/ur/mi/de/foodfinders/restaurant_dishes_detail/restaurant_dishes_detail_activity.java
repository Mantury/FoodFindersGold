package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.add_dish.add_dish_activity;
import com.example.christoph.ur.mi.de.foodfinders.dish_detail.dish_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.download;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
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
        setUpAddButton();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDishList();
        adapter.notifyDataSetChanged();
    }


    private void initAdapter() {
        ListView list = (ListView) findViewById(R.id.restaurant_dishes_detail_list);
        adapter = new dish_item_ArrayAdapter(restaurant_dishes_detail_activity.this, dishes);
        adapter.setOnDetailRequestedListener(this);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initDishList() {
        dishes.clear();
        Toast.makeText(restaurant_dishes_detail_activity.this, "Loading...", Toast.LENGTH_SHORT).show();

        //start firebase
        Firebase.setAndroidContext(this);
        final Firebase dish = new Firebase("https://foodfindersgold.firebaseio.com/reviews");
        Query queryRef= dish.orderByChild("place_id").equalTo(place_id);
        queryRef.addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to Firebase
            //TODO neuerste zuerst?default?
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                dish_item dish = new dish_item(snapshot);
                dishes.add(dish);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
        adapter.notifyDataSetChanged();
    }

    //addbutton starts the add_dish_activitys
    private void setUpAddButton() {
        Button add_dish = (Button) findViewById(R.id.restaurant_dishes_detail_button);
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
        setUpName();
    }

    private void setUpName() {
        TextView restaurant = (TextView) findViewById(R.id.restaurant_dishes_detail_header);
        restaurant.setText(name);
    }

    //starts dish_detail_activity if a dish_item is selected
    @Override
    public void onDetailRequested(String reviewId) {
        Intent in = new Intent(restaurant_dishes_detail_activity.this, dish_detail_activity.class);
        in.putExtra("reviewId", reviewId);
        startActivity(in);
    }

}
