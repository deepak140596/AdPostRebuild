package com.sapicons.deepak.k2psap.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sapicons.deepak.k2psap.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Main"

        mAuth = FirebaseAuth.getInstance()

        initialiseUI()


        Log.d("TAG","Main")
    }

    fun initialiseUI(){

        // setup sign out button
        main_sign_out_btn.setOnClickListener{
            signOutUser()
        }
    }

    fun checkIfUserIsSignedIn(){
        var user: FirebaseUser?= mAuth!!.currentUser

        if(user == null) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

    }



    fun signOutUser(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener{
                    Toasty.success(this,"Signed Out Successfully!!").show()
                    checkIfUserIsSignedIn()
                }
    }

    override fun onResume() {
        super.onResume()

        checkIfUserIsSignedIn()
    }
}
