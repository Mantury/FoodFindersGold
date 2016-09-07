package com.example.christoph.ur.mi.de.foodfinders.dish_detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.dish_item;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl("https://foodfindersgold.firebaseio.com/reviews");
        refReview.child(reviewId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("firebaseReview", dataSnapshot.toString());
                dish_item dishItem = new dish_item(dataSnapshot);
                setUpUi(dishItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("firebaseReview", "The read failed" + databaseError.toString());

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
        TextView author = (TextView) findViewById(R.id.dish_detail_author);
        //sets the data
        name.setText(dish.getNameDish());
        author.setText("von: "+dish.getAuthor());
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

            byte[] imageAsBytes = Base64.decode(dish.getImage().getBytes(), Base64.DEFAULT);
            Bitmap bit =  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
       // Bitmap bitmap = dish.getImageBitmap();
        //Bild wird immer angezeigt auch wenn null??
       image.setImageBitmap(bit);
    }
}
