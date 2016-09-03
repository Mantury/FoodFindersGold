package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Layout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;

import java.util.List;

//This class lists all dishes from the cloud storage in a list.

public class dish_item_ArrayAdapter extends ArrayAdapter<dish_item> {

    private List<dish_item> dishes;
    private Context context;
    private OnDetailRequestedListener onDetailRequestedListener;

    public dish_item_ArrayAdapter(Context context, List<dish_item> dishes) {
        super(context, R.layout.dish_item, dishes);
        this.context = context;
        this.dishes = dishes;
    }

    //Displaying the "dish_items" in the list
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.dish_item, null);
        }
        TextView name = (TextView) v.findViewById(R.id.dish_name);
        ImageView image = (ImageView) v.findViewById(R.id.dish_image);
        RatingBar rating = (RatingBar) v.findViewById(R.id.dish_ratingbar);
        TextView vegan = (TextView) v.findViewById(R.id.dish_vegan);
        TextView gluten = (TextView) v.findViewById(R.id.dish_gluten);
        RelativeLayout linlayout = (RelativeLayout) v.findViewById(R.id.dish_layout);
        final dish_item dish = dishes.get(position);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dish_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailRequestedListener.onDetailRequested(dish.getDishId());
            }
        });
        name.setText(dish.getNameDish());
        String firepicture=dish.getImage();
        byte[] imageAsBytes = Base64.decode(firepicture.getBytes(), Base64.DEFAULT);
        image.setImageBitmap(
                BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
        );
        rating.setRating(dish.getRating());
        if (dish.getRating() >= 4) {
            linlayout.setBackgroundResource(R.color.green);

        }
        vegan.setText("Vegan: " + dish.getVegan());
        gluten.setText("Glutenfrei: " + dish.getGluten());
        return v;
    }

    public void setOnDetailRequestedListener(OnDetailRequestedListener onDetailRequestedListener) {
        this.onDetailRequestedListener = onDetailRequestedListener;
    }

    public interface OnDetailRequestedListener {
        public void onDetailRequested(String reviewid);
    }
}
