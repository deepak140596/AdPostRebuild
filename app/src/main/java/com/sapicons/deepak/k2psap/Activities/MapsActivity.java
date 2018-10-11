package com.sapicons.deepak.k2psap.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sapicons.deepak.k2psap.Others.UserLocation;
import com.sapicons.deepak.k2psap.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class MapsActivity extends FragmentActivity //implements OnMapReadyCallback {
{

    private GoogleMap mMap;
    private FancyButton saveLocationBtn;

    String TAG = "MAPS_ACTIVITY";

    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        createPlacePicker();



    }


    public void createPlacePicker(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            Log.d(TAG,"opening startActivityforResult");
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(savedLocation.getLatitude(),savedLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(position).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }
    */


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, this);
                LatLng latLng = place.getLatLng();
                Log.d(TAG,"LatLng: "+latLng);
                saveLocation(latLng);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveLocation(LatLng latLng){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        float lat = (float)latLng.latitude;
        float lon = (float)latLng.longitude;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("latitude",lat);
        editor.putFloat("longitude",lon);
        editor.putBoolean("isLocationManual",true);
        editor.apply();
        editor.commit();

        finish();
    }


}
