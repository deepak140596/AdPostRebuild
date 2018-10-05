package com.sapicons.deepak.k2psap.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sapicons.deepak.k2psap.Adapters.PhotoPreviewPagerAdapter;
import com.sapicons.deepak.k2psap.Objects.CategoryItem;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Deepak Prasad on 28-09-2018.
 */

public class PostFragment extends Fragment {

    ImageButton imgOneIv, imgTwoIv, imgThreeIv, imgFourIv,imgFiveIv;
    ImageView emptyViewPagerIv;
    ViewPager viewPager;
    FloatingActionButton doneBtn;
    Spinner categorySpinner;
    ProgressDialog progressDialog;
    EditText adTitleEt, descriptionEt, priceEt;
    SwitchCompat sharePhoneNumberSwitch;

    Uri imgOneUri, imgTwoUri, imgThreeUri, imgFourUri, imgFiveUri;
    int categoryId ;
    String categoryName;
    String imgOneUrl="", imgTwoUrl="",imgThreeUrl="",imgFourUrl="",imgFiveUrl="";
    String confirmedPhoneNumber = "";

    int imgClicked=0;
    int imageUploadCount =0;
    List<Uri> list;
    StorageReference storageReference;

    Context context;

    String TAG ="POST_FRAG";

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Post Ad");

        View view = inflater.inflate(R.layout.fragment_post, container, false);
        context = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        initialiseViews(view);
        getCategoriesFromDatabase();
    }

    public void  initialiseViews(View view){

        imgOneIv = view.findViewById(R.id.post_image_one_ib);
        imgTwoIv = view.findViewById(R.id.post_image_two_ib);
        imgThreeIv = view.findViewById(R.id.post_image_three_ib);
        imgFourIv = view.findViewById(R.id.post_image_four_ib);
        imgFiveIv = view.findViewById(R.id.post_image_five_ib);
        emptyViewPagerIv = view.findViewById(R.id.post_empty_view_pager_iv);

        adTitleEt = view.findViewById(R.id.post_title_et);
        descriptionEt = view.findViewById(R.id.post_description_et);
        priceEt = view.findViewById(R.id.post_price_et);

        doneBtn = view.findViewById(R.id.post_done_fab);
        viewPager= view.findViewById(R.id.post_preview_images_viewpager);
        categorySpinner = view.findViewById(R.id.post_category_spinner);
        sharePhoneNumberSwitch = view.findViewById(R.id.post_share_phone_switch);


        list = new ArrayList<>();


        setTextWatchers();

        setOnClickListeners();

        initialiseMCallbacks();

        // set switch listener



    }

    public void setTextWatchers(){
        // set text watchers
        priceEt.addTextChangedListener(watcher);
        descriptionEt.addTextChangedListener(watcher);
        adTitleEt.addTextChangedListener(watcher);
    }

    public void setOnClickListeners(){
        // set on click listeners
        imgOneIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClicked =1;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(),PostFragment.this);
            }
        });
        imgTwoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClicked =2;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(),PostFragment.this);
            }
        });
        imgThreeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClicked =3;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(),PostFragment.this);
            }
        });
        imgFourIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClicked =4;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(),PostFragment.this);
            }
        });
        imgFiveIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgClicked =5;
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(getActivity(),PostFragment.this);
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // check if user has verified his phone number, if not, verify it
                // only when user opts to share his phone number;
                if(sharePhoneNumberSwitch.isChecked()){
                    if(user.getPhoneNumber().isEmpty())
                        showDialogToEnterPhoneNumber();
                    else
                        askForConfirmationToUploadAd();
                        //linkPhoneAuthWithCurrentAuth();
                }
                else
                    askForConfirmationToUploadAd();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri picUri = result.getUri();
                setUriForImages(picUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void setUriForImages(Uri picUri){
        if(imgClicked ==1){
            imgOneUri = picUri;
            Glide.with(getActivity()).load(picUri).into(imgOneIv);
        }
        else if(imgClicked ==2){
            imgTwoUri = picUri;
            Glide.with(getActivity()).load(picUri).into(imgTwoIv);
        }
        else if(imgClicked ==3){
            imgThreeUri = picUri;
            Glide.with(getActivity()).load(picUri).into(imgThreeIv);
        }
        else if(imgClicked ==4){
            imgFourUri = picUri;
            Glide.with(getActivity()).load(picUri).into(imgFourIv);
        }
        else if(imgClicked ==5){
            imgFiveUri = picUri;
            Glide.with(getActivity()).load(picUri).into(imgFiveIv);
        }

        setUpViewPager();
    }


    public void setUpViewPager(){

        emptyViewPagerIv.setVisibility(View.GONE);
        list = new ArrayList<>();
        if(imgOneUri != null)
            list.add(imgOneUri);
        if(imgTwoUri != null)
            list.add(imgTwoUri);
        if(imgThreeUri != null)
            list.add(imgThreeUri);
        if(imgFourUri != null)
            list.add(imgFourUri);
        if(imgFiveUri != null)
            list.add(imgFiveUri);

        PhotoPreviewPagerAdapter adapter = new PhotoPreviewPagerAdapter(getActivity(),list);
        viewPager.setAdapter(adapter);
    }


    // set up categories

    public void getCategoriesFromDatabase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final List<CategoryItem> list = new ArrayList<>();
        showProgressDialog("Please Wait ...");

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
                            list.add(item);
                        }
                        progressDialog.dismiss();
                        setCategoriesToSpinner(list);
                    }
                });
    }

    public void setCategoriesToSpinner(final List<CategoryItem> list){

        // store the category names
        List<String> catName = new ArrayList<>();
        for(CategoryItem item: list)
            catName.add(item.getName());

        // Create a default adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,catName);

        // Drop down layout style
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryId = list.get(i).getId();
                categoryName = list.get(i).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // set up text watcher

    private  final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if(     adTitleEt.getText().toString().length() == 0
                    || descriptionEt.getText().toString().length() ==0
                    || priceEt.getText().toString().length() ==0        ){

                // hide FAB
                doneBtn.setVisibility(View.GONE);
            }
            else{
                doneBtn.setVisibility(View.VISIBLE);
            }
        }
    };

    public void showProgressDialog(String msg){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(msg);
        progressDialog.show();
    }


    // ask for confirmation to upload ad
    public void askForConfirmationToUploadAd(){
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Post Ad?")
                .setMessage("Are you sure you want to Post Ad?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgressDialog("Posting Ad. Please Wait ...");
                        postAd();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    public void postAd(){

        if(list.isEmpty())
            uploadPostToDatabase();
        else
            uploadPics();
    }

    public void uploadPics(){

        storageReference = FirebaseStorage.getInstance().getReference().child("users").child(user.getEmail())
                .child("posts");

        for(Uri uri: list){

            if(uri != null){

                final StorageReference newRef = storageReference.child(uri.getLastPathSegment());
                UploadTask task = newRef.putFile(uri);

                Task<Uri> urlTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return newRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.d("PIC_URL","url: "+downloadUri);
                            imageUploadCount++;
                            setImageDownloadUrl(imageUploadCount,downloadUri);

                            if(imageUploadCount==list.size())
                                //Toasty.success(getActivity(),"Images Uploaded").show();
                                uploadPostToDatabase();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        }
    }

    public void  setImageDownloadUrl(int img, Uri picUrl){
        if(img == 1)
            imgOneUrl = picUrl.toString();
        else if(img ==2)
            imgTwoUrl = picUrl.toString();
        else if(img == 3)
            imgThreeUrl = picUrl.toString();
        else if(img == 4)
            imgFourUrl = picUrl.toString();
        else if(img == 5)
            imgFiveUrl = picUrl.toString();
    }

    public void uploadPostToDatabase(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        String postId = Calendar.getInstance().getTimeInMillis()+"";
        String email = user.getEmail(),
                title = adTitleEt.getText().toString(),
                description = descriptionEt.getText().toString(),
                price = priceEt.getText().toString();
        String phoneNumber = "";

        // share phone number only if user opts to
        if(sharePhoneNumberSwitch.isChecked()){
            phoneNumber = user.getPhoneNumber();
        }

        int category = categoryId;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        float latitude = sharedPreferences.getFloat("latitude",0.0f);
        float longitude = sharedPreferences.getFloat("longitude",0.0f);

        PostItem postItem = new PostItem(postId,email,title,description,price,category,categoryName);
        postItem.setPhoneNumber(phoneNumber);
        postItem.setLatitude(latitude);
        postItem.setLongitude(longitude);
        postItem.setImgUrlOne(imgOneUrl);
        postItem.setImgUrlTwo(imgTwoUrl);
        postItem.setImgUrlThree(imgThreeUrl);
        postItem.setImgUrlFour(imgFourUrl);
        postItem.setImgUrlFive(imgFiveUrl);


        FirebaseFirestore db= FirebaseFirestore.getInstance();
        DocumentReference postRef = db.collection("ads").document(postId);


        postRef.set(postItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.success(context,"Ad Posted!").show();
                clearUI();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e("POST_FRAG","Error posting ad. "+e);
            }
        });
    }

    private void clearUI(){
        progressDialog.dismiss();

        //if(imageUploadCount==list.size()) {

            //set edit text as empty
            adTitleEt.setText("");
            descriptionEt.setText("");
            priceEt.setText("");

            imgOneUri = null;
            imgTwoUri = null;
            imgThreeUri = null;
            imgFourUri = null;
            imgFiveUri = null;
            categoryId = 0;
            imgOneUrl = "";
            imgTwoUrl = "";
            imgThreeUrl = "";
            imgFourUrl = "";
            imgFiveUrl = "";

            imgClicked = 0;
            imageUploadCount = 0;
            list = null;

            setUpViewPager();
            emptyViewPagerIv.setVisibility(View.VISIBLE);

            Fragment fragment = new ExploreFragment();
            getActivity().getFragmentManager().beginTransaction().replace(R.id.navigation_activity_content_frame, fragment, "").commit();
        //}
    }


    public void showDialogToEnterPhoneNumber(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_input,null);
        builder.setView(view);

        final EditText inputEt = view.findViewById(R.id.custom_input_et);
        inputEt.setText("+91");
        inputEt.setInputType(InputType.TYPE_CLASS_PHONE);

        builder.setTitle("Enter Phone Number ");
        builder.setPositiveButton("Send OTP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!inputEt.getText().toString().isEmpty()){

                    sendOTP(inputEt.getText().toString().trim());

                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                sharePhoneNumberSwitch.setChecked(false);
            }
        });

        builder.create().show();
    }

    public void sendOTP(String phoneNumber){
        showProgressDialog("Please wait while we confirm your phone number.");
        confirmedPhoneNumber = phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS, getActivity(), mCallbacks);

    }

    public void initialiseMCallbacks(){
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                Toasty.success(context,"Phone Verified!").show();

                //progressDialog.dismiss();
                linkPhoneAuthWithCurrentAuth(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                progressDialog.dismiss();
                //confirmedPhoneNumber = "";

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                    Toasty.error(context,"Invalid Phone Number. Try Again!").show();
                    showDialogToEnterPhoneNumber();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toasty.error(context,"Please try after some time!").show();
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                //VerificationId = verificationId;
                //mResendToken = token;

                // ...
            }
        };
    }

    public void linkPhoneAuthWithCurrentAuth(PhoneAuthCredential credential){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Log.d(TAG, "linkWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            updateUserPhoneNumberToDB();
                            askForConfirmationToUploadAd();

                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toasty.error(context, "Phone number already exists.").show();
                            sharePhoneNumberSwitch.setChecked(false);
                        }
                    }
                });


    }

    public void updateUserPhoneNumberToDB(){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(user.getEmail());
        docRef.update("phoneNumber",user.getPhoneNumber()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Error updating phone number to DB. "+e);
            }
        });
    }
}
