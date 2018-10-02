package com.sapicons.deepak.k2psap.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
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

import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * Created by Deepak Prasad on 29-09-2018.
 */

public class AdPostAdapter extends ArrayAdapter<PostItem> {

    String TAG = "AD_POST_ADAPTER";
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
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

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

        final PostItem postItem =getItem(position);

        holder.titleTv.setText(postItem.getTitle());
        holder.descriptionTv.setText(postItem.getDescription());
        holder.priceTv.setText(postItem.getPrice());

        if(!postItem.getImgUrlOne().isEmpty())
            Glide.with(context).load(postItem.getImgUrlOne()).into(holder.coverIv);
        else
            holder.coverIv.setImageResource(R.mipmap.ic_android_icon);



        // set if the ad is favorited or not
        setFavButton(holder,postItem);

       holder.favButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               //add post to favorite list
               addPostToFavList(holder,postItem);
           }
       });

        holder.unfavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //remove post from favorite list
                removePostFromFavList(holder,postItem);
            }
        });

        return  convertView;
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
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){


                        holder.favButton.setVisibility(View.GONE);
                        holder.unfavButton.setVisibility(View.VISIBLE);
                    }else{
                        //no such doc exits
                        Log.d(TAG,"No such doc exits!");
                        holder.unfavButton.setVisibility(View.GONE);
                        holder.favButton.setVisibility(View.VISIBLE);
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
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to update favorites!   " + e);
            }
        });
    }

    public void removePostFromFavList(final ViewHolder holder,final PostItem postItem){
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
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to remove ad from fav list.   "+e);
            }
        });
    }

}
