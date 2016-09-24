package com.example.christoph.ur.mi.de.foodfinders.restaurant_detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.christoph.ur.mi.de.foodfinders.R;

import java.util.ArrayList;


public class adaper_viewpager extends PagerAdapter {

    private LayoutInflater inflater;
    private Context ctx;
    private ArrayList<Bitmap> images;

    public adaper_viewpager(Context ctx, ArrayList<Bitmap> images) {
        this.ctx = ctx;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.swipe_layout_imageview);
        imageView.setImageBitmap(images.get(position));
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }
}
