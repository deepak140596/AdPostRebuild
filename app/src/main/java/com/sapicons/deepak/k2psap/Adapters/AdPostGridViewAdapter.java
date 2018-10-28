package com.sapicons.deepak.k2psap.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sapicons.deepak.k2psap.Activities.AdPreviewActivity;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;

import java.util.List;

import es.dmoral.toasty.Toasty;
import io.reactivex.annotations.NonNull;

import static java.security.AccessController.getContext;

/**
 * Created by Deepak Prasad on 27-10-2018.
 */

public class AdPostGridViewAdapter extends BaseAdapter {

    String TAG = "AD_POST_GRID_ADAPTER";

    private Context context;
    List<PostItem> postItemList;
    int layoutResourceId;

    public AdPostGridViewAdapter(Context context, int resource, @NonNull List<PostItem> postItem){
        this.context = context;
        layoutResourceId = resource;
        postItemList = postItem;
    }

    public static class ViewHolder{
        TextView postTitle, postPrice;
        ImageView postImage;
        ImageButton favBtn, unfavBtn;
        int position;
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

        final ViewHolder holder;
        //View convertView;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_ad_grid, null, false);

            holder = new ViewHolder();
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder)convertView.getTag();
        }
            final PostItem postItem = postItemList.get(position);

        holder.postImage = convertView.findViewById(R.id.item_grid_ad_image_view);
        holder.postTitle = convertView.findViewById(R.id.item_grid_ad_title_tv);
        holder.postPrice = convertView.findViewById(R.id.item_grid_ad_price_tv);
        holder.favBtn = convertView.findViewById(R.id.item_ad_grid_favorite_iv);
        holder.unfavBtn = convertView.findViewById(R.id.item_ad_grid_unfavorite_iv);

            String imgUrl = postItem.getImgUrlOne();

            if( imgUrl != null && imgUrl.length() > 0 ){
                Glide.with(context).load(imgUrl).into(holder.postImage);
            }else{
                Glide.with(context).load(R.drawable.open_book).into(holder.postImage);
            }

        holder.postTitle.setText(postItem.getTitle());
        holder.postPrice.setText(AdPreviewActivity.setTradeType(postItem));

        if(postItem.getStatus().equals("closed")){
            holder.favBtn.setVisibility(View.GONE);
            holder.unfavBtn.setVisibility(View.GONE);
        }else{
            // set if the ad is favorited or not
            setFavButton(holder,postItem);
        }


        holder.favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPostToFavList(holder,postItem);
            }
        });

        holder.unfavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removePostFromFavList(holder,postItem);
            }
        });

        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AdPreviewActivity.class);
                //Bundle bundle =

                intent.putExtra("selected_post_item", postItem);
                context.startActivity(intent);
            }
        });

        return convertView;
    }





    public void setFavButton(final ViewHolder holder, PostItem postItem){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());

        favRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@android.support.annotation.NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){


                        holder.favBtn.setVisibility(View.GONE);
                        holder.unfavBtn.setVisibility(View.VISIBLE);
                    }else{
                        //no such doc exits
                        Log.d(TAG,"No such doc exits!");
                        holder.unfavBtn.setVisibility(View.GONE);
                        holder.favBtn.setVisibility(View.VISIBLE);
                    }
                }else{
                    Log.d(TAG,"Error getting document!");
                }
            }
        });
    }

    public void addPostToFavList(final ViewHolder holder, final PostItem postItem){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());


        favRef.set(postItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Ad Favorited!");
                Toasty.info(context,"Added to Favorites!").show();
                setFavButton(holder,postItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Log.d(TAG,"Failed to update favorites!   " + e);
            }
        });
    }

    public void removePostFromFavList(final ViewHolder holder, final PostItem postItem){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());

        favRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Ad removed from fav List.");
                Toasty.info(context,"Removed from Favorites!").show();
                setFavButton(holder,postItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@android.support.annotation.NonNull Exception e) {
                Log.d(TAG,"Failed to remove ad from fav list.   "+e);
            }
        });
    }
}
