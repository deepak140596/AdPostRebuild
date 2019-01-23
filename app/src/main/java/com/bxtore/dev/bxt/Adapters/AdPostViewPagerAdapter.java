package com.bxtore.dev.bxt.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bxtore.dev.bxt.Objects.PostItem;
import com.bxtore.dev.bxt.Others.CalculateDistance;
import com.bxtore.dev.bxt.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Deepak Prasad on 29-09-2018.
 */

public class AdPostViewPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<PostItem> list;

    public AdPostViewPagerAdapter(Context context, List<PostItem> list){
        this.context = context;
        this.list = list;
        this.layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = this.layoutInflater.inflate(R.layout.item_post_view_pager,container,false);

        ImageView postImg = view.findViewById(R.id.item_post_vp_imageview);
        TextView title = view.findViewById(R.id.item_post_vp_title_tv);
        TextView price = view.findViewById(R.id.item_post_vp_price_tv);
        TextView location = view.findViewById(R.id.item_post_vp_location);
        TextView date = view.findViewById(R.id.item_post_vp_date);

        PostItem postItem = this.list.get(position);

        if(!postItem.getImgUrlOne().isEmpty()){
            Glide.with(this.context).load(postItem.getImgUrlOne()).into(postImg);
        }
        title.setText(postItem.getTitle());
        price.setText(postItem.getPrice());
        location.setText(setDistance(postItem));

        //set up date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM");
        date.setText(dateFormatter.format(Long.parseLong(postItem.getPostId())));





        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toasty.info(context,"Clicked on: "+position).show();
            }
        });

        return view;
    }

    public String setDistance(PostItem postItem){
        CalculateDistance calculateDistance = new CalculateDistance(context);
        double distance = calculateDistance.distanceInKM(postItem.getLatitude(),postItem.getLongitude());
        DecimalFormat value = new DecimalFormat("#.#");

        return value.format(distance)+" kms away";


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
