package com.bxtore.dev.bxt.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;
import com.bxtore.dev.bxt.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 28-09-2018.
 */

public class PhotoPreviewPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Uri> list;

    public PhotoPreviewPagerAdapter(Context context, List<Uri> list){
        this.context = context;
        this.list = list;
        this.layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = this.layoutInflater.inflate(R.layout.item_photo_preview_view_pager,container,false);

        //ImageView previewImg = view.findViewById(R.id.item_view_pager_image_view);
        ZoomageView previewImg = view.findViewById(R.id.item_view_pager_image_view);

        Uri imgUri = this.list.get(position);

        if(imgUri!=null){
            Glide.with(this.context).load(imgUri).into(previewImg);
        }
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==((View)object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


}
