package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail.dish_item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private String firepicture;
    private boolean saveimage = false;
    private Button addimage;
    private final String FirebaseReviewURL = "https://foodfindersgold.firebaseio.com/reviews";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dish_layout);
        getIntentdata();
        SetUpUI();
    }

    //Sets up the UI elements with their listeners
    private void SetUpUI() {
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
        //addDish on click send data to Firebase
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

    //gets data from Layout check if input and saves on Firebase
    private void publishDish() {
        boolean ready = true;
        //dishname has to exist
        String gerichtname = String.valueOf(name.getText());
        if (gerichtname.isEmpty()) {
            ready = false;
            Toast.makeText(add_dish_activity.this, getString(R.string.inputNamepls_ger), Toast.LENGTH_SHORT).show();
        }
        String gluten = getGlutenRadiodata();
        String vegan = getVeganRadiodata();
        //rating had to exist
        float rank = rating.getRating();
        if (0 == rank) {
            ready = false;
            Toast.makeText(add_dish_activity.this, getString(R.string.inputRatingpls_ger), Toast.LENGTH_SHORT).show();
        }
        //vorrübergehend deaktiviert
        if (!saveimage) {
            ready = false;
            Toast.makeText(add_dish_activity.this, getString(R.string.inputImagepls_ger), Toast.LENGTH_SHORT).show();
        }

        //checks if every required value exists, if true--> sends the data to parse
        //funktionierts
        if (ready) {
            String com = String.valueOf(comment.getText());

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String username = auth.getCurrentUser().getDisplayName();
            String uID = auth.getCurrentUser().getUid();
            dish_item dish = new dish_item(gerichtname, place_id, (int) rank, gluten, vegan, com, firepicture, username, uID);

            DatabaseReference refReview = FirebaseDatabase.getInstance().getReferenceFromUrl(FirebaseReviewURL).push();
            refReview.setValue(dish, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toast.makeText(add_dish_activity.this, getString(R.string.imageULerror_ger), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(add_dish_activity.this, getString(R.string.imageULsuccess_ger), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            String id = refReview.getKey();
            dish.setDishId(id);
            finish();
        }

    }

    //Gets Radiobutton info
    private String getGlutenRadiodata() {
        RadioButton glutenYes = (RadioButton) findViewById(R.id.radio_glutenfree_yes);
        RadioButton glutenNoInfo = (RadioButton) findViewById(R.id.radio_glutenfree_noinfo);
        RadioButton glutenNo = (RadioButton) findViewById(R.id.radio_glutenfree_no);
        String gluten;
        if (glutenYes.isChecked()) {
            gluten = getString(R.string.add_dish_layout_radio_yes);
        } else if (glutenNo.isChecked()) {
            gluten = getString(R.string.add_dish_layout_radio_no);
        } else {
            gluten = getString(R.string.add_dish_layout_radio_noinfo);
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
            vegan = getString(R.string.add_dish_layout_radio_yes);
        } else if (veganNo.isChecked()) {
            vegan = getString(R.string.add_dish_layout_radio_no);
        } else {
            vegan = getString(R.string.add_dish_layout_radio_noinfo);
            veganNoInfo.toggle();
        }
        return vegan;
    }

    private void takePicture() {
        //Creates the file for the image
        fileUri = FoodFindersImageFileHelper.getOutputImageFileURL();
        grabImageFromCamera(fileUri);
    }


    private void convertImageForFirebase(Bitmap bit) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] image = stream.toByteArray();
        firepicture = Base64.encodeToString(image, Base64.DEFAULT);
        saveimage = true;
    }

    //Starts intent to capture an image
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
                //Compresses image
                BitmapFactory.Options bitopt = new BitmapFactory.Options();
                bitopt.inJustDecodeBounds = true;
                bitopt.inSampleSize = 10;
                bitopt.inJustDecodeBounds = false;
                Rect rect = new Rect(1, 1, 1, 1);
                Bitmap bit = BitmapFactory.decodeStream(is, rect, bitopt);
                convertImageForFirebase(bit);
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                addimage.setVisibility(View.GONE);
                imageview.setVisibility(View.VISIBLE);
                imageview.setImageBitmap(bit);
                saveimage = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
