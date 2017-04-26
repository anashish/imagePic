package com.apptown.Picazzy;


import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Created by Enter Password on 16-06-2016.
 */
public class LoaderFragment extends Fragment {
    ImageView img;
    AnimationDrawable animationDrawable;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.loadermain,container,false);
        img= (ImageView) v.findViewById(R.id.animation);
        img.setBackgroundResource(R.drawable.loading_animation);
        animationDrawable= (AnimationDrawable) img.getBackground();
        animationDrawable.start();


        return v;
    }

}
