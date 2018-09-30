package com.sapicons.deepak.k2psap.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 29-09-2018.
 */

public class AdPostAdapter extends ArrayAdapter<PostItem> {

    Context context;
    int layoutResourceId;
    List<PostItem> postItems;

    ProgressDialog progressDialog;


    public AdPostAdapter(@NonNull Context context, int resource, @NonNull List<PostItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.postItems = objects;
    }

    static class ViewHolder{

        TextView titleTv, descriptionTv, priceTv;
        ImageView coverIv;
        boolean isFavorited=false;
        ImageView favButton,unfavButton;
        int position;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if(convertView == null){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_ad_post, parent, false);

            holder = new ViewHolder();
            holder.titleTv = convertView.findViewById(R.id.item_ad_post_title_tv);
            holder.favButton = convertView.findViewById(R.id.item_ad_post_fav);
            holder.unfavButton = convertView.findViewById(R.id.item_ad_post_unfav);
            holder.descriptionTv = convertView.findViewById(R.id.item_ad_post_description_tv);
            holder.priceTv = convertView.findViewById(R.id.item_ad_post_price_tv);
            holder.coverIv = convertView.findViewById(R.id.item_ad_post_imageview);

            holder.position = position;

            convertView.setTag(holder);


        }else{
            holder = (ViewHolder)convertView.getTag();

        }

        PostItem postItem =getItem(position);

        holder.titleTv.setText(postItem.getTitle());
        holder.descriptionTv.setText(postItem.getDescription());
        holder.priceTv.setText(postItem.getPrice());

        if(!postItem.getImgUrlOne().isEmpty())
            Glide.with(context).load(postItem.getImgUrlOne()).into(holder.coverIv);
        else
            holder.coverIv.setImageResource(R.mipmap.ic_android_icon);

        /*if(holder.isFavorited) {
            holder.unfavButton.setVisibility(View.VISIBLE);
            holder.favButton.setVisibility(View.GONE);
        }
        else{
            holder.favButton.setVisibility(View.VISIBLE);
            holder.unfavButton.setVisibility(View.GONE);
        }

       holder.favButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               holder.isFavorited = true;
               holder.favButton.setVisibility(View.GONE);
               holder.unfavButton.setVisibility(View.VISIBLE);

               //add post to favorite list
           }
       });

        holder.unfavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.isFavorited = false;
                holder.unfavButton.setVisibility(View.GONE);
                holder.favButton.setVisibility(View.VISIBLE);

                //remove post from favorite list
            }
        });*/

        return  convertView;
    }
}
