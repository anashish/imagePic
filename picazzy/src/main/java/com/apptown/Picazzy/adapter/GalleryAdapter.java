package com.apptown.Picazzy.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apptown.Picazzy.R;
import com.apptown.Picazzy.model.PhotoModel;
import com.apptown.Picazzy.util.AndroidUtil;
import com.apptown.Picazzy.view.TedSquareImageView;
import com.bumptech.glide.Glide;

import java.util.List;

import static com.apptown.Picazzy.R.id.image_src;

/**
 * Created by ${="Ashish"} on 17/12/16.
 */

public class GalleryAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PhotoModel> images;
    private Context mContext;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private int mSelectedImageItemPosition=0;
    private boolean mImageRotate;

    class VHHeader extends RecyclerView.ViewHolder {
        ImageView imgView;

        public VHHeader(View itemView) {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(image_src);
        }

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        public View root;
        public TedSquareImageView iv_thumbnail;

        public MyViewHolder(View view) {
            super(view);
            root = (View) view.findViewById(R.id.root);
            iv_thumbnail = (TedSquareImageView) view.findViewById(R.id.iv_thumbnail);
        }
    }


    public GalleryAdapter(Context context, List<PhotoModel> images) {
        mContext = context;
        this.images = images;
    }
    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_row, parent, false);

        View ViewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_row, parent, false);
        return new MyViewHolder(ViewItem);

/*
        if (viewType == TYPE_ITEM) {
            View ViewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_item_row, parent, false);
            return new MyViewHolder(ViewItem);
        } else if (viewType == TYPE_HEADER) {
            View ViewHeader= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_home_header, parent, false);
            return new VHHeader(ViewHeader);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");*/

    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof MyViewHolder) {
            PhotoModel image = images.get(position);
            MyViewHolder myViewHolder=(MyViewHolder)holder;

            AndroidUtil.printLog("postionValue true "+mSelectedImageItemPosition);

            if(mSelectedImageItemPosition == position){
                myViewHolder.root.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.root.setVisibility(View.INVISIBLE);
            }

            Glide.with(mContext)
                    .load(image.getImageUri())
                    .thumbnail(0.1f)
                    .dontAnimate()
                    .centerCrop()
                    .placeholder(R.drawable.img_logo_white)
                    .error(R.drawable.img_logo_white)
                    .into(myViewHolder.iv_thumbnail);

        } /*else if (holder instanceof VHHeader) {
            PhotoModel image = images.get(mSelectedImageItemPosition);
            VHHeader header=(VHHeader)holder;
            Glide.with(mContext)
                    .load(image.getImageUri())
                    .thumbnail(0.1f)
                    .dontAnimate()
                    .centerCrop()
                    .placeholder(R.drawable.img_logo_white)
                    .error(R.drawable.img_logo_white)
                    .into(header.imgView);
        }*/



    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public PhotoModel getItem(int pos){
        return images.get(pos);
    }
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public void notifyDateChanged(int pos){

        mSelectedImageItemPosition=pos;
        notifyDataSetChanged();
    }


}
