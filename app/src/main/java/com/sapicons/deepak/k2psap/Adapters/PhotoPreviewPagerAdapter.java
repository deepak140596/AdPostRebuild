package com.sapicons.deepak.k2psap.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sapicons.deepak.k2psap.R;

import java.util.List;

import es.dmoral.toasty.Toasty;

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

        ImageView previewImg = view.findViewById(R.id.item_view_pager_image_view);
        Uri imgUri = this.list.get(position);

        if(imgUri!=null){
            Glide.with(this.context).load(imgUri).into(previewImg);
        }
        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.info(context,"Clicked on: "+position).show();
            }
        });

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
