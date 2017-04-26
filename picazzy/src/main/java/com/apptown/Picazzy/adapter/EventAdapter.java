package com.apptown.Picazzy.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apptown.Picazzy.R;
import com.apptown.Picazzy.model.EventInfo;
import com.apptown.Picazzy.model.PicazzyEvent;
import com.apptown.Picazzy.util.AndroidUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by ${="Ashish"} on 15/1/17.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder> {

    private Context context;
    private EventInfo eventInfo;
    private int mSelectedImageItemPosition=-1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public ImageView filter;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            filter=(ImageView)view.findViewById(R.id.filter);
        }
    }

    public EventAdapter(Context context, EventInfo eventInfo) {
        this.context=context;
        this.eventInfo=eventInfo;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_card_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.title.setText(eventInfo.getPicazzyEvent().get(position).getEventName());
        Glide.with(context).load(eventInfo.getPicazzyEvent().get(position).getImageThumb()).into(holder.thumbnail);
        if(mSelectedImageItemPosition == position){
            AndroidUtil.printLog("TRUE");
            holder.filter.setVisibility(View.VISIBLE);
        }else{
            holder.filter.setVisibility(View.INVISIBLE);
        }
        Glide.with(context)
                    .load(eventInfo.getPicazzyEvent().get(position).getImagePotr())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if(resource!=null){
                                AndroidUtil.printLog("POTR Bitmap ");
                                eventInfo.getPicazzyEvent().get(position).setImagePotrBitmap(resource);
                            }

                        }
                    });

            Glide.with(context)
                    .load(eventInfo.getPicazzyEvent().get(position).getImageLand())
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if(resource!=null){
                                AndroidUtil.printLog("LAND Bitmap ");
                                eventInfo.getPicazzyEvent().get(position).setImageLandBitmap(resource);
                            }

                        }
                    });

    }

    @Override
    public int getItemCount() {
        return eventInfo.getPicazzyEvent().size();
    }
    public PicazzyEvent getItem(int pos){

        return eventInfo.getPicazzyEvent().get(pos);
    }
    public void notifyDateChanged(int pos){

        mSelectedImageItemPosition=pos;
        notifyDataSetChanged();
    }
}
