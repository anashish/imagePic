package com.apptown.Picazzy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apptown.Picazzy.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

/**
 * Created by Password on 18-08-2016.
 */
public class NewFilterAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    ArrayList<String> title;
    ArrayList<String> effect_code;
    ImageView image_layer;
    ArrayList<Integer> effect_image;
    SharedPreferences sharedPreferences;



    public NewFilterAdapter(Context context, ArrayList<String> title, ArrayList<Integer> effect_image, ArrayList<String> effect_code) {

        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.effect_image = effect_image;
        this.effect_code=effect_code;
        // this.image_layer=image_layer;
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return title.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        final ViewHolder holder;

        if (convertView == null) {


            view = mInflater.inflate(R.layout.effect_elements, parent, false);

            holder = new ViewHolder();
            holder.imgThumbnail = (ImageView) view.findViewById(R.id.img_thumbnail);
            holder.tvSpecies = (TextView) view.findViewById(R.id.tv_species);
           // holder.image_layer=(ImageView)view.findViewById(R.id.filter);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        // SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value= prefs.getString("Title", "");

        holder.tvSpecies.setText(title.get(position));

        Glide.with(context).load(effect_image.get(position)).asBitmap().centerCrop().into(new BitmapImageViewTarget( holder.imgThumbnail) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                holder.imgThumbnail.setImageDrawable(circularBitmapDrawable);
            }
        });


        /*Glide.with(context)
                .load(image.getImageUri())
                .thumbnail(0.1f)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.img_logo_white)
                .error(R.drawable.img_logo_white)
                .into(holder.iv_thumbnail);


        holder.imgThumbnail.setImageResource(effect_image.get(position));*/
        // holder.image_layer.setVisibility(image_layer.getVisibility());
        if(value!=null) {
            if (value == title.get(position))

            {
                holder.image_layer.setVisibility(View.VISIBLE);
            } else {
                holder.image_layer.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    public class ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvSpecies;
        public ImageView image_layer;
    }
}