package com.sapicons.deepak.k2psap.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.Fragment
import android.app.PendingIntent.getActivity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
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
import com.sapicons.deepak.k2psap.Others.RateUs
import com.sapicons.deepak.k2psap.Others.UserLocation
import com.sapicons.deepak.k2psap.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.*
import kotlinx.android.synthetic.main.nav_header_navigation.view.*
import java.util.ArrayList

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var auth: FirebaseAuth ?=null
    private var doubleBackToExit = false
    lateinit var locationManager : LocationManager
    var isPermissionAcquired = false
    var isLocationNull = true

    //var categoryList: MutableList<CategoryItem> = ArrayList()
    var TAG = "NAV_ACTIVITY"
    var REQUEST_CHECK_SETTINGS = 101

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
        setUpNavigationBar()

        //set bottom navigation bar
        bottom_navigation_bar.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener)



        auth = FirebaseAuth.getInstance()

        // check for Permissions
        askForPermissions()

        // get local categories
        //getCategoriesFromDatabase()


        //start explore fragment
        //startExploreFragment()

        content_nav_request_permission_btn.setOnClickListener({askForPermissions()})
        content_nav_enable_location_btn.setOnClickListener({createLocationRequest()})


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

            R.id.nav_your_posts -> {

                if(isPermissionAcquired && !isLocationNull)
                    startActivity(Intent(this,PostedAdsActivity::class.java))
            }

            R.id.nav_profile_view -> {
                startActivity(Intent(this,ProfileView::class.java))
            }

            R.id.nav_settings -> {
                if(isPermissionAcquired && !isLocationNull)
                    startActivity(Intent(this,SettingsActivity::class.java))

            }
            R.id.nav_share -> {
                val i = Intent(android.content.Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Take a look at this app!")
                i.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.string_share_text) + applicationContext.packageName)
                startActivity(Intent.createChooser(i, "Share via"))
            }
            R.id.nav_rate_us -> {
                val rateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + applicationContext.packageName))
                startActivity(rateIntent)
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
                if(isPermissionAcquired && !isLocationNull) {
                    var fragment: Fragment? = ExploreFragment()
                    var fragTags = ""

                    when (item.itemId) {
                        R.id.bottom_navigation_home -> {
                            fragment = ExploreFragment()

                        }
                        R.id.bottom_navigation_post -> {
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
                    fragmentManager.beginTransaction().replace(R.id.navigation_activity_content_frame, fragment, fragTags).commit()

                    true
                }
                true
            }


    private fun startExploreFragment(){


        var fragment = ExploreFragment()
        var fragmentManager = fragmentManager
        fragmentManager.beginTransaction().replace(R.id.navigation_activity_content_frame,fragment,"").commit()
        RateUs.app_launched(this)

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

                    isPermissionAcquired = true

                    //set grant access button invisible
                    content_nav_request_permission_btn.visibility = View.GONE


                    // check if GPS is enabled or not, if prompt to enable gps
                    createLocationRequest()

                }
                else{
                    // permission was denied
                    isPermissionAcquired = false
                    content_nav_request_permission_btn.visibility = View.VISIBLE
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



    fun setManualLocation(isLocationManual: Boolean){
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()

        editor.putBoolean("isLocationManual",isLocationManual)
        editor.apply()
        editor.commit()
    }


    fun setUpNavigationBar(){
        var view = nav_view.getHeaderView(0)

        var user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            //Log.d(TAG,"Name: "+user.displayName)
            view.nav_header_display_name_tv.setText(user.displayName)
            view.nav_header_email_tv.setText(user.email)
            var picUrl = user.photoUrl
            if(picUrl != null){
                Glide.with(this).load(picUrl).into(view.nav_header_profile_pic_iv)
            }else{
                Glide.with(this).load(getDrawable(R.drawable.placeholder_profile)).into(view.nav_header_profile_pic_iv)
            }
        }

        view.setOnClickListener({
            startActivity(Intent(this,ProfileView::class.java))
        })
    }

    fun createLocationRequest(){
        val locationRequest = LocationRequest().apply{
            interval = 1000
            fastestInterval= 5000
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            getUserLocation()
            // also starts explore fragment
            //getCategoriesFromDatabase()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this@NavigationActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG,"ExceptionL :  $sendEx")
                    // Ignore the error.
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun getUserLocation(){
        var userLocation = UserLocation(this)
        var loc = userLocation.locationManager
        var location = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        Log.d(TAG, "LM: $location")

        var progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Retrieving Location ...")
        //progressDialog.show()

        val handler = Handler()
        val runnable = object: Runnable {
            override fun run() {
                Log.d(TAG, "Runnable is working")
                if (location == null) {
                    location = loc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    //location = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    Log.d(TAG, "LM: $location")
                    handler.postDelayed(this, 0)
                } else {
                    handler.removeCallbacks(this)
                    isLocationNull = false
                    Log.d(TAG, "LM: $location")
                    progressDialog.dismiss()
                    getCategoriesFromDatabase()

                }
            }
        }

        //handler.removeCallbacks(this)
        if(userLocation.savedLocation.latitude == 0.0 && userLocation.savedLocation.longitude == 0.0) {
            progressDialog.show()
            handler.postDelayed(runnable, 200)
        }else{
            isLocationNull = false
            Log.d(TAG, "LM: $location")
            getCategoriesFromDatabase()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                content_nav_enable_location_btn.visibility = View.GONE
                getUserLocation()
            }else{
                Toasty.error(this,"Location off").show()
                content_nav_enable_location_btn.visibility = View.VISIBLE
            }
        }
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

                   // start explore fragment after checking for location permissions
                    startExploreFragment()
                })
    }


    override fun onResume() {
        super.onResume()

        //askForPermissions()
    }
}
