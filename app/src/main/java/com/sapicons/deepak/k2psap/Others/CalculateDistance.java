package com.sapicons.deepak.k2psap.Others;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Deepak Prasad on 04-10-2018.
 */

public class CalculateDistance {
    String TAG = "CALC_DIST";
    Context context;
    public CalculateDistance(Context context) {
        this.context = context;
    }
    public double distanceInKM(double lat1, double long1){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        double lat2 = sharedPreferences.getFloat("latitude", 0);
        double long2 = sharedPreferences.getFloat("longitude", 0);


        if (lat2 == 0 && long2 == 0) {
            UserLocation userLocation = new UserLocation(context);
            LocationManager locationManager = userLocation.getLocationManager();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            lat2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude();
            long2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude();
        }
        Location location1 = new Location("Location1");
        location1.setLatitude(lat1);
        location1.setLongitude(long1);

        Location location2 = new Location("Location2");
        location2.setLongitude(long2);
        location2.setLatitude(lat2);

        Log.d(TAG,"LOC1: "+location1);
        Log.d(TAG,"LOC2: "+location2);

        double distanceInKMeters = location1.distanceTo(location2) / 1000.0;


        return  distanceInKMeters;
    }

    public boolean isNearby(double distanceInKm){


        if(distanceInKm <= getRadiusInKM())
            return true;
        else return false;
    }

    public double getRadiusInKM(){

        // get distance from SharedPreferences
        // TODO
        return 5.0;
    }


    /*
    public double distance(double lat1, double long1, double lat2, double long2){
        double theta  = long1 - long2;
        double dist = Math.sin(degToRad(lat1))
                * Math.sin(degToRad(lat2))
                + Math.cos(degToRad(lat1))
                * Math.cos(degToRad(lat2))
                * Math.cos(degToRad(theta));

        dist = Math.acos(dist);
        dist = radToDeg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double degToRad(double deg){
        return (deg * (180.0 / Math.PI));
    }

    private double radToDeg(double rad){
        return ( rad * ( Math.PI / 180.0));
    }
    */
}