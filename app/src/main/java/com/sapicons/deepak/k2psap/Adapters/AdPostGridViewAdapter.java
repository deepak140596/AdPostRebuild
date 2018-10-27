package com.sapicons.deepak.k2psap.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sapicons.deepak.k2psap.Activities.AdPreviewActivity;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;

import java.util.List;

import io.reactivex.annotations.NonNull;

import static java.security.AccessController.getContext;

/**
 * Created by Deepak Prasad on 27-10-2018.
 */

public class AdPostGridViewAdapter extends BaseAdapter {

    private Context context;
    List<PostItem> postItemList;
    int layoutResourceId;

    public AdPostGridViewAdapter(Context context, int resource, @NonNull List<PostItem> postItem){
        this.context = context;
        layoutResourceId = resource;
        postItemList = postItem;
    }

    @Override
    public int getCount() {
        return postItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View convertView;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_ad_grid, null, false);

        }

            ImageView postImage = convertView.findViewById(R.id.item_grid_ad_image_view);
            TextView postTitle = convertView.findViewById(R.id.item_grid_ad_title_tv);
            TextView postPrice = convertView.findViewById(R.id.item_grid_ad_price_tv);

            String imgUrl = postItemList.get(position).getImgUrlOne();

            if( imgUrl != null && imgUrl.length() > 0 ){
                Glide.with(context).load(imgUrl).into(postImage);
            }else{
                Glide.with(context).load(R.drawable.open_book).into(postImage);
            }

            postTitle.setText(postItemList.get(position).getTitle());
            postPrice.setText(AdPreviewActivity.setTradeType(postItemList.get(position)));

        return convertView;
    }
}
