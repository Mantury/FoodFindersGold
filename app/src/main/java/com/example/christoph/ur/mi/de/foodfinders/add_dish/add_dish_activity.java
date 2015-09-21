package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Christoph on 30.08.15.
 */
public class add_dish_activity extends Activity {

    private String place_id;
    private EditText name;
    private RatingBar rating;
    private EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish_layout);
        //Set up parse nur einmal!!!!!---> hier nicht nötig????
       // Parse.enableLocalDatastore(this);
        //Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");
        getIntentdata();
        SetUpUi();


    }

    private void SetUpUi() {
        name=(EditText) findViewById(R.id.add_dish_nameedit);
        rating=(RatingBar) findViewById(R.id.add_dish_ratingbar);
        comment=(EditText) findViewById(R.id.add_dish_comment);
        Button addDish=(Button) findViewById(R.id.add_dish_addbutton);
        addDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishDish();
                finish();
            }
        });
        Button cancelDish=(Button) findViewById(R.id.add_dish_cancelbutton);
        cancelDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getIntentdata() {
        place_id = getIntent().getStringExtra("place_id");
    }

    private void publishDish(){

        //String restaurantId=place_id;
        String restaurantname= String.valueOf(name.getText());
        String gluten=getGlutenRadiodata();
        String vegan=getVeganRadiodata();
        float rank = rating.getRating();
        String com=String.valueOf(comment.getText());

        ParseObject gericht = new ParseObject("gericht");
        gericht.put("restaurant_id",place_id);
        gericht.put("rating",rank);
        gericht.put("comment",com);
        gericht.put("gluten",gluten);
        gericht.put("vegan",vegan);
        gericht.put("Name",restaurantname);
        gericht.saveInBackground();

    }

    private String getGlutenRadiodata(){
        RadioButton glutenYes=(RadioButton) findViewById(R.id.radio_glutenfree_yes);
        RadioButton glutenNoInfo=(RadioButton) findViewById(R.id.radio_glutenfree_noinfo);
        RadioButton glutenNo=(RadioButton) findViewById(R.id.radio_glutenfree_no);
        String gluten;
        if(glutenYes.isChecked()){
            gluten="YES";
        }else if(glutenNo.isChecked()){
            gluten="NO";
        }else{
            gluten="NOINFO";
            glutenNoInfo.toggle();
        }
        return gluten;
    }

    public String getVeganRadiodata() {
        RadioButton veganYes=(RadioButton) findViewById(R.id.radio_vegan_yes);
        RadioButton veganNoInfo=(RadioButton) findViewById(R.id.radio_vegan_noinfo);
        RadioButton veganNo=(RadioButton) findViewById(R.id.radio_vegan_no);
        String vegan;
        if(veganYes.isChecked()){
            vegan="YES";
        }else if(veganNo.isChecked()){
            vegan="NO";
        }else{
            vegan="NOINFO";
            veganNoInfo.toggle();
        }
        return vegan;
    }
}
