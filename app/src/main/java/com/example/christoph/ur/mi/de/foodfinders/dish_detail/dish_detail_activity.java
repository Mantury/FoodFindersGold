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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

//bild noch zu klein!!!

public class dish_detail_activity extends Activity {

    private String parse_id;
    private String objectId="objectId";
    private String parseobject="gericht";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dish_detail_layout);
        getIntentData();
        setUpData();
    }

    public void getIntentData() {
        parse_id = getIntent().getStringExtra("parse_id");
    }

    //searches the dish by using the objectId from parse!
    private void setUpData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(parseobject);
        query.whereEqualTo(objectId, parse_id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(parseObject!=null){
                    setUpUi(parseObject);
                }
            }
        });


    }

    //gets the Layoutreference and sets up the data
    private void setUpUi(ParseObject dish) {
        TextView name = (TextView) findViewById(R.id.dish_detail_name);
        ImageView image = (ImageView) findViewById(R.id.dish_detail_picture);
        RatingBar rating = (RatingBar) findViewById(R.id.dish_detail_ratingbar);
        TextView vegan = (TextView) findViewById(R.id.dish_detail_vegan_info);
        TextView gluten = (TextView) findViewById(R.id.dish_detail_glutenfree_info);
        TextView comment = (TextView) findViewById(R.id.dish_detail_comment);

        //sets the data
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

        //gets the image from parse
        Bitmap bitmap = null;
        ParseFile imagefile = dish.getParseFile("image");
        if (imagefile != null) {
            //kein foto wurde hochgeladen!!
            try {
                //
                byte[] picture = imagefile.getData();
                bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
        }
        //Bild wird immer angezeigt auch wenn null??
        image.setImageBitmap(bitmap);
    }
}
