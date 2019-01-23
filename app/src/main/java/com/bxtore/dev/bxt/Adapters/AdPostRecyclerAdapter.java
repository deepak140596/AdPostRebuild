package com.bxtore.dev.bxt.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bxtore.dev.bxt.Objects.PostItem;
import com.bxtore.dev.bxt.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 29-09-2018.
 */

public class AdPostRecyclerAdapter extends RecyclerView.Adapter<AdPostRecyclerAdapter.AdPostViewHolder> {


    List<PostItem> postList;
    Context context;

    public class AdPostViewHolder extends  RecyclerView.ViewHolder{
        public TextView titleTv, descriptionTv, priceTv;
        public ImageView coverIv;

        public boolean isFavorited = false;

        //public MaterialFavoriteButton favButton;
        //int position;

        AdPostViewHolder(View view){
            super(view);
            titleTv = view.findViewById(R.id.item_ad_post_title_tv);
            //favButton = view.findViewById(R.id.item_ad_post_fav);
            descriptionTv = view.findViewById(R.id.item_ad_post_description_tv);
            priceTv = view.findViewById(R.id.item_ad_post_price_tv);
            coverIv = view.findViewById(R.id.item_ad_post_imageview);

        }
    }

    public AdPostRecyclerAdapter(Context context,List<PostItem> postList){
        this.postList = postList;
        this.context = context;
    }




    @Override
    public AdPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ad_post,parent,false);
        return new AdPostViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdPostViewHolder holder, int position) {

        PostItem postItem = postList.get(position);

        holder.titleTv.setText(postItem.getTitle());
        holder.descriptionTv.setText(postItem.getDescription());
        holder.priceTv.setText(postItem.getPrice());

        if(!postItem.getImgUrlOne().isEmpty())
            Glide.with(context).load(postItem.getImgUrlOne()).into(holder.coverIv);
        else
            holder.coverIv.setImageResource(R.mipmap.ic_android_icon);





    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
