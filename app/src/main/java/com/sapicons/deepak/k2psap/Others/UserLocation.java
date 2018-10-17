package com.sapicons.deepak.k2psap.Others;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Deepak Prasad on 27-09-2018.
 */

public class UserLocation {
    //Activity activity;
    Context context;

    long MIN_TIME_FRAME = 1000 * 60 * 60 ; // 1 hr
    int MIN_DISTANCE = 1000 ; // 1000m

    public UserLocation(Context context) {
        //this.activity = activity;
        this.context = context;
    }

    public LocationManager getLocationManager() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // if location changes, update the location on device as well as on update location history on database
                saveLocationToSharedPreferences(location);
                //saveUserHistory(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }else
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FRAME,MIN_DISTANCE , locationListener);

        return  locationManager;
    }

    public void saveLocationToSharedPreferences(Location location){
        Log.d("LOCATION","Location: "+location);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        editor.putFloat("latitude",(float)location.getLatitude());
        editor.putFloat("longitude",(float)location.getLongitude());
        editor.apply();
        editor.commit();

    }
    public void saveLocationToSharedPreferences(LatLng latLng){
        Log.d("LOCATION","Location: "+latLng);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        editor.putFloat("latitude",(float)latLng.latitude);
        editor.putFloat("longitude",(float)latLng.longitude);
        editor.apply();
        editor.commit();

    }

    public Location getSavedLocation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        float lat = sharedPreferences.getFloat("latitude",0);
        float lon = sharedPreferences.getFloat("longitude",0);
        Location location = new Location("SavedLocation");
        location.setLatitude(lat);
        location.setLongitude(lon);

        return location;
    }

    public void saveUserHistory(Location location){

        Log.d("LOCATION","Updating to firebase");

        String timestamp = Calendar.getInstance().getTimeInMillis()+"";

        FirebaseUser  user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference userLocRef = firebaseFirestore.collection("locationHistory").document(user.getEmail())
                .collection("locationHistory")
                .document(timestamp);
        Map<String,Object> docData = new HashMap<>();
        docData.put("latitude",location.getLatitude()+"");
        docData.put("longitude",location.getLongitude()+"");

        userLocRef.set(docData).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOCATION","Failed to update user location to database ");
            }
        });
    }


    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getThoroughfare();
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            add = add + ", " + obj.getSubLocality();
            add = add + ", " + obj.getSubAdminArea();
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();
            //add= add+ "\n" +


            Log.v("IGA", "Address" + add);
            // Toast.makeText(this, "Address=>" + add,
            // Toast.LENGTH_SHORT).show();

            // TennisAppActivity.showDialog(add);
            return add;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }
}
