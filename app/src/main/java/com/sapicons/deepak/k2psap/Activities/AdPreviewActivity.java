package com.sapicons.deepak.k2psap.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.sapicons.deepak.k2psap.Adapters.CategoryAdapter;
import com.sapicons.deepak.k2psap.Adapters.PhotoPreviewPagerAdapter;
import com.sapicons.deepak.k2psap.Objects.CategoryItem;
import com.sapicons.deepak.k2psap.Objects.ChatItem;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.Objects.User;
import com.sapicons.deepak.k2psap.Others.CalculateDistance;
import com.sapicons.deepak.k2psap.Others.UserLocation;
import com.sapicons.deepak.k2psap.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

public class AdPreviewActivity extends AppCompatActivity {
    String TAG = "AD_PREVIEW_ACTIVITY";

    PostItem postItem;
    ViewPager viewPager;
    ImageView emptyViewPagerIv, postUserIv;
    TextView titleTv, datePostedTv, descriptionTv, priceTv;
    TextView distanceTv, categoryTv,  closedAdInstructionTv;
    TextView postUserNameTv, locationNameTv, relativeTimeTv;
    FancyButton callBtn, messageBtn,closeBtn;
    LinearLayout contactLL;
    RelativeLayout closeRl;
    FloatingActionButton addRemFavBtn;

    PopupWindow popupWindow;

    RelativeLayout bottomSheet;
    BottomSheetBehavior bottomSheetBehavior;



    FirebaseUser user;
    ProgressDialog progressDialog;

    boolean isFav = false;
    //boolean isFavFrag = false;

    // related to slideshow
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    int NUM_PAGES;
    Handler handler ;
    Runnable Update;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_preview);

        postItem = (PostItem)getIntent().getSerializableExtra("selected_post_item");
        //isFavFrag = getIntent().getBooleanExtra("is_fav_frag",true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        initialiseViews();
        setupBottomSheet();

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
        closeBtn = findViewById(R.id.ad_preview_close_btn);

        contactLL= findViewById(R.id.ad_preview_contact_ll);
        closeRl = findViewById(R.id.ad_preview_close_post_rl);
        addRemFavBtn = findViewById(R.id.ad_preview_set_rem_fav_fab);

        postUserIv = findViewById(R.id.ad_preview_user_iv);
        postUserNameTv = findViewById(R.id.ad_preview_user_name_tv);
        locationNameTv = findViewById(R.id.ad_preview_location_name_tv);
        closedAdInstructionTv = findViewById(R.id.ad_preview_closed_ad_instruction_tv);

        relativeTimeTv = findViewById(R.id.ad_preview_relative_time_tv);

        bottomSheet = findViewById(R.id.bottom_sheet_rl);




        progressDialog = new ProgressDialog(this);


    }
    public void setupViews(){
        titleTv.setText(postItem.getTitle());
        descriptionTv.setText(postItem.getDescription());
        priceTv.setText(setTradeType(postItem));
        distanceTv.setText(setDistance(postItem));

        // set category
        //getCategoriesFromDatabase(postItem.getCategory());
        categoryTv.setText(postItem.getCategoryName());


        // hide call button if phone number not provided
        if(postItem.getPhoneNumber().isEmpty())
            callBtn.setVisibility(View.GONE);

        // setup contact layouts and disable them as per closed posts
        // if the ad prev is from fav fragments,check if its stauts is closed
        //Log.d(TAG,"isFavFrag: "+isFavFrag);
        //if(isFavFrag)
            checkStatusForFavFragment();
        //else
            //setupContactDetails();



        // setup posted date
        setUpDate();

        // setup post user name
        if(postItem.getPostUserName()!=null)
            postUserNameTv.setText(postItem.getPostUserName());
        // setup post user image
        if(postItem.getPostUserPicUrl()!=null)
            Glide.with(this).load(postItem.getPostUserPicUrl()).into(postUserIv);

        // setup location name
        setupLocationName();

        // set relative time
        relativeTimeTv.setText(getRelativeTime(Long.parseLong(postItem.getPostId())));



        // setup close post feature
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeStatusOfPost(postItem,"closed");
            }
        });


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

    public void setupBottomSheet(){

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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
        viewPager.setClipToPadding(false);
        viewPager.setPadding(50,16,60,0);
        viewPager.setPageMargin(16);
        viewPager.setAdapter(adapter);
        NUM_PAGES = list.size();

        //setUpSlideShow();
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
        //showProgressDialog("Please Wait ...");
        DocumentReference favRef = FirebaseFirestore.getInstance()
                .collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds")
                .document(postItem.getPostId());

        favRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                //progressDialog.dismiss();
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

    public void setUpSlideShow(){


        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    public void setupLocationName(){
        UserLocation userLocation = new UserLocation(this);
        String address = userLocation.getAddress(postItem.getLatitude(),postItem.getLongitude());
        locationNameTv.setText(address);
    }

    public static String setTradeType(PostItem postItem){
        String exchangeType = postItem.getExchangeType();

        // TODO
        // remove next two lines
        if(exchangeType==null)
            exchangeType = "Price";
        if(exchangeType.equals("Free"))
            return "Free";
        else if(exchangeType.equals("Price"))
            return postItem.getPrice();
        else if(exchangeType.equals("Exchange"))
            return "Exchange for "+postItem.getExchangeFor();

        return "";
    }

    public void changeStatusOfPost(final PostItem postItem, final String status){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("ads").document(postItem.getPostId());
        documentReference.update("status",status).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Update status of post failed. Error: "+e);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("chats");
                    Query query = collectionReference.whereEqualTo("postId", postItem.getPostId());

                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                 for(QueryDocumentSnapshot document : task.getResult()){
                                     ChatItem chatItem = document.toObject(ChatItem.class);
                                     changeStatusOfChats(chatItem, status);
                                 }

                                Toasty.info(AdPreviewActivity.this,"Ad Closed!").show();
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Failed getting chats . Error: "+e);
                        }
                    });


            }
        });

        DocumentReference dr = firebaseFirestore.collection("favorites")
                .document(user.getEmail()).collection("favoritedAds").document(postItem.getPostId());
        dr.update("status",status).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Update status of fav post failed. Error: "+e);
            }
        });
    }

    public void changeStatusOfChats(ChatItem chatItem,String status){

        DocumentReference chatDoc = FirebaseFirestore.getInstance().collection("chats").document(chatItem.getChatId());
        chatDoc.update("status", status).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Failed updating status of chat  . Error: "+e);
            }
        });
    }

    public void checkStatusForFavFragment(){
        showProgressDialog("Please Wait!");
        DocumentReference postRef = FirebaseFirestore.getInstance().collection("ads").document(postItem.getPostId());
        postRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    PostItem pi = task.getResult().toObject(PostItem.class);

                    if(pi.getStatus().equals("closed")){
                        closeRl.setVisibility(View.GONE);
                        contactLL.setVisibility(View.GONE);
                        closedAdInstructionTv.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        setupContactDetails();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Error fetching doc: "+e);
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    public void setupContactDetails(){

        // if ad is posted by the same user, show close ad option

            if (user.getEmail().equals(postItem.getEmailId())) {
                if (postItem.getStatus().equals("open"))
                    closeRl.setVisibility(View.VISIBLE);
                else if (postItem.getStatus().equals("closed"))
                    closedAdInstructionTv.setVisibility(View.VISIBLE);
            } else {
                if (postItem.getStatus().equals("closed"))
                    closedAdInstructionTv.setVisibility(View.VISIBLE);
                else
                    contactLL.setVisibility(View.VISIBLE);
            }

    }

    public static String getRelativeTime(long oldTime){

        long now = Calendar.getInstance().getTimeInMillis();
        CharSequence ago = DateUtils.getRelativeTimeSpanString(oldTime,now,DateUtils.MINUTE_IN_MILLIS);
        return ago.toString();
    }
}
