package com.sapicons.deepak.k2psap.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sapicons.deepak.k2psap.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var auth: FirebaseAuth ?=null
    private var doubleBackToExit = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        setSupportActionBar(toolbar)


        //set drawer toggle
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //set side navigation bar
        nav_view.setNavigationItemSelectedListener(this)
        //set bottom navigation bar
        bottom_navigation_bar.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener)


        auth = FirebaseAuth.getInstance()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if(doubleBackToExit){
                super.onBackPressed()
            }else{

                this.doubleBackToExit = true
                Toasty.info(this,"Press BACK again to exit").show()
                Handler().postDelayed(Runnable {
                    this.doubleBackToExit = false
                },2000)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
            R.id.nav_log_out -> {
                signOutUser()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private val mOnBottomNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.bottom_navigation_home ->{

                    }
                    R.id.bottom_navigation_post ->{


                    }
                    R.id.bottom_navigation_favorites -> {

                    }
                    R.id.bottom_navigation_chat -> {


                    }
                }

                true
            }


    private fun signOutUser(){
        auth!!.signOut()
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener {
                    checkIfUserIsSignedIn()
                }
    }

    private fun checkIfUserIsSignedIn(){
        var user: FirebaseUser?= auth!!.currentUser

        // if user is signed out
        if(user == null){
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }
    }
}
