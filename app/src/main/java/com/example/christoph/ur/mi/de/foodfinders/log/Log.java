package com.example.christoph.ur.mi.de.foodfinders.log;

public class Log {
    private static final boolean DEBUG = true;
    private static final String TAG = "FoodFinders";

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, msg);
        }
    }
}
