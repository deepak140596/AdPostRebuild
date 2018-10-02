package com.sapicons.deepak.k2psap.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sapicons.deepak.k2psap.Fragments.ChatsFragment
import com.sapicons.deepak.k2psap.Fragments.ExploreFragment
import com.sapicons.deepak.k2psap.Fragments.FavoritesFragment
import com.sapicons.deepak.k2psap.Fragments.PostFragment
import com.sapicons.deepak.k2psap.Others.UserLocation
import com.sapicons.deepak.k2psap.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var auth: FirebaseAuth ?=null
    private var doubleBackToExit = false
    lateinit var locationManager : LocationManager


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

        // check for Permissions
        askForPermissions()

        //start explore fragment
        startExploreFragment();

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



    // setup UI (menus, bottom Nav bar, Side Nav bar)
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


    // side navigation bar
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

    // bottom navigation bar

    private val mOnBottomNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                var fragment : Fragment? = ExploreFragment()
                var fragTags =""
                when(item.itemId){
                    R.id.bottom_navigation_home ->{
                        fragment = ExploreFragment()

                    }
                    R.id.bottom_navigation_post ->{
                        fragment = PostFragment()
                    }
                    R.id.bottom_navigation_favorites -> {
                        fragment = FavoritesFragment()
                    }
                    R.id.bottom_navigation_chat -> {
                        fragment = ChatsFragment()

                    }

                }

                var fragmentManager = fragmentManager
                fragmentManager.beginTransaction().replace(R.id.navigation_activity_content_frame,fragment,fragTags).commit()

                true
            }


    private fun startExploreFragment(){
        var fragment = ExploreFragment()
        var fragmentManager = fragmentManager
        fragmentManager.beginTransaction().replace(R.id.navigation_activity_content_frame,fragment,"").commit()

    }

    // handle user authentication data
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



    @SuppressLint("MissingPermission")
    // get user location
    fun getUserLocation(){

        var userLocation = UserLocation(this)
        locationManager = userLocation.locationManager

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) run {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1)
        } else*/
        Log.d("NA","LM: "+locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))

    }

    private fun askForPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            1 ->{

                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted

                    // get the user's location
                    getUserLocation()
                }
                else{
                    // permission was denied
                    Toasty.error(this,"Permission Denied").show()
                    // set empty list view
                }
            }

        }
    }
}
