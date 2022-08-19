package com.example.shopwherev2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private double latitude;
    private double longitude;
    private Object transferData[];
    private GetNearbyPlaces getNearbyPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("ShopWhere");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void requestPermissionsAndProceed() {
        transferData = new Object[2];
        getNearbyPlaces = new GetNearbyPlaces();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }


    private void getCurrentLocation() {
        try {
            if (locationPermissionGranted) {
                Task loc = mFusedLocationProviderClient.getLastLocation();
                loc.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentLocation = (Location) task.getResult();
                            latitude = currentLocation.getLatitude();
                            longitude = currentLocation.getLongitude();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15f));
                        } else {
                            Toast.makeText(MapsActivity.this, "Cannot retrieve location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void findLocation() {
        String searchString = "buy " + getIntent().getStringExtra("predictionText");
//        Toast.makeText(MapsActivity.this, "Searching for: " + searchString, Toast.LENGTH_LONG).show();
//        Geocoder geocoder = new Geocoder(MapsActivity.this);
//        List<Address> list = new ArrayList<>();
//        try {
//            list = geocoder.getFromLocationName("Best Buy", 1);
//        } catch(IOException e) {
//            Log.e("Exception: %s", e.getMessage());
//        }
//        if(list.size() > 0)
//        {
//            Address address = list.get(0);
//            Toast.makeText(MapsActivity.this, "Found: " + address.toString(), Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(MapsActivity.this, "No locations found", Toast.LENGTH_LONG).show();
//        }
        mMap.clear();
        String url = getUrl(latitude, longitude, searchString);
        transferData[0] = mMap;
        transferData[1] = url;

        getNearbyPlaces.execute(transferData);
    }

    private String getUrl(double latitide, double longitude, String nearbyPlace)
    {
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googleURL.append("location=" + latitide + "," + longitude);
        googleURL.append("&radius=" + 10000);
        googleURL.append("&query=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyBm6-b-SgzqteDlcJv4R56sT_180wSCZ5I");

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        requestPermissionsAndProceed();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        findLocation();
    }
}