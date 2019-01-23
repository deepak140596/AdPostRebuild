package com.bxtore.dev.bxt;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by Deepak Prasad on 26-09-2018.
 */

public class FirebaseInstanceIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("FCM_Service", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
        //saveTokenToSharedPreferences(token);
    }

    private void sendRegistrationToServer(String token){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();


            DocumentReference docRef = db.collection("users").document(user.getEmail());
            docRef.update("tokenId", token).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("FCM_Service", "TokenId update failed!");
                }
            });
        }
    }

    /*private void saveTokenToSharedPreferences(String token){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor  editor= sharedPreferences.edit();

        editor.putString("fcm_token",token);
        editor.apply();
        editor.commit();

    }*/


}
