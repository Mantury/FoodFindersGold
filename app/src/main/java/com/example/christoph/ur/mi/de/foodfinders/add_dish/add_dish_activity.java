package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class add_dish_activity extends Activity {

    private String place_id;
    private EditText name;
    private RatingBar rating;
    private EditText comment;
    private Uri fileUri;
    private ParseFile file;
    private boolean saveimage=false;
    private Button addimage;

    private String yes="Ja";
    private String no="Nein";
    private String noinfo="Nicht angegeben";


    //Strings for the parseobject and its attributes
    private String nameodject_parse="gericht";
    private String placeid_parse="restaurant_id";
    private String rating_parse="rating";
    private String comment_parse="comment";
    private String gluten_parse="gluten";
    private String vegan_parse="vegan";
    private String namedish_parse="Name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish_layout);
        getIntentdata();
        SetUpUi();
    }

    private void SetUpUi() {
        name = (EditText) findViewById(R.id.add_dish_nameedit);
        addimage = (Button) findViewById(R.id.add_dish_add_picture_button);
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        rating = (RatingBar) findViewById(R.id.add_dish_ratingbar);
        comment = (EditText) findViewById(R.id.add_dish_comment);
        //addDish on click send data to parse
        Button addDish = (Button) findViewById(R.id.add_dish_addbutton);
        addDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishDish();
            }
        });
        //cancelDish finishes the Activity
        Button cancelDish = (Button) findViewById(R.id.add_dish_cancelbutton);
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

    //gets data from Layout and saves on parse.com
    private void publishDish() {
        boolean ready=true;
        //dishname had to exist
        String gerichtname = String.valueOf(name.getText());;
        if(gerichtname.equals("")){
            ready=false;
            Toast.makeText(add_dish_activity.this, "Bitte geben Sie den Namen des Gerichts an", Toast.LENGTH_SHORT).show();
        }

        String gluten = getGlutenRadiodata();
        String vegan = getVeganRadiodata();
        //rating had to exist
        float rank = rating.getRating();
        if(0 == rank){
            ready=false;
            Toast.makeText(add_dish_activity.this, "Bitte bewerten sie das Gericht", Toast.LENGTH_SHORT).show();
        }
        if(!saveimage){
            ready=false;
            Toast.makeText(add_dish_activity.this, "Bitte fügen Sie ein Bild hinzu", Toast.LENGTH_SHORT).show();
       }


        //checks if every required value exists, if true--> sends the data to parse
        if(ready) {
            String com = String.valueOf(comment.getText());
            //creats an parseObject with the values from the user
            ParseObject gericht = new ParseObject(nameodject_parse);
                gericht.put("image", file);

            gericht.put(placeid_parse, place_id);
            gericht.put(rating_parse, rank);
            gericht.put(comment_parse, com);
            gericht.put(gluten_parse, gluten);
            gericht.put(vegan_parse, vegan);
            gericht.put(namedish_parse, gerichtname);
            gericht.saveInBackground();
            Toast.makeText(add_dish_activity.this, "Gericht hochgeladen", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private String getGlutenRadiodata() {
        RadioButton glutenYes = (RadioButton) findViewById(R.id.radio_glutenfree_yes);
        RadioButton glutenNoInfo = (RadioButton) findViewById(R.id.radio_glutenfree_noinfo);
        RadioButton glutenNo = (RadioButton) findViewById(R.id.radio_glutenfree_no);
        String gluten;
        if (glutenYes.isChecked()) {
            gluten = yes;
        } else if (glutenNo.isChecked()) {
            gluten = no;
        } else {
            gluten = noinfo;
            glutenNoInfo.toggle();
        }
        return gluten;
    }

    public String getVeganRadiodata() {
        RadioButton veganYes = (RadioButton) findViewById(R.id.radio_vegan_yes);
        RadioButton veganNoInfo = (RadioButton) findViewById(R.id.radio_vegan_noinfo);
        RadioButton veganNo = (RadioButton) findViewById(R.id.radio_vegan_no);
        String vegan;
        if (veganYes.isChecked()) {
            vegan = yes;
        } else if (veganNo.isChecked()) {
            vegan = no;
        } else {
            vegan = noinfo;
            veganNoInfo.toggle();
        }
        return vegan;
    }

    private void takePicture() {
        // creates the file for the image
        fileUri = FoodFindersImageFileHelper.getOutputImageFileURL();
        grabImageFromCamera(fileUri);
    }


    private void  sendImageToParse(Bitmap bit) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // send Bitmap image to parse and returns the file!
         bit.compress(Bitmap.CompressFormat.PNG, 50, stream);
        //uploads the image
        byte[] image = stream.toByteArray();
        file = new ParseFile("Foto.png", image);
        saveimage=true;
        file.saveInBackground();
        Toast.makeText(add_dish_activity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
    }

    //starts intent to capture a image
    private void grabImageFromCamera(Uri fileUri) {
        Intent takeFoodieImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeFoodieImage.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        takeFoodieImage.putExtra("filename", fileUri.toString());
        startActivityForResult(takeFoodieImage, 0);
    }

    // converts the image(fileUri) to Bitmap
    //and sets the Imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        if (resultCode == RESULT_OK) {
            ImageView imageview = (ImageView) findViewById(R.id.add_dish_photoimage);

            //image(fileUri) zu bitmap umwandeln
            if (fileUri != null) {
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(fileUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //compress image
                BitmapFactory.Options bitopt = new BitmapFactory.Options();
                bitopt.inJustDecodeBounds = true;
                bitopt.inSampleSize = 10;
                bitopt.inJustDecodeBounds = false;
                Rect rect = new Rect(1, 1, 1, 1);
              Bitmap  bit = BitmapFactory.decodeStream(is, rect, bitopt);
                sendImageToParse(bit);
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addimage.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                imageview.setImageBitmap(bit);
                saveimage=true;

            }
            }
        }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
