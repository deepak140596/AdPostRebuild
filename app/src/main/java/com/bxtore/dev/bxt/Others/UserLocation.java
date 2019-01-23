package com.bxtore.dev.bxt.Others;

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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Deepak Prasad on 27-09-2018.
 */

public class UserLocation {
    //Activity activity;
    Context context;
    String TAG = "USER_LOCATION";

    long MIN_TIME_FRAME = 1000 * 60 * 60; //
    int MIN_DISTANCE = 1000 ; //

    public UserLocation(Context context) {
        //this.activity = activity;
        this.context = context;

    }

    public LocationManager getLocationManager() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        boolean firstTimeStarted = sharedPreferences.getBoolean("firstTimeStarted",true);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // if location changes, update the location on device
                saveLocationToSharedPreferences(location);

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
        }else {
            if(firstTimeStarted){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                editor.putBoolean("firstTimeStarted",false);
                editor.apply();
                editor.commit();
            }else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FRAME, MIN_DISTANCE, locationListener);
                //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FRAME, MIN_DISTANCE, locationListener);
            }
        }

        return  locationManager;
    }

    public void saveLocationToSharedPreferences(Location location){
        Log.d(TAG,"Saving Location: "+location);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        editor.putFloat("latitude",(float)location.getLatitude());
        editor.putFloat("longitude",(float)location.getLongitude());
        editor.apply();
        editor.commit();

    }
    public void saveLocationToSharedPreferences(LatLng latLng){
        Log.d(TAG,"Saving Location: "+latLng);
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
        Log.d(TAG,"Saved Location: "+location);

        return location;
    }


    public String getAddress(double lat, double lng) {

        Log.d(TAG,"get Address for LAT: "+lat+"  LON: "+lng);
        if(lat == 0.0 && lng == 0.0)
            return null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = (obj.getThoroughfare()==null) ? "" :obj.getThoroughfare()+", ";
            //add = add + "\n" + obj.getCountryName();
            //add = add + "\n" + obj.getCountryCode();
            //add = add + "\n" + obj.getAdminArea();
            //add = add + "\n" + obj.getPostalCode();
            add = add + ((obj.getSubLocality()==null) ? "" : obj.getSubLocality()+", ");
            add = add + ((obj.getSubAdminArea()==null) ? "" : obj.getSubAdminArea());
            //add = add + "\n" + obj.getLocality();
            //add = add + "\n" + obj.getSubThoroughfare();
            //add= add+ "\n" +


            Log.v(TAG, "Address received: " + add);

            return add;
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }

    /*
    public void getL(){
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        //**************************
        builder.setAlwaysShow(true); //this is the key ingredient
        //**************************

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    context, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
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
    */
}
