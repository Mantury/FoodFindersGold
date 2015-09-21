package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Christoph on 30.08.15.
 */
public class add_dish_activity extends Activity {

    private String place_id;
    private EditText name;
    private RatingBar rating;
    private EditText comment;
    private Uri fileUri;

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
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        rating=(RatingBar) findViewById(R.id.add_dish_ratingbar);
        comment=(EditText) findViewById(R.id.add_dish_comment);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ImageView imageview=(ImageView) findViewById(R.id.add_dish_photoimage);
               //imageview.setImageURI(fileUri);

            }
        });
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
        //ParseFile file= sendImageToParse(fileUri);
        //file.saveInBackground();

        ParseObject gericht = new ParseObject("gericht");

      //  gericht.put("image",file);
        gericht.put("restaurant_id",place_id);
        gericht.put("rating",rank);
        gericht.put("comment",com);
        gericht.put("gluten",gluten);
        gericht.put("vegan",vegan);
        gericht.put("Name", restaurantname);

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

    private void takePicture() {
        fileUri = FoodFindersImageFileHelper.getOutputImageFileURL(); // create a file to save the image
        grabImageFromCamera(fileUri);

    }

    private ParseFile sendImageToParse(Bitmap bit) {
       // Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(fileUri));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        ParseFile file = new ParseFile("Foto.png", image);
        return file;
    }


    private void grabImageFromCamera(Uri fileUri) {
        Intent takeFoodieImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeFoodieImage.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        takeFoodieImage.putExtra("filename", fileUri.toString());
        //takeFoodieImage.putExtra("filename", String.valueOf(fileUri));
        startActivityForResult(takeFoodieImage, 0);

      //  Bitmap image=FoodieImageFileHelper.getBitmap(fileUri.toString());
      //  sendImageToParse(image);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("super    es wird resümiert!!!");
        ImageView imageview = (ImageView) findViewById(R.id.add_dish_photoimage);
        //Bitmap bit =FoodFindersImageFileHelper.getScaledBitmap(String.valueOf(fileUri));
        //ParseFile file= sendImageToParse(fileUri);
        //file.saveInBackground();

        if (fileUri != null) {
            //image zu bitmap umwandeln
            InputStream is = null;
            try {
                is = getContentResolver().openInputStream(fileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            ParseFile file =sendImageToParse(bitmap);
            file.saveInBackground();


            ParseObject imgupload = new ParseObject("ImageUpload");
            imgupload.put("ImageName", "AndroidBegin Logo");
            imgupload.put("ImageFile", file);
            imgupload.saveInBackground();

            Toast.makeText(add_dish_activity.this, "Image Uploaded",
                    Toast.LENGTH_SHORT).show();

            Log.d("wo is des image???");

            try {
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            imageview.setImageBitmap(bitmap);
            Log.d("uri     !   " + fileUri.toString());
      //  imageview.setImageURI(fileUri);
         }


    }
}
