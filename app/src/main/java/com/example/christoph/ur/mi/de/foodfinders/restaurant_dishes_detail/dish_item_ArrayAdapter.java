package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.example.christoph.ur.mi.de.foodfinders.log.Log;

import java.util.List;

/**
 * Created by juli on 22.09.15.
 */
public class dish_item_ArrayAdapter extends ArrayAdapter<dish_item> {

    private List<dish_item> dishes;
    private Context context;
    private OnDetailRequestedListener onDetailRequestedListener;

    public dish_item_ArrayAdapter(Context context, List<dish_item> dishes) {
        super(context, R.layout.dish_item,dishes);
        this.context = context;
        this.dishes=dishes;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.dish_item, null);
        }

        // init Layout resourcen
        TextView name=(TextView) v.findViewById(R.id.dish_name);
        ImageView image=(ImageView) v.findViewById(R.id.dish_image);
        RatingBar rating=(RatingBar) v.findViewById(R.id.dish_ratingbar);
        TextView vegan=(TextView) v.findViewById(R.id.dish_vegan);
        TextView gluten=(TextView) v.findViewById(R.id.dish_gluten);



        final dish_item dish = dishes.get(position);
        //Auf ganzes Layout on clickListener setzen!
        RelativeLayout layout= (RelativeLayout) v.findViewById(R.id.dish_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailRequestedListener.onDetailRequested(dish.getParse_id());
            }
        });

        //Layoutsachen mit Daten befüllen!!
        name.setText(dish.getNameDish());
        image.setImageBitmap(dish.getImage());
        rating.setRating(dish.getRating());
        vegan.setText(dish.getVegan());
        gluten.setText(dish.getGluten());


        return v;
    }

    public void setOnDetailRequestedListener(OnDetailRequestedListener onDetailRequestedListener) {
        this.onDetailRequestedListener = onDetailRequestedListener;
    }

    public interface OnDetailRequestedListener {
        public void onDetailRequested(String parse_id);}
}
