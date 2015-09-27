package com.example.christoph.ur.mi.de.foodfinders.add_dish;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//Vorlage aus der 2.Studienleistung(Foodie) erstellt einen "eingigartigen filename"!!

public class FoodFindersImageFileHelper {

    public static String IMAGE_DIRECTORY = "FoodFindersApp";
    public static String LOG_MESSAGE = "FoodFinders";

    public static Uri getOutputImageFileURL() {
        Log.d(LOG_MESSAGE, "external dir:" + Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath());
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(LOG_MESSAGE, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getAbsolutePath() + File.separator +
                "IMG_" + timeStamp + ".png");
        return Uri.fromFile(mediaFile);
    }

}

