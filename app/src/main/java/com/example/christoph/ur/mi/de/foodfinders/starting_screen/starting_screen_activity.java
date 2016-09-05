package com.example.christoph.ur.mi.de.foodfinders.starting_screen;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;
import com.example.christoph.ur.mi.de.foodfinders.restaurant_detail.restaurant_detail_activity;
import com.example.christoph.ur.mi.de.foodfinders.restaurants.restaurant;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Map;

//This activity is the starting screen of the app.


public class starting_screen_activity extends FragmentActivity implements download.OnRestaurantDataProviderListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng position;
    private double latUr=48.9984593454694;
    private double lngUr=12.097473442554474 ;
    private download data;
    private CameraUpdate update;
    private ArrayList<restaurant> restaurants = new ArrayList<>();
    private String placesearchurl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    private String placesearchparameter1 ="&radius=";
    private String placesearchparameter2 = "&types=restaurant&key=AIzaSyBWuaV6fCf_Ha8ITK4p8oRKHS1X5-mNIaA&language=de";
    private int placesearchparameterradius = 1500;
    private Circle myCircle;
    private boolean drag=false;
    private String yourLocation="Standort";
    private String draggedLocation="Eigene Position";
    private String defaultLocation="Kein aktueller Standort";

    @Override
    public void onMapReady(GoogleMap map) {
        //TODO höhe einstellen?
        mMap = map;
        setUpMarker();
        draggablePosition();
        setOnlongPoschange();
        sekker();
        updateButton();
        setUpData();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_screen_layout);
        if(checkInternetConn()) {
           // LogInFirebase(); // test für den login
            setUpMapIfNeeded();
        }
    }
    private void LogInFirebase() {
        Log.d("firebaselogin","start login");
        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://foodfindersgold.firebaseio.com");
        ref.createUser("bobtony@firebase.com", "correcthorsebatterystaple", new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Log.d("firebaslogin", "success"+result.get("uid"));
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                Log.d("firebaslogin", "fehler"+firebaseError.toString());
                // there was an error
            }
        });
    }


    private boolean checkInternetConn() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            Toast.makeText(starting_screen_activity.this, "No Internet", Toast.LENGTH_SHORT).show();
            finish();
           return false;
        }else {
            return true;
        }
    }

    private void setupDrawer(ArrayList<restaurant> rests){
        //TODO favoriten speichern und die liste nehmen!
        final DrawerLayout FavDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ListView FavList = (ListView) findViewById(R.id.left_drawer);

        Favourites_ArrayAdapter aa = new Favourites_ArrayAdapter(rests,this);
        FavList.setAdapter(aa);
        FavList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO lade detail screen it Restaurant
                restaurant clickedRest = (restaurant) FavList.getItemAtPosition(position);
                FavDrawer.closeDrawer(FavList);
                openRestaurantDetail(clickedRest.getPlace_id());
            }
        });

     //   mDrawerList.setItemChecked(position, true);
     //   mDrawerLayout.closeDrawer(mDrawerList);

     //   Context context = getApplicationContext();
     //   CharSequence text = "Hello toast!";
     //   int duration = Toast.LENGTH_SHORT;
     //   Toast toast = Toast.makeText(context, text, duration);
     //   toast.show();

    }

    private void setUpData() {
        data = new download();
        getData();
        data.setRestaurantDataProviderListener(this);
    }

    private void getData(){
        data.getlocationdata(placesearchurl + position.latitude + "," + position.longitude + placesearchparameter1 + placesearchparameterradius + placesearchparameter2);
        Toast.makeText(starting_screen_activity.this, "Loading...", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateButton();
    }

    private void draggablePosition() {
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mMap.clear();
                position = marker.getPosition();
                drag = true;
                setUpMarker();
                getData();
            }
        });
    }

    private void updateButton() {
        Button update = (Button) findViewById(R.id.updatebutton);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                drag = false;
                setUpMarker();
                getData();
            }
        });
    }

    private void setOnlongPoschange(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.clear();
                position = (latLng);
                drag = true;
                setUpMarker();
                getData();
            }
        });
    }

    private void sekker(){

        final SeekBar seeker = (SeekBar) findViewById(R.id.starting_screen_seek_bar);
        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seeker, int progress, boolean fromUser) {


                CircleOptions circleOptions = new CircleOptions()
                        .center(position)   //set center
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
                getData();            }
        });


    }

    //Sets up a marker at the users current position or uses the UR as default location or the dragged position
    private void setUpMarker() {
        String locService = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(locService);
        String provider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(provider);

        if(drag){
            mMap.addMarker(new MarkerOptions().position(position).title(draggedLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
            update = CameraUpdateFactory.newLatLng(position);
            mMap.moveCamera(update);

        }else {

            if (location != null) {

                position = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(position).title(yourLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
                update = CameraUpdateFactory.newLatLng(position);;
                mMap.moveCamera(update);

            } else {
                LatLng defaultUr = new LatLng(latUr, lngUr);
                position=defaultUr;
                mMap.addMarker(new MarkerOptions().position(defaultUr).title(defaultLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).draggable(true));
                update = CameraUpdateFactory.newLatLng(defaultUr);
                mMap.moveCamera(update);
            }
        }
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment supMapFrag = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            supMapFrag.getMapAsync(this);
        }
    }

    //Sets up the markers for all found restaurants and colours them accordingly to their openninghours
    @Override
    public void onRestaurantDataReceived(ArrayList<restaurant> restaurants) {
        this.restaurants = restaurants;
        setupDrawer(restaurants);

        if(restaurants==null){

        }else {
            for (int i = 0; i < restaurants.size(); i++) {
                restaurant res = restaurants.get(i);
                LatLng positionitem = new LatLng(res.getLatitude(), res.getLongitude());
                String name = res.getName();
                String opennow;
                if (res.getOpen() == 0) {
                    opennow = "Öffnungszeiten n.a.";
                    mMap.addMarker(new MarkerOptions().position(positionitem).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).snippet(opennow));
                } else {
                    if (res.getOpen() == 1) {
                        opennow = "Offen";
                        mMap.addMarker(new MarkerOptions().position(positionitem).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(opennow));
                    }
                    if (res.getOpen() == 2) {
                        opennow = "Geschlossen";
                        mMap.addMarker(new MarkerOptions().position(positionitem).title(name).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(opennow));
                    }
                }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.d(yourLocation+marker.getTitle());

                        if(yourLocation.equals(marker.getTitle())||draggedLocation.equals(marker.getTitle())||defaultLocation.equals(marker.getTitle())){

                        }else{
                            openRestaurantDetail(getPlaceId(marker.getTitle()));
                        }

                    }
                });
            }
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
