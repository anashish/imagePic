package com.apptown.Picazzy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Password on 18-08-2016.
 */
public class Adapter  extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<String> title;
    private List<Drawable> effect_image;

    public Adapter(Activity context, ArrayList<String> title, List<Drawable> effect_image) {

        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.effect_image = effect_image;

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
            holder.image_layer=(ImageView)view.findViewById(R.id.filter);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        // SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String value= prefs.getString("Title", "");

        holder.tvSpecies.setText(title.get(position));
        holder.imgThumbnail.setImageDrawable(effect_image.get(position));

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