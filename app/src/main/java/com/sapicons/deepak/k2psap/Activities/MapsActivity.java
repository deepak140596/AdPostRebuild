package com.sapicons.deepak.k2psap.Activities;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sapicons.deepak.k2psap.Objects.User;
import com.sapicons.deepak.k2psap.Others.UserLocation;
import com.sapicons.deepak.k2psap.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    String TAG = "MAPS_ACTIVITY";


    LatLng postCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        float latitude = intent.getFloatExtra("latitude",0);
        float longitude = intent.getFloatExtra("longitude",0);
        postCoordinates = new LatLng(latitude,longitude);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Marker marker=null;

        // Add a marker in Sydney and move the camera
        LatLng position = postCoordinates;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 15);
        mMap.animateCamera(cameraUpdate);
        if(marker != null)
            marker.remove();
        mMap.addMarker(new MarkerOptions().position(position));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }


}
