package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurants.restaurant;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.restaurant_dishes_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.download;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.login_signup_user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

//This activity displays a selected restaurant from the "starting_screen_activity" with more details.
//The screen provides openning hours, address, phone number, and  access to the app-only-dish data.

public class restaurant_detail_activity extends Activity implements download.OnRestaurantDetailDataProviderListener {

    private download data;
    private String place_id;
    private String name;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private adaper_viewpager adapter;
    private ViewPager viewpager;
    private int numberimages;
    private Switch favorit;
    private CompoundButton.OnCheckedChangeListener changeLis;
    private final String FIREBASEUSERURL = "https://foodfindersgold.firebaseio.com/user/";
    private final String FIREBASEREVURL = "https://foodfindersgold.firebaseio.com/reviews";
    private final String FIREBASEUSERURLFAV = "/Favourites";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_layout);
        getIntentdata();
        setUpDownload();
        setUpUI();
    }

    private void setUpUI() {
        LinearLayout newDish = (LinearLayout) findViewById(R.id.restaurant_detail_dishlayout);
        Button addButton = (Button) findViewById(R.id.restaurant_detail_dishaddbutton);
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
        favorit = (Switch) findViewById(R.id.restaurant_detail_favswitch);
        changeLis = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!userSignedIn()) {
                    if (isChecked) {
                        showLoginAlert();
                    }
                } else {
                    if (isChecked) {
                        addToFavList();
                    }
                    if (!isChecked) {
                        deleteFromFavList();
                    }
                }
            }
        };
    }

    private void showLoginAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(restaurant_detail_activity.this);
        builder.setPositiveButton(getString(R.string.dologin_ger), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent i = new Intent(restaurant_detail_activity.this, login_signup_user.class);
                i.putExtra("intentData", "login");
                startActivity(i);
            }
        });
        builder.setNeutralButton(getString(R.string.back_ger), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                favorit.setChecked(false);
            }
        });
        builder.setMessage(getString(R.string.plsLogin_ger));
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteFromFavList() {
        Toast.makeText(restaurant_detail_activity.this, getString(R.string.removedFav_ger), Toast.LENGTH_SHORT).show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEUSERURL + userID + FIREBASEUSERURLFAV);
        final Query refQuery = refReview;
        refReview.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String place = (String) dataSnapshot.getValue();
                if (place.equals(place_id)) {
                    dataSnapshot.getRef().removeValue();
                    refQuery.removeEventListener(this);
                }
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

    //Saving restaurant to favliist for the specific UserID
    private void addToFavList() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();
        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEUSERURL + userID + FIREBASEUSERURLFAV).push();
        refReview.setValue(place_id, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(restaurant_detail_activity.this, getString(R.string.favSaveError_ger), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(restaurant_detail_activity.this, getString(R.string.addedFav_ger), Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        showUi();
        name = res.getName();
        if (!res.getImages().isEmpty()) {
            numberimages = res.getImages().size();
            for (int i = 0; i < res.getImages().size(); i++) {
                data.getRestaurantPicturefromURL(res.getImages().get(i));
            }
        } else {
            ViewPager slideshow = (ViewPager) findViewById(R.id.restaurant_detail_slideshow);
            slideshow.setVisibility(View.GONE);
            ProgressBar spinnerImage = (ProgressBar) findViewById(R.id.restaurant_detail_progressBarImage);
            spinnerImage.setVisibility(View.GONE);
        }


        TextView name = (TextView) findViewById(R.id.restaurant_detail_textview_name);
        name.setText(res.getName());
        TextView öffnungzeiten = (TextView) findViewById(R.id.restaurant_detail_textview_openinghours);
        if (res.getOpenweekday().equals("notfound")) {
            öffnungzeiten.setText(getString(R.string.noOppeningHours_ger));
        } else {
            öffnungzeiten.setText(parseOpenninghours(res.getOpenweekday()));
        }
        TextView number = (TextView) findViewById(R.id.restaurant_detail_textview_telephonenumber);
        if (!(res.getNumber().isEmpty())) {
            number.setText(getString(R.string.phone_ger) + res.getNumber());
            number.setTextColor(getResources().getColor(R.color.blue));
        } else {
            number.setText(R.string.noPhone_ger);
            number.setClickable(false);
        }
        TextView website = (TextView) findViewById(R.id.restaurant_detail_textview_website);
        if (!(res.getWebsite().isEmpty())) {
            website.setText(getString(R.string.website_ger) + res.getWebsite());
            website.setTextColor(getResources().getColor(R.color.blue));
        } else {
            website.setText(R.string.noWebsite_ger);
            website.setClickable(false);
        }

        TextView address = (TextView) findViewById(R.id.restaurant_detail_textview_address);
        address.setText(res.getAddress());
    }

    private void showUi() {
        ProgressBar spinner = (ProgressBar) findViewById(R.id.restaurant_detail_progressBarScreen);
        spinner.setVisibility(View.GONE);
        LinearLayout layout_top = (LinearLayout) findViewById(R.id.restaurant_detail_linearlayout_top);
        layout_top.setVisibility(View.VISIBLE);
        LinearLayout layout_dish = (LinearLayout) findViewById(R.id.restaurant_detail_dishlayout);
        layout_dish.setVisibility(View.VISIBLE);
        ProgressBar spinnerImage = (ProgressBar) findViewById(R.id.restaurant_detail_progressBarImage);
        spinnerImage.setVisibility(View.VISIBLE);
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
        images.add(result);
        if (numberimages == images.size()) {
            ProgressBar spinner = (ProgressBar) findViewById(R.id.restaurant_detail_progressBarImage);
            spinner.setVisibility(View.GONE);
            viewpager = (ViewPager) findViewById(R.id.restaurant_detail_slideshow);
            adapter = new adaper_viewpager(this, images);
            viewpager.setAdapter(adapter);
        }
    }

    //Setting the number of dishes found for the restaurant using a quantity string
    private void setDishesCounter() {
        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEREVURL);
        Query queryReviewRestaurant = refReview.orderByChild("place_id").equalTo(place_id);
        queryReviewRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int dishesCount = (int) dataSnapshot.getChildrenCount();
                TextView dishcounter = (TextView) findViewById(R.id.restaurant_detail_dishcounter);
                dishcounter.setText(dishesCount + " " + (getResources().getQuantityString(R.plurals.numberOfDishes_ger, dishesCount)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private boolean userSignedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d("firebaselogin:", "user:" + auth.getCurrentUser().getUid());
            return true;
        } else {
            return false;
        }
    }

    private void checkForFavButtonSet() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();
        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEUSERURL + userID + FIREBASEUSERURLFAV);
        Query queryRef = refReview;
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                while (it.hasNext()) {
                    String placeID = it.next().getValue().toString();
                    if (placeID.equals(place_id)) {
                        favorit.setChecked(true);
                    }
                }
                favorit.setOnCheckedChangeListener(changeLis);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        favorit.setOnCheckedChangeListener(null);
        favorit.setChecked(false);
        if (!userSignedIn()) {
            favorit.setOnCheckedChangeListener(changeLis);
        }
        if (userSignedIn()) {
            checkForFavButtonSet();
        }

    }

}


