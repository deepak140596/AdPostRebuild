package com.sapicons.deepak.k2psap.Activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.sapicons.deepak.k2psap.FirebaseInstanceIdService
import com.sapicons.deepak.k2psap.Objects.User
import com.sapicons.deepak.k2psap.Objects.UserItem
import es.dmoral.toasty.Toasty
import java.util.*

class SignInActivity : AppCompatActivity() {

    var mAuth : FirebaseAuth? = null
    var RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Sign In"

        mAuth = FirebaseAuth.getInstance()

    }

    private fun startFirebaseAuthUI(){

        var providers: List<AuthUI.IdpConfig> = Arrays.asList(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build()
        )

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), RC_SIGN_IN)

    }


    fun startNextActivity(user: FirebaseUser? = null){
        if(user != null){
            uploadUserInformation(user)
            startActivity(Intent(this, NavigationActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            //var response = IdpResponse.fromResultIntent(data)!!
            finish()

            if(resultCode == Activity.RESULT_OK){
                var user : FirebaseUser?= FirebaseAuth.getInstance().currentUser

                startNextActivity(user)
            }
        }
    }


    override fun onResume() {
        super.onResume()

        var user : FirebaseUser? = mAuth!!.currentUser
        if(user != null)
            startNextActivity(user)
        else
            startFirebaseAuthUI()
    }
}


private fun uploadUserInformation( user: FirebaseUser? = null){

    if(user != null){
        //val token = FirebaseInstanceId.getInstance().getToken()

        //user!!.getIdToken(true).addOnSuccessListener {

            //val tokenId = it.token
            val tokenId = FirebaseInstanceId.getInstance().getToken()
            Log.d("SIGN_IN", "tokenID: $tokenId")

            var name: String? = user.displayName
            var email: String? = user.email
            var picUrl : String = user.photoUrl.toString()
            //var tokenId: String = FirebaseInstanceId.getInstance().id

            val newUser = UserItem(name,email,tokenId,"",picUrl)


            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(email.toString())

            userRef.set(newUser).addOnFailureListener(OnFailureListener { Log.d("SignIn","error uploading user info") })
        //}


    }

}