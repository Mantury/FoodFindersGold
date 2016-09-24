package com.example.christoph.ur.mi.de.foodfinders.starting_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.restaurants.restaurant;

import java.util.List;


public class Favourites_ArrayAdapter extends ArrayAdapter<restaurant> {

    private List<restaurant> restaurants;
    private Context context;

    public Favourites_ArrayAdapter(List<restaurant> restaurants, Context context) {
        super(context, R.layout.favourites_item, restaurants);
        this.restaurants = restaurants;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.favourites_item, null);
        }
        restaurant Rest = restaurants.get(position);
        if (Rest != null) {
            TextView restName = (TextView) v.findViewById(R.id.restListName);
            TextView restCity = (TextView) v.findViewById(R.id.restListCity);
            restName.setText(Rest.getName());
            restCity.setText(Rest.getAddress());
        }
        return v;
    }
}
