package com.example.christoph.ur.mi.de.foodfinders.dish_detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.dish_item;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


//bild noch zu klein!!!

public class dish_detail_activity extends Activity {

    private String reviewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_detail_layout);
        getIntentData();
        setUpData();
    }

    public void getIntentData() {
        reviewId = getIntent().getStringExtra("reviewId");
        Log.d("reviewId"," "+reviewId);
    }

    //searches the dish by using the id from Firebase!
    private void setUpData() {
        Firebase.setAndroidContext(this);
        final Firebase dish = new Firebase("https://foodfindersgold.firebaseio.com/reviews");

        dish.child(reviewId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("firebasedish",snapshot.toString());
                dish_item dishItem=new dish_item(snapshot);
                setUpUi(dishItem);
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    //gets the Layoutreference and sets up the data
    private void setUpUi(dish_item dish) {
        TextView name = (TextView) findViewById(R.id.dish_detail_name);
        ImageView image = (ImageView) findViewById(R.id.dish_detail_picture);
        RatingBar rating = (RatingBar) findViewById(R.id.dish_detail_ratingbar);
        TextView vegan = (TextView) findViewById(R.id.dish_detail_vegan_info);
        TextView gluten = (TextView) findViewById(R.id.dish_detail_glutenfree_info);
        TextView comment = (TextView) findViewById(R.id.dish_detail_comment);

        //sets the data
        name.setText(dish.getNameDish());
        rating.setRating(dish.getRating());

        vegan.setText("Vegan:" + dish.getVegan());
        if (dish.getVegan().equals("Ja")) {
            vegan.setBackgroundResource(R.color.green);
        }
        if (dish.getVegan().equals("Keine Info")) {
            vegan.setBackgroundResource(R.color.yellow);
        }

        gluten.setText("Glutenfrei:" + dish.getGluten());
        if (dish.getGluten().equals("Ja")) {
            gluten.setBackgroundResource(R.color.green);
        }
        if (dish.getGluten().equals("Keine Info")) {
            gluten.setBackgroundResource(R.color.yellow);
        }

        comment.setText(dish.getComment());

        Bitmap bitmap = dish.getImageBitmap();
        //Bild wird immer angezeigt auch wenn null??
        image.setImageBitmap(bitmap);
    }
}
