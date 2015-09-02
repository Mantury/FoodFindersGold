package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class starting_screen_activity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private double lat;
    private double lng;
    private GoogleApiClient mGoogleApiClient;
    private download download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("test");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen_layout);




        setUpMapIfNeeded();
        setUpMarker();




        download= new download();
        download.getDataForWeekday("https://maps.googleapis.com/maps/api/place/radarsearch/json?location=48.9984593454694,12.097473442554474&radius=5000&types=restaurants&key=AIzaSyBZELi4vKefXVU8I-TwFNOkgdA5wd5Q-mM");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d("Länge: " + lat + "\n" + "Breite: " + lng );
            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("You are here!!!"));
            CameraUpdate update= CameraUpdateFactory.newLatLng(new LatLng(lat, lng));
            mMap.moveCamera(update);
        }
        else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(48.9984593454694, 12.097473442554474)).title("You are here!!!"));
        }

    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
