package com.sapicons.deepak.k2psap.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.k2psap.Adapters.AdPostAdapter;
import com.sapicons.deepak.k2psap.Adapters.PhotoPreviewPagerAdapter;
import com.sapicons.deepak.k2psap.Objects.CategoryItem;
import com.sapicons.deepak.k2psap.Objects.ChatItem;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.Others.CalculateDistance;
import com.sapicons.deepak.k2psap.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

public class AdPreviewActivity extends AppCompatActivity {
    String TAG = "AD_PREVIEW_ACTIVITY";

    PostItem postItem;
    ViewPager viewPager;
    ImageView emptyViewPagerIv;
    TextView titleTv, datePostedTv, descriptionTv, priceTv, distanceTv, categoryTv;
    FancyButton callBtn, messageBtn;
    LinearLayout contactLL;
    RelativeLayout closeRl;
    FloatingActionButton addRemFavBtn;

    FirebaseUser user;
    ProgressDialog progressDialog;

    boolean isFav = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_preview);

        postItem = (PostItem)getIntent().getSerializableExtra("selected_post_item");

        user = FirebaseAuth.getInstance().getCurrentUser();

        initialiseViews();
        setupViews();
        setUpViewPager();
        setUpFavButton();
    }

    public void initialiseViews(){
        viewPager = findViewById(R.id.ad_preview_images_viewpager);
        emptyViewPagerIv = findViewById(R.id.ad_preview_empty_view_pager_iv);
        titleTv = findViewById(R.id.ad_preview_title_tv);
        datePostedTv = findViewById(R.id.ad_preview_posted_on_tv);
        descriptionTv = findViewById(R.id.ad_preview_description_tv);
        priceTv = findViewById(R.id.ad_preview_price_tv);
        distanceTv = findViewById(R.id.ad_preview_distance_tv);
        categoryTv = findViewById(R.id.ad_preview_category_tv);

        callBtn = findViewById(R.id.ad_preview_call_btn);
        messageBtn = findViewById(R.id.ad_preview_message_btn);

        contactLL= findViewById(R.id.ad_preview_contact_ll);
        closeRl = findViewById(R.id.ad_preview_close_post_rl);
        addRemFavBtn = findViewById(R.id.ad_preview_set_rem_fav_fab);


        progressDialog = new ProgressDialog(this);


    }
    public void setupViews(){
        titleTv.setText(postItem.getTitle());
        descriptionTv.setText(postItem.getDescription());
        priceTv.setText(postItem.getPrice());
        distanceTv.setText(setDistance(postItem));

        // set category
        getCategoriesFromDatabase(postItem.getCategory());


        // hide call button if phone number not provided
        if(postItem.getPhoneNumber().isEmpty())
            callBtn.setVisibility(View.GONE);

        // if ad is posted by the same user, show close ad option
        if(user.getEmail().equals(postItem.getEmailId()))
            closeRl.setVisibility(View.VISIBLE);
        else
            contactLL.setVisibility(View.VISIBLE);

        // setup posted date
        setUpDate();

        // setup distance

        // setup close post feature


        // setup contact
        // setup chat
        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog("Please Wait ...");
                getPreviousChatHistory();
            }
        });

        // call seller
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callSeller();
            }
        });

        // setup fav button
        addRemFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFav)
                    removePostFromFavList();
                else
                    addPostToFavList();
            }
        });




    }

    public void showProgressDialog(String msg){
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    public void getCategoriesFromDatabase(final int id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("categories")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot value, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if(e != null){
                            Log.d("POST_FRAG","Listen failed!",e);
                            return;
                        }
                        for(QueryDocumentSnapshot doc : value){
                            CategoryItem item = doc.toObject(CategoryItem.class);
                            if(item.getId()==id)
                                categoryTv.setText(item.getName());
                        }

                    }
                });
    }

    public void setUpViewPager(){


        List<Uri> list = new ArrayList<>();
        if(!postItem.getImgUrlOne().isEmpty())
            list.add(Uri.parse(postItem.getImgUrlOne()));
        if(!postItem.getImgUrlTwo().isEmpty())
            list.add(Uri.parse(postItem.getImgUrlTwo()));
        if(!postItem.getImgUrlThree().isEmpty())
            list.add(Uri.parse(postItem.getImgUrlThree()));
        if(!postItem.getImgUrlFour().isEmpty())
            list.add(Uri.parse(postItem.getImgUrlFour()));
        if(!postItem.getImgUrlFive().isEmpty())
            list.add(Uri.parse(postItem.getImgUrlFive()));

        if(!list.isEmpty())
            emptyViewPagerIv.setVisibility(View.GONE);


        PhotoPreviewPagerAdapter adapter = new PhotoPreviewPagerAdapter(this,list);
        viewPager.setAdapter(adapter);
    }

    public void setUpDate(){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM");
        datePostedTv.setText(dateFormatter.format(Long.parseLong(postItem.getPostId())));
    }

    public void getPreviousChatHistory(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference chatRef = db.collection("chats");
        Query query = chatRef.whereEqualTo("userIdOne",user.getEmail())
                .whereEqualTo("userIdTwo",postItem.getEmailId())
                .whereEqualTo("postId",postItem.getPostId());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
                        for(QueryDocumentSnapshot doc: task.getResult()){
                            Log.d("AD_PREV", doc.getId() + " => " + doc.getData());
                            ChatItem chatItem = doc.toObject(ChatItem.class);
                            startChatActivity(chatItem);
                        }
                    }else{
                        Log.d("AD_PREV","No Previous Chats!");
                        ChatItem chatItem = new ChatItem(Calendar.getInstance().getTimeInMillis()+"",
                                user.getEmail(),postItem.getEmailId(),
                                postItem.getPostId());
                        createNewChatRoom(chatItem);
                        //startChatActivity(chatItem);
                    }
                }else{
                    Log.d("AD_PREV","Error getting chats. "+task.getException());
                }
            }
        });
    }

    public void createNewChatRoom(final ChatItem chatItem){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference chatRef = db.collection("chats").document(chatItem.getChatId());
        chatRef.set(chatItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AD_PREV","Successfully created a new chatroom.");
                startChatActivity(chatItem);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("AD_PREV","Error creating a new chatroom.  "+e);
            }
        });

    }

    public void startChatActivity(ChatItem chatItem){
        Intent intent=new Intent(AdPreviewActivity.this,ChatActivity.class);
        intent.putExtra("selected_chat",chatItem);
        intent.putExtra("user_name","Send a message");
        progressDialog.dismiss();
        startActivity(intent);

    }

    public void callSeller(){
        String phone = postItem.getPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phone,null));
        startActivity(intent);
    }

    public void setUpFavButton(){
        showProgressDialog("Please Wait ...");
        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());

        favRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){

                        // if already favorited
                        addRemFavBtn.setImageResource(R.drawable.ic_star_black_24dp);
                        isFav = true;

                    }else{
                        //no such doc exits
                        // if not favorited
                        Log.d(TAG,"No such doc exits!");
                        addRemFavBtn.setImageResource(R.drawable.ic_star_border_black_24dp);
                        isFav = false;

                    }
                }else{
                    Log.d(TAG,"Error getting document!");
                }
            }
        });
    }


    public void addPostToFavList(){
        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());


        favRef.set(postItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Ad Favorited!");
                Toasty.info(AdPreviewActivity.this,"Added to Favorites!").show();
                setUpFavButton();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to update favorites!   " + e);
            }
        });
    }

    public void removePostFromFavList(){
        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());

        favRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Ad removed from fav List.");
                Toasty.info(AdPreviewActivity.this,"Removed from Favorites!").show();
                setUpFavButton();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to remove ad from fav list.   "+e);
            }
        });
    }

    public String setDistance(PostItem postItem){
        CalculateDistance calculateDistance = new CalculateDistance(this);
        double distance = calculateDistance.distanceInKM(postItem.getLatitude(),postItem.getLongitude());
        DecimalFormat value = new DecimalFormat("#.#");

        return value.format(distance)+" kms away";


    }

}
