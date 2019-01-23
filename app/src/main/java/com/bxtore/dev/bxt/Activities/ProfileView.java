package com.bxtore.dev.bxt.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.bxtore.dev.bxt.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ProfileView extends AppCompatActivity {

    TextView nameTv, phoneTv, emailTv;
    CircleImageView picIv;
    FirebaseUser user;
    StorageReference storageReference;
    ImageButton addPhoneBtn;
    ImageView editUserPicIV;

    Uri userPicUri;
    ProgressDialog progressDialog;

    String TAG= "ACTIVITY";

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String confirmedPhoneNumber="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        setTitle("Profile");
        Log.d(TAG,"UserProfileActivity");
        getSupportActionBar().setElevation(0);

        getUserAccount();
        initialiseViews();
        setViews();
        initialiseMCallbacks();
    }

    private void initialiseViews(){
        nameTv=findViewById(R.id.user_profile_name_tv);
        phoneTv=findViewById(R.id.user_profile_phone_tv);
        emailTv=findViewById(R.id.user_profile_email_tv);
        picIv=findViewById(R.id.user_profile_pic_iv);
        addPhoneBtn = findViewById(R.id.user_profile_add_phone_btn);
        editUserPicIV = findViewById(R.id.user_profile_pic_change_iv);
        progressDialog=new ProgressDialog(this);

        //showProgressDialog("Updating Profile Pic ...");

    }

    public void showProgressDialog(String msg){

        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    private void getUserAccount(){
        user= FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setViews(){
        nameTv.setText(user.getDisplayName());
        emailTv.setText(user.getEmail());
        Log.d(TAG,"Phone Num: "+user.getPhoneNumber());
        if((user.getPhoneNumber() == null ) || (user.getPhoneNumber()+"").length()==0){
            phoneTv.setText("Add Phone Number");
            addPhoneBtn.setVisibility(View.VISIBLE);
        }else{

            phoneTv.setText(user.getPhoneNumber());
            addPhoneBtn.setVisibility(View.GONE);
        }

        Log.d("USER","picUrl: "+user.getPhotoUrl());
        Log.d("USER","phoneN: "+user.getPhoneNumber());

        if(user.getPhotoUrl()!=null){
            Glide.with(this).load(user.getPhotoUrl()).into(picIv);
        }else
            Glide.with(this).load(R.drawable.placeholder_profile).into(picIv);

        editUserPicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileView.this);


            }
        });

        addPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogToEnterPhoneNumber();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserAccount();
        setViews();
    }

    private void uploadUserProfilePicToDB(){
        showProgressDialog("Uploading Image");
        storageReference= FirebaseStorage.getInstance().getReference().child("users_profile_pic")
                .child(user.getEmail());
        if(userPicUri!=null) {
            storageReference = storageReference.child(userPicUri.getLastPathSegment());
            UploadTask task = storageReference.putFile(userPicUri);

            Task<Uri> urlTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("PIC_URL","url: "+downloadUri);
                        updateUserProfile(downloadUri);
                        updatePicUrlToDB(downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

    }

    private void updatePicUrlToDB(Uri picUri){
        DocumentReference profileRef = FirebaseFirestore.getInstance().collection("users")
                .document(user.getEmail());
        profileRef.update("photoUrl",picUri.toString()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Update to DB failed: "+e);
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ProfileView.this);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("is_custom_profile_pic",true);
                    editor.apply();
                    editor.commit();
                }
            }
        });
    }

    private void updateUserProfile(Uri picUri){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(picUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("USER", "User profile updated.");
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                userPicUri = result.getUri();
                Glide.with(this).load(userPicUri).into(picIv);
                Log.d("URI","Result URI: "+userPicUri.toString());

                uploadUserProfilePicToDB();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void showDialogToEnterPhoneNumber(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
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


            }
        });

        builder.create().show();
    }

    public void sendOTP(String phoneNumber){
        showProgressDialog("Please wait while we confirm your phone number.");
        confirmedPhoneNumber = phoneNumber;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,60, TimeUnit.SECONDS, this, mCallbacks);

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
                Toasty.success(ProfileView.this,"Phone Verified!").show();

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
                    Toasty.error(ProfileView.this,"Invalid Phone Number. Try Again!").show();
                    showDialogToEnterPhoneNumber();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toasty.error(ProfileView.this,"Please try after some time!").show();
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
                            //askForConfirmationToUploadAd();

                        } else {
                            Log.w(TAG, "linkWithCredential:failure", task.getException());
                            Toasty.error(ProfileView.this, "Phone number already exists.").show();
                            //sharePhoneNumberSwitch.setChecked(false);
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
