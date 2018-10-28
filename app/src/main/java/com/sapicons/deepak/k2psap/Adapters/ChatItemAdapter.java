package com.sapicons.deepak.k2psap.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sapicons.deepak.k2psap.Activities.AdPreviewActivity;
import com.sapicons.deepak.k2psap.Activities.ChatActivity;
import com.sapicons.deepak.k2psap.Objects.ChatItem;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.Objects.UserItem;
import com.sapicons.deepak.k2psap.Others.CalculateDistance;
import com.sapicons.deepak.k2psap.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Deepak Prasad on 01-10-2018.
 */

public class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.ChatItemViewHolder>{

    List<ChatItem> chatList;
    Context context;

    public class ChatItemViewHolder extends  RecyclerView.ViewHolder{
        public CircleImageView adImageView;
        public TextView adTitleTv, postUserTv, lastMsgTv, distanceAwayTv,priceTv;

        public ChatItemViewHolder(View view){
            super(view);
            adImageView = view.findViewById(R.id.item_chat_ad_pic);
            adTitleTv = view.findViewById(R.id.item_chat_ad_title);
            postUserTv = view.findViewById(R.id.item_chat_post_user_name);
            lastMsgTv = view.findViewById(R.id.item_chat_last_msg_time);
            distanceAwayTv = view.findViewById(R.id.item_chat_distance_away);

            priceTv = view.findViewById(R.id.item_chat_post_price);
        }
    }

    public ChatItemAdapter(@NonNull Context context, @NonNull List<ChatItem> objects) {
        this.chatList = objects;
        this.context = context;
    }


    @NonNull
    @Override
    public ChatItemViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false);

        return new ChatItemViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ChatItemAdapter.ChatItemViewHolder holder, final int position) {

        ChatItem chatItem = chatList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // set chat item views
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = (chatItem.getUserIdOne().equals(user.getEmail())) ? chatItem.getUserIdTwo(): chatItem.getUserIdOne();

        final String role = (userEmail.equals(chatItem.getUserIdOne())) ? " (Buyer)" : " (Seller)";

        // set ad data
        DocumentReference postRef = db.collection("ads").document(chatItem.getPostId());

        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot= task.getResult();
                    if(documentSnapshot.exists()){


                        Log.d("CIA","Documents Snapshot data: "+documentSnapshot);
                        PostItem postItem = documentSnapshot.toObject(PostItem.class);
                        holder.adTitleTv.setText(postItem.getTitle());
                        holder.priceTv.setText(AdPreviewActivity.setTradeType(postItem));
                        if(!postItem.getImgUrlOne().isEmpty())
                            Glide.with(context).load(postItem.getImgUrlOne()).into(holder.adImageView);
                        else
                            holder.adImageView.setImageResource(R.mipmap.ic_android_icon);

                        // set distance with latitude and longitude

                        holder.distanceAwayTv.setText(setDistance(postItem));

                    }else {
                        Log.d("CIA","No such doc exits");
                    }
                }else{
                    Log.d("CIA","get failed e : "+task.getException());
                }
            }
        });

        final String userName[]=new String[1];
        // set user name
        postRef = db.collection("users").document(userEmail);

        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot= task.getResult();
                    if(documentSnapshot.exists()){


                        Log.d("CIA","Documents Snapshot data: "+documentSnapshot);
                        UserItem userItem = documentSnapshot.toObject(UserItem.class);
                        userName[0] = userItem.getName();

                        holder.postUserTv.setText(userItem.getName()+role);
                    }else {
                        Log.d("CIA","No such doc exits");
                    }
                }else{
                    Log.d("CIA","get failed e : "+task.getException());
                }
            }
        });

        // set relative time
        holder.lastMsgTv.setText(getRelativeTime(chatItem.getLastMsgTimestamp()));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatItem chatItem = chatList.get(position);
                Intent intent = new Intent(context, ChatActivity.class);
                //Bundle bundle =
                intent.putExtra("selected_chat",chatItem);
                intent.putExtra("user_name",userName[0]);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public String getRelativeTime(long oldTime){

        long now = Calendar.getInstance().getTimeInMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(oldTime,now,DateUtils.MINUTE_IN_MILLIS);
        return ago.toString();
    }

    public String setDistance(PostItem postItem){
        CalculateDistance calculateDistance = new CalculateDistance(context);
        double distance = calculateDistance.distanceInKM(postItem.getLatitude(),postItem.getLongitude());
        DecimalFormat value = new DecimalFormat("#.#");

        return value.format(distance)+" kms away";


    }
}
