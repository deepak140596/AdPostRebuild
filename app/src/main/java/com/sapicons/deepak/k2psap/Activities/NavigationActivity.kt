package com.sapicons.deepak.k2psap.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.sapicons.deepak.k2psap.Fragments.ChatsFragment
import com.sapicons.deepak.k2psap.Fragments.ExploreFragment
import com.sapicons.deepak.k2psap.Fragments.FavoritesFragment
import com.sapicons.deepak.k2psap.Fragments.PostFragment
import com.sapicons.deepak.k2psap.Objects.CategoryItem
import com.sapicons.deepak.k2psap.Others.UserLocation
import com.sapicons.deepak.k2psap.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import java.util.ArrayList

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var auth: FirebaseAuth ?=null
    private var doubleBackToExit = false
    lateinit var locationManager : LocationManager

    //var categoryList: MutableList<CategoryItem> = ArrayList()
    var TAG = "NAV_ACTIVITY"

    companion object {
       @JvmStatic var categoryList: MutableList<CategoryItem> = ArrayList()
    }


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

        // get local categories
        getCategoriesFromDatabase()


        //start explore fragment
        //startExploreFragment()


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if(doubleBackToExit){

                // set manually set location to false
                // next time the app starts with live location of the user
                setManualLocation(false)
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
        //menuInflater.inflate(R.menu.navigation, menu)
        //menuInflater.inflate(R.menu.collapsed_search_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
        */
        //return super.onOptionsItemSelected(item)
        return false
    }


    // side navigation bar
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_your_posts -> {

                startActivity(Intent(this,YourAdsActivity::class.java))
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
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var isLocationManual = sharedPref.getBoolean("isLocationManual",false)

        var userLocation = UserLocation(this)
        if(!isLocationManual) {
            locationManager = userLocation.locationManager
            Log.d("NA", "LM: " + locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER))
        }


        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) run {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    1)
        } else*/


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


    fun getLocaleCountry(): String {
        var locale = this.resources.configuration.locale.country
        Log.d("LOCALE","Country: "+locale)
        return locale
    }


    fun getCategoriesFromDatabase() {
        val db = FirebaseFirestore.getInstance()

        categoryList = ArrayList<CategoryItem>()

        db.collection("categories")
                .document(getLocaleCountry())
                .collection("categories")
                .addSnapshotListener(EventListener { value, e ->
                    if (e != null) {
                        Log.d(TAG, "Listen failed!", e)
                        return@EventListener
                    }
                    for (doc in value!!) {
                        val item = doc.toObject(CategoryItem::class.java)
                        Log.d(TAG, "CATEGORIES: " + item.name)
                        categoryList.add(item)
                    }

                    // save categories to database

                    //start Fragments
                    startExploreFragment()
                })
    }

    fun setManualLocation(isLocationManual: Boolean){
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()

        editor.putBoolean("isLocationManual",isLocationManual)
        editor.apply()
        editor.commit()
    }

}
