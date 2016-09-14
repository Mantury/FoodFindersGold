package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.dish_item;
import com.example.christoph.ur.mi.de.foodfinders.restaurants.restaurant;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.restaurant_dishes_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.starting_screen.download;
import com.google.android.gms.ads.mediation.customevent.CustomEvent;
import com.google.android.gms.vision.text.Line;
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
import java.util.List;

//This activity displays a selected restaurant from the "starting_screen_activity" with more details.
//The screen provides openning hours, address, phone number, google comments, and  access to the app-only-dish data.

public class restaurant_detail_activity extends Activity implements download.OnRestaurantDetailDataProviderListener {

    private download data;
    private String place_id;
    private String name;
    private ArrayList<Bitmap> images= new ArrayList<>();
    private adaper_viewpager adapter;
    private ViewPager viewpager;
    private int numberimages;
    private boolean isFavored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_detail_layout);
        getIntentdata();
        setUpDownload();
        setUpUi();
    }

    private void setUpUi() {
        LinearLayout newDish = (LinearLayout) findViewById(R.id.restaurant_detail_dishlayout);
        Button addButton =(Button) findViewById(R.id.restaurant_detail_dishaddbutton);
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

        final Switch favorit = (Switch) findViewById(R.id.restaurant_detail_favswitch);
        if(isFavored==true) {
            favorit.setChecked(true);
        }
        favorit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    if(userSignedIn()) {
                        addToFavList();
                    }
                    else {
                        Toast.makeText(restaurant_detail_activity.this, "Bitte melden sie sich zuerst an", Toast.LENGTH_SHORT).show();
                        favorit.setChecked(false);
                    }

                }
                if(!isChecked) {
                    deleteFromFavList();
                }
            }
        });


    }

    private void deleteFromFavList() {

        Toast.makeText(restaurant_detail_activity.this, "Aus Favoriten entfernt", Toast.LENGTH_SHORT).show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://foodfindersgold.firebaseio.com/user/" + userID + "/Favourites");
        final Query refQuery = refReview;
        refReview.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String place = (String) dataSnapshot.getValue();
                if(place.equals(place_id)) {
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

    private void addToFavList() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String userID = user.getUid();

        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://foodfindersgold.firebaseio.com/user/"+userID+"/Favourites").push();
        refReview.setValue(place_id, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(restaurant_detail_activity.this, "Fehler beim speichern als Favorit", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(restaurant_detail_activity.this, "Zu Favoriten hinzugefügt", Toast.LENGTH_SHORT).show();
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

    private void getIntentdata()
    {
        place_id = getIntent().getStringExtra("name");
        String fav = getIntent().getStringExtra("favored");
        if(fav.equals("yes")) {isFavored=true;}
        else {isFavored=false;}
    }

    //Display all the data for the specific restaurant
    @Override
    public void onRestaurantDetailDataReceived(restaurant res) {
        showUi();
        Log.d(String.valueOf(res));
        name = res.getName();
        if (!res.getImages().isEmpty()) {
            numberimages=res.getImages().size();
            Log.d("imagesArrayanzahl1", String.valueOf(numberimages));
            Log.d("imagesArrayanzahl2", String.valueOf(res.getImages().size()));
            for(int i=0;i<res.getImages().size()-1;i++){
                data.getRestaurantPicturefromURL(res.getImages().get(i));
            }
            data.getRestaurantPicturefromURL(res.getImages().get(0));

        }else {
            ViewPager slideshow = (ViewPager) findViewById(R.id.restaurant_detail_slideshow);
             slideshow.setVisibility(View.GONE);
        }


        TextView name = (TextView) findViewById(R.id.restaurant_detail_textview_name);
        name.setText(res.getName());
        TextView öffnungzeiten = (TextView) findViewById(R.id.restaurant_detail_textview_openinghours);
        if (res.getOpenweekday().equals("notfound")) {
            öffnungzeiten.setText("Keine Öffnungszeiten verfügbar!");
        } else {
            öffnungzeiten.setText(parseOpenninghours(res.getOpenweekday()));
        }
        TextView number = (TextView) findViewById(R.id.restaurant_detail_textview_telephonenumber);
        number.setText("Telefon: " + res.getNumber());
        TextView address = (TextView) findViewById(R.id.restaurant_detail_textview_address);
        address.setText(res.getAddress());
        ListView disheslist = (ListView) findViewById(R.id.restaurant_detail_commentlistview);//Kommentare??? arraylist(String) aus item!!!
        Adapter aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, res.getComments());
        disheslist.setAdapter((ListAdapter) aa);
    }

    private void showUi() {
        ProgressBar spinner = (ProgressBar)findViewById(R.id.restaurant_detail_progressBarScreen);
        spinner.setVisibility(View.GONE);
        LinearLayout layout_top = (LinearLayout) findViewById(R.id.restaurant_detail_linearlayout_top);
        layout_top.setVisibility(View.VISIBLE);
        LinearLayout layout_dish = (LinearLayout) findViewById(R.id.restaurant_detail_dishlayout);
        layout_dish.setVisibility(View.VISIBLE);
        ProgressBar spinnerImage = (ProgressBar)findViewById(R.id.restaurant_detail_progressBarImage);
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
        //nur fürs ErsteBild Imageview bis alle anderen geladen sind?
        //ProgressBar spinner = (ProgressBar)findViewById(R.id.restaurant_detail_progressBarImage);
        //spinner.setVisibility(View.GONE);
       // ImageView image = (ImageView) findViewById(R.id.restaurant_detail_imageview);
       // image.setVisibility(View.VISIBLE);
       // image.setImageBitmap(result);

        images.add(result);
        Log.d("images", String.valueOf(images.size()));

        if(numberimages==images.size()){
        ProgressBar spinner = (ProgressBar)findViewById(R.id.restaurant_detail_progressBarImage);
        spinner.setVisibility(View.GONE);
            viewpager=(ViewPager) findViewById(R.id.restaurant_detail_slideshow);
            adapter = new adaper_viewpager(this,images);
            viewpager.setAdapter(adapter);
        }


    }

    //Adds up all in-app-dishes and displays it.
    //TODO dish Counter überarbeiten dauert noch zu lange
    private void setDishesCounter() {
        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://foodfindersgold.firebaseio.com/reviews");
        Query queryReviewRestaurant= refReview.orderByChild("place_id").equalTo(place_id);
        queryReviewRestaurant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView dishcounter = (TextView) findViewById(R.id.restaurant_detail_dishcounter);
                dishcounter.setText(dataSnapshot.getChildrenCount() + " eingetragene Gerichte");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
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
}
