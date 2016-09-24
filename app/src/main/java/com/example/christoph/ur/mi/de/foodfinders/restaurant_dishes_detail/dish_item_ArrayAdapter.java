package com.example.christoph.ur.mi.de.foodfinders.restaurant_dishes_detail;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.christoph.ur.mi.de.foodfinders.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        TextView author = (TextView) v.findViewById(R.id.dish_author);
        RatingBar rating = (RatingBar) v.findViewById(R.id.dish_ratingbar);
        TextView vegan = (TextView) v.findViewById(R.id.dish_vegan);
        TextView gluten = (TextView) v.findViewById(R.id.dish_gluten);
        final dish_item dish = dishes.get(position);
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dish_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailRequestedListener.onDetailRequested(dish.getDishId());
            }
        });
        name.setText(dish.getNameDish());
        String firepicture = dish.getImage();
        byte[] imageAsBytes = Base64.decode(firepicture.getBytes(), Base64.DEFAULT);
        image.setImageBitmap(
                BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
        );
        author.setText(context.getString(R.string.byColon_ger) + dish.getAuthor());
        rating.setRating(dish.getRating());
        if (dish.getRating() >= 4) {
            layout.setBackgroundResource(R.color.green);

        }

        vegan.setText(context.getString(R.string.veganColon_ger) + dish.getVegan());
        gluten.setText(context.getString(R.string.glutenfreeColon_ger) + dish.getGluten());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null) {

            if (user.getUid().equals(dish.getAuthorID())) {
                layout.setBackgroundResource(R.color.bright_green);
                layout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onDetailRequestedListener.onDishDeletedListener(dish.getDishId(), user.getUid());
                        return true;
                    }
                });
            }
        }
        return v;
    }

    public void setOnDetailRequestedListener(OnDetailRequestedListener onDetailRequestedListener) {
        this.onDetailRequestedListener = onDetailRequestedListener;
    }

    public interface OnDetailRequestedListener {
        public void onDetailRequested(String reviewid);

        public void onDishDeletedListener(String dishId, String UID);
    }


}
