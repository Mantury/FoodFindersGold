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
import android.widget.Button;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;

import com.example.christoph.ur.mi.de.foodfinders.restaurant_detail.restaurant_detail_activity;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class starting_screen_activity extends FragmentActivity implements download.OnRestaurantDataProviderListener {


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng postion;
    private double lat;
    private double lng;
    private download data;
    private CameraUpdate update;
    private ArrayList<restaurantitemstart> restaurants = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        Parse.enableLocalDatastore(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen_layout);

        Parse.initialize(this, "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9", "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7");
    }

   @Override
   protected void onResume(){
       super.onResume();
       setUpMapIfNeeded();
       setUpMarker();
       data = new download();
       //&language=de/german???
       data.getlocationdata("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=1000&language=German&types=restaurant&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA");
       data.setRestaurantDataProviderListener(this);
       updateButton();
       mMap.moveCamera(update);


    }

    private void updateButton() {
        Button update=(Button) findViewById(R.id.updatebutton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                setUpMarker();
                data.getlocationdata("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng + "&radius=1500&types=restaurant&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA");

            }
        });
    }


    private void setUpMarker() {
        //Setzt den PersonenMarker
        String locService = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(locService);
        String provider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);

        Log.d(String.valueOf(location));

        String locationData;

        if (location != null) {
            postion = new LatLng(location.getLatitude(), location.getLongitude());
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d(String.valueOf(postion));
            mMap.addMarker(new MarkerOptions().position(postion).title("Standort").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
             update = CameraUpdateFactory.newLatLng(postion);


        } else {
            lat = 48.9984593454694;
            lng = 12.097473442554474;
            mMap.addMarker(new MarkerOptions().position(new LatLng(48.9984593454694, 12.097473442554474)).title("Kein aktueller Standort").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            LatLng latlng = new LatLng(lat, lng);
            update = CameraUpdateFactory.newLatLng(latlng);
        }

    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #()} once when {@link #mMap} is not null.
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
        }
    }


    @Override
    public void onRestaurantDataReceived(ArrayList<restaurantitemstart> restaurants) {
        //umliegende Restaurants werden in Marker mit verschiedenen Farben umgewandelt!
        this.restaurants = restaurants;

        for (int i = 0; i < restaurants.size(); i++) {
            restaurantitemstart item = restaurants.get(i);
            postion = new LatLng(item.getLatitude(), item.getLongitude());
            String name = item.getName();
            String opennow;
            if (item.isOpenednow() == 0) {
                opennow = "Öffnungszeiten n.A.";
                mMap.addMarker(new MarkerOptions().position(postion).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet(opennow));

            }else {
                if (item.isOpenednow() == 1) {
                    opennow = "Offen";
                    mMap.addMarker(new MarkerOptions().position(postion).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(opennow));

                }
                if (item.isOpenednow() == 2) {
                    opennow = "Geschlossen";
                    mMap.addMarker(new MarkerOptions().position(postion).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(opennow));

                }
            }
                //setzt Onclicklistener
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        openRestaurantDetail(getPlaceId(marker.getTitle()));
                    }
                });

            }
        }


    public String getPlaceId(String title) {
        //sucht mit Namen nach Restaurant und holt sich die place_id
        String place_id = "null";
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getName().equals(title)) {
                place_id = restaurants.get(i).getPlace_id();
            }
        }
        return place_id;
    }


    private void openRestaurantDetail(String place_id) {
        //startet Restaurantdetailactivity und übergibt die place_id;
        Intent i = new Intent(starting_screen_activity.this, restaurant_detail_activity.class);
        i.putExtra("name", place_id);
        startActivity(i);

    }

}
