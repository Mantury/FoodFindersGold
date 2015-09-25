package com.example.christoph.ur.mi.de.foodfinders.dish_detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class dish_detail_activity extends Activity {

    private String parse_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("detailActivity+");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_detail_layout);
        //Set up parse
        //Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");
        getIntentData();
        setUpData();
    }

    public void getIntentData() {
        parse_id = getIntent().getStringExtra("parse_id");
    }

    private void setUpData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("gericht");
        Log.d(parse_id);
        query.whereEqualTo("objectId", parse_id);
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    Log.d(list.size() + "parsiert biatch :) ");
                    setUpUi(list);
                } else {
                    Log.d("Error: " + e.getMessage());
                }
            }

        });


    }

    private void setUpUi(List<ParseObject> list) {
        TextView name = (TextView) findViewById(R.id.dish_detail_name);
        ImageView image = (ImageView) findViewById(R.id.dish_detail_picture);
        RatingBar rating = (RatingBar) findViewById(R.id.dish_detail_ratingbar);
        TextView vegan = (TextView) findViewById(R.id.dish_detail_vegan_info);
        TextView gluten = (TextView) findViewById(R.id.dish_detail_glutenfree_info);
        TextView comment = (TextView) findViewById(R.id.dish_detail_comment);

        ParseObject dish = list.get(0);
        name.setText(dish.getString("Name"));
        rating.setRating(dish.getInt("rating"));


        vegan.setText("Vegan:" + dish.getString("vegan"));
        if (dish.getString("vegan").equals("Ja")) {
            vegan.setBackgroundResource(R.color.green);
        }
        if (dish.getString("vegan").equals("Keine Info")) {
            vegan.setBackgroundResource(R.color.yellow);
        }


        gluten.setText("Glutenfrei:" + dish.getString("gluten"));
        if (dish.getString("gluten").equals("Ja")) {
            gluten.setBackgroundResource(R.color.green);
        }
        if (dish.getString("gluten").equals("Keine Info")) {
            gluten.setBackgroundResource(R.color.yellow);
        }

        comment.setText(dish.getString("comment"));

        Bitmap bitmap = null;
        ParseFile imagefile = dish.getParseFile("image");
        if (imagefile != null) {
            //image Bytes to bitmap!!!!


            try {
                byte[] in = imagefile.getData();
                bitmap = BitmapFactory.decodeByteArray(in, 0, in.length);
                Log.d("bitmap ready");
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }

        }
        image.setImageBitmap(bitmap);

    }
}
