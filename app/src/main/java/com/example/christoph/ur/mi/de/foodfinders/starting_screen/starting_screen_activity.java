package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;

import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;


public class starting_screen_activity extends FragmentActivity  {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng postion;
    private double lat;
    private double lng;



    private download data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen_layout);
        setUpMapIfNeeded();
        setUpMarker();
        data=new download();
        data.getlocationdata("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=49.010006,12.093229&radius=500&types=restaurant&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA");
        Log.d(String.valueOf(lat)+"   "+String.valueOf(lng));

        data.getlocationdata("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+"," +lng+ "&radius=500&types=food&name=cruise&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA");

    }


    private void setUpMarker() {
        //Setzt den PersonenMarker
        String locService = Context.LOCATION_SERVICE;
        LocationManager locationManager= (LocationManager)getSystemService(locService);
        String provider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);

        Log.d(String.valueOf(location));

        String locationData;

        if (location != null) {
            postion= new LatLng(location.getLatitude(), location.getLongitude());
            lat=location.getLatitude();
            lng=location.getLongitude();
            Log.d("Länge: " + location.getLatitude() + "\n" + "Breite: " + location.getLongitude());
            Log.d(String.valueOf(postion));
            mMap.addMarker(new MarkerOptions().position(postion).title("You are here!!!"));
            CameraUpdate update= CameraUpdateFactory.newLatLng(postion);
            mMap.moveCamera(update);

        }
        else {
            lat=48.9984593454694;
            lng=12.097473442554474;
            mMap.addMarker(new MarkerOptions().position(new LatLng(48.9984593454694, 12.097473442554474)).title("You are here!!!"));
        }

    }




    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

}
