package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.dish_item;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class add_dish_activity extends Activity {

    private String place_id;
    private EditText name;
    private RatingBar rating;
    private EditText comment;
    private Uri fileUri;

    private ParseFile file;//

    private boolean saveimage=false;
    private Button addimage;
    private String yes="Ja";
    private String no="Nein";
    private String noinfo="Nicht angegeben";

    private Bitmap picture=null;


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

    //gets data from Layout check if input and saves on firebase
    private void publishDish() {
        boolean ready=true;
        //dishname had to exist
        String gerichtname = String.valueOf(name.getText());
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
        //vorrübergehend deaktiviert
        if(!saveimage){
     //       ready=false;
            Toast.makeText(add_dish_activity.this, "Bitte fügen Sie ein Bild hinzu", Toast.LENGTH_SHORT).show();
       }
        //TODO extra Firebase-Klasse?!
        //TODO picture muss noch gemacht bzw. überarbeitet werden
        //checks if every required value exists, if true--> sends the data to parse
        if(ready) {
            String com = String.valueOf(comment.getText());
            //firebase Foto fehllt noch!
            dish_item dish= new dish_item(gerichtname, place_id , (int)rank ,gluten, vegan, com, picture);
            Firebase.setAndroidContext(this);
            Firebase rootRef = new Firebase("https://foodfindersgold.firebaseio.com");
            Firebase postDish = rootRef.child("reviews");
            Firebase dishID =postDish.push();
            dishID.setValue(dish, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        Toast.makeText(add_dish_activity.this, "Fehler beim hochloden der Daten", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(add_dish_activity.this, "Ihre Daten wurden erfolgreich hochgeladen", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            String id = dishID.getKey();
            dish.setDishId(id);
            Log.d(id);
            Log.d("Ending firebase");
            //firebase end
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

      // file = new ParseFile("Foto.png", image);
        saveimage=true;
        //file.saveInBackground();
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
               picture = BitmapFactory.decodeStream(is, rect, bitopt);
                sendImageToParse(picture);

                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addimage.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                imageview.setImageBitmap(picture);
                saveimage=true;
            }
            }
        }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
