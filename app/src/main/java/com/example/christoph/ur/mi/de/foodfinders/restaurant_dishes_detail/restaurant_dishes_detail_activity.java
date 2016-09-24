package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.add_dish.add_dish_activity;
import com.example.christoph.ur.mi.de.foodfinders.dish_detail.dish_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.login_signup_user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

//This activity shows all the dishes from a restaurant found in the cloud storage. The user also has the possibility to add own dishes and rate.
public class restaurant_dishes_detail_activity extends Activity implements dish_item_ArrayAdapter.OnDetailRequestedListener {

    private String place_id;
    private String name;
    private dish_item_ArrayAdapter adapter;
    private ArrayList<dish_item> dishes = new ArrayList<dish_item>();

    private final String FIREBASEREVURL = "https://foodfindersgold.firebaseio.com/reviews";


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
        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEREVURL);
        Query queryRef = refReview.orderByChild("place_id").equalTo(place_id);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                dish_item dish = new dish_item(dataSnapshot);
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
            public void onCancelled(DatabaseError databaseError) {

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
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent i = new Intent(restaurant_dishes_detail_activity.this, add_dish_activity.class);
                    i.putExtra("place_id", place_id);
                    startActivity(i);
                } else {
                    // User is signed out
                    showAlert();
                }
            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(restaurant_dishes_detail_activity.this);
        builder.setPositiveButton(getString(R.string.dologin_ger), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(restaurant_dishes_detail_activity.this, login_signup_user.class);
                i.putExtra("intentData", "login");
                startActivity(i);
            }
        });
        builder.setNeutralButton(getString(R.string.back_ger), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.setMessage(getString(R.string.plsLogin_ger));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getIntentData() {
        place_id = getIntent().getStringExtra("place_id");
        name = getIntent().getStringExtra("name");
        setUpName();
    }

    private void setUpName() {
        TextView restaurant = (TextView) findViewById(R.id.restaurant_dishes_detail_header);
        restaurant.setSelected(true);
        restaurant.setText(name);
    }

    //starts dish_detail_activity if a dish_item is selected
    @Override
    public void onDetailRequested(String reviewId) {
        Intent in = new Intent(restaurant_dishes_detail_activity.this, dish_detail_activity.class);
        in.putExtra("reviewId", reviewId);
        startActivity(in);
    }

    @Override
    public void onDishDeletedListener(String dishId, String userUID) {
        removeFromFirebase(userUID);
        removeFromArraylist(dishId);
    }

    private void removeFromArraylist(String dishId) {
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getDishId().equals(dishId)) {
                dishes.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    //Removes the Dish from the Dishlist if the user is the author
    private void removeFromFirebase(String userUID) {
        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEREVURL);
        final Query queryRef = refReview.orderByChild("authorID").equalTo(userUID);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(dataSnapshot.toString());
                dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(restaurant_dishes_detail_activity.this, getString(R.string.dishRemovedSucces_ger), Toast.LENGTH_SHORT).show();
                    }
                });
                queryRef.removeEventListener(this);
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
