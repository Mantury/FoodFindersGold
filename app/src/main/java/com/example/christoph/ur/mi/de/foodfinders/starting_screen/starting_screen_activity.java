package com.example.christoph.ur.mi.de.foodfinders.starting_screen;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_detail.restaurant_detail_activity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import java.util.ArrayList;

//This activity is the starting screen of the app.


public class starting_screen_activity extends FragmentActivity implements download.OnRestaurantDataProviderListener {

    private GoogleMap mMap;
    private LatLng postion;
    private double lat;
    private double lng;
    private download data;
    private CameraUpdate update;
    private ArrayList<restaurantitemstart> restaurants = new ArrayList<>();
    private String parseClientKey = "PbusOboa70OtcFcYG72ILR7Xrxh86IZ5SDLOXdu7";
    private String parseApplicationKey = "qn09yetmFcN4h8TctK2xZhjrgzwXc1r5BC0QYgv9";
    private String placesearchurl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private String placesearchparameter1 ="&radius=";
    private String placesearchparameter2 = "&types=restaurant&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA&language=de";
    private int placesearchparameterradius = 1500;
    private Circle myCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("start");
        Parse.enableLocalDatastore(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen_layout);
        setUpMapIfNeeded();
        setUpMarker();
        data = new download();
        data.getlocationdata(placesearchurl + lat + "," + lng + placesearchparameter1 + placesearchparameterradius + placesearchparameter2);
        Parse.initialize(this, parseApplicationKey, parseClientKey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        data.setRestaurantDataProviderListener(this);
        updateButton();
        sekker();
        mMap.moveCamera(update);
    }

    private void updateButton() {
        Button update = (Button) findViewById(R.id.updatebutton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                setUpMarker();
                data.getlocationdata(placesearchurl + lat + "," + lng + placesearchparameter1 + placesearchparameterradius + placesearchparameter2);
            }
        });
    }

    private void sekker(){
        SeekBar seeker = (SeekBar) findViewById(R.id.starting_screen_seek_bar);
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seeker, int progress, boolean fromUser){

                CircleOptions circleOptions = new CircleOptions()
                        .center(new LatLng(lat, lng))   //set center
                        .radius(placesearchparameterradius * 1.2)   //set radius in meters
                        .fillColor(Color.TRANSPARENT)  //default
                        .strokeColor(R.color.green)
                        .strokeWidth(5);

                myCircle = mMap.addCircle(circleOptions);
                placesearchparameterradius = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMap.clear();
                setUpMarker();
                data.getlocationdata(placesearchurl + lat + "," + lng + placesearchparameter1 + placesearchparameterradius + placesearchparameter2);
            }
        });


    }

    //Sets up a marker at the users current position or uses the UR as default location
    private void setUpMarker() {
        String locService = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(locService);
        String provider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);
        Log.d(String.valueOf(location));
        if (location != null) {
            postion = new LatLng(location.getLatitude(), location.getLongitude());
            lat = location.getLatitude();
            lng = location.getLongitude();
            mMap.addMarker(new MarkerOptions().position(postion).title("Standort").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
            update = CameraUpdateFactory.newLatLng(postion);
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    LatLng position = marker.getPosition();
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(marker.getPosition()).title("gedraggde Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
                    update = CameraUpdateFactory.newLatLng(marker.getPosition());

                    data.getlocationdata("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + position.latitude + "," + position.longitude + "&radius=1500&types=restaurant&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA&language=de");
                }
            });
        } else {
            lat = 48.9984593454694;
            lng = 12.097473442554474;
            mMap.addMarker(new MarkerOptions().position(new LatLng(48.9984593454694, 12.097473442554474)).title("Kein aktueller Standort").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
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
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    //Sets up the markers for all found restaurants and colours them accordingly to their openninghours
    @Override
    public void onRestaurantDataReceived(ArrayList<restaurantitemstart> restaurants) {
        this.restaurants = restaurants;
        for (int i = 0; i < restaurants.size(); i++) {
            restaurantitemstart item = restaurants.get(i);
            postion = new LatLng(item.getLatitude(), item.getLongitude());
            String name = item.getName();
            String opennow;
            if (item.isOpenednow() == 0) {
                opennow = "Öffnungszeiten n.a.";
                mMap.addMarker(new MarkerOptions().position(postion).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet(opennow));
            } else {
                if (item.isOpenednow() == 1) {
                    opennow = "Offen";
                    mMap.addMarker(new MarkerOptions().position(postion).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(opennow));
                }
                if (item.isOpenednow() == 2) {
                    opennow = "Geschlossen";
                    mMap.addMarker(new MarkerOptions().position(postion).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(opennow));
                }
            }
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    openRestaurantDetail(getPlaceId(marker.getTitle()));
                }
            });
        }
    }

    public String getPlaceId(String title) {
        String place_id = "null";
        for (int i = 0; i < restaurants.size(); i++) {
            if (restaurants.get(i).getName().equals(title)) {
                place_id = restaurants.get(i).getPlace_id();
            }
        }
        return place_id;
    }

    private void openRestaurantDetail(String place_id) {
        Intent i = new Intent(starting_screen_activity.this, restaurant_detail_activity.class);
        i.putExtra("name", place_id);
        startActivity(i);
    }
}
