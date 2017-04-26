package com.apptown.Picazzy.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apptown.Picazzy.R;
import com.apptown.Picazzy.model.PhotoModel;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by ${="Ashish"} on 8/2/17.
 */

public class SwipeDeckAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<PhotoModel> list;
    public SwipeDeckAdapter(Context context,List<PhotoModel> list) {
        mContext = context;
        this.list=list;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((CardView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.card_layout_image_row, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.card_image_layout);
        Glide.with(mContext)
                .load(list.get(position).getImageUri())
                .thumbnail(0.1f)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.img_logo_white)
                .error(R.drawable.img_logo_white)
                .into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((CardView) object);
    }
}
