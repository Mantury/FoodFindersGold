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
    private Button addimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish_layout);
        getIntentdata();
        SetUpUi();
    }

    private void SetUpUi() {
        name = (EditText) findViewById(R.id.add_dish_nameedit);
        final Button addimage = (Button) findViewById(R.id.add_dish_add_picture_button); //neuer Button für Foto hinzufügen nötig; Layout!!!
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                addimage.setVisibility(View.INVISIBLE);
            }
        });

        rating = (RatingBar) findViewById(R.id.add_dish_ratingbar);
        comment = (EditText) findViewById(R.id.add_dish_comment);
        Button addDish = (Button) findViewById(R.id.add_dish_addbutton);
        addDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishDish();
            }
        });//opens the methode to send Data to parse
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

    //gets Data form Layout and saves on parse.com
    private void publishDish() {
        String restaurantname = String.valueOf(name.getText());
        String gluten = getGlutenRadiodata();
        String vegan = getVeganRadiodata();
        float rank = rating.getRating();
        String com = String.valueOf(comment.getText());

        ParseObject gericht = new ParseObject("gericht");
        gericht.put("image", file);
        gericht.put("restaurant_id", place_id);
        gericht.put("rating", rank);
        gericht.put("comment", com);
        gericht.put("gluten", gluten);
        gericht.put("vegan", vegan);
        gericht.put("Name", restaurantname);
        gericht.saveInBackground();
        Toast.makeText(add_dish_activity.this, "Dish Uploaded", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getGlutenRadiodata() {
        RadioButton glutenYes = (RadioButton) findViewById(R.id.radio_glutenfree_yes);
        RadioButton glutenNoInfo = (RadioButton) findViewById(R.id.radio_glutenfree_noinfo);
        RadioButton glutenNo = (RadioButton) findViewById(R.id.radio_glutenfree_no);
        String gluten;
        if (glutenYes.isChecked()) {
            gluten = "YES";
        } else if (glutenNo.isChecked()) {
            gluten = "NO";
        } else {
            gluten = "NOINFO";
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
            vegan = "YES";
        } else if (veganNo.isChecked()) {
            vegan = "NO";
        } else {
            vegan = "NOINFO";
            veganNoInfo.toggle();
        }
        return vegan;
    }

    private void takePicture() {
        fileUri = FoodFindersImageFileHelper.getOutputImageFileURL(); // create a file to save the image
        grabImageFromCamera(fileUri);
    }


    private void sendImageToParse(Bitmap bit) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // send Bitmap image to parse and returns the file!
        bit.compress(Bitmap.CompressFormat.PNG, 50, stream);
        //uploads the image
        byte[] image = stream.toByteArray();
        file = new ParseFile("Foto.jpeg", image);
        file.saveInBackground();
    }

    private void grabImageFromCamera(Uri fileUri) {
        Intent takeFoodieImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeFoodieImage.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        takeFoodieImage.putExtra("filename", fileUri.toString());
        startActivityForResult(takeFoodieImage, 0);
    }

    //tries to convert the image(fileUri) to Bitmap and upload it on parse.com
    //and sets the Imageview
    @Override
    protected void onResume() {
        super.onResume();

        File file = new File(String.valueOf(fileUri));
        final Button addimage = (Button) findViewById(R.id.add_dish_add_picture_button); //neuer Button für Foto hinzufügen nötig; Layout!!!

        if (file.exists()){


        ImageView imageview = (ImageView) findViewById(R.id.add_dish_photoimage);
        imageview.setVisibility(View.VISIBLE);

        if (fileUri != null) { //image(fileUri) zu bitmap umwandeln
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
            Bitmap bit = BitmapFactory.decodeStream(is, rect, bitopt);
            sendImageToParse(bit);
            Toast.makeText(add_dish_activity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();

            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageview.setVisibility(View.VISIBLE);
            imageview.setImageBitmap(bit);
        }
    }
        else{
            addimage.setVisibility(View.VISIBLE);
        }
    }
}
