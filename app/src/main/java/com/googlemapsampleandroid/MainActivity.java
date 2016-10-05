package com.googlemapsampleandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final int ACCESS_FINE_LOCATION_REQUEST_CODE = 100;
    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 101;
    private static final int REQUEST_CODE_GOOGLE_MAPS_NAVIGATION = 102;
    private final String LOG_TAG = this.getClass().getSimpleName();
    private GoogleMap googleMap;
    private TextView textView_dropLocation;
    private FloatingActionButton floatingActionButton_navigate;
    // IFFCO Chowk Metro Station, Sector 29, Gurgaon, Haryana 28.472165,77.0703233
    private LatLng latLng_HUDA = new LatLng(28.4592693, 77.0724192);
    private CameraPosition cameraPosition_HudaCityCenterGurgaon =
            new CameraPosition.Builder()
                    .target(latLng_HUDA)
                    .zoom(15.5f)
                    .bearing(300)
                    .tilt(50)
                    .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();
    }

    private void initializeView() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_googleMap);
        supportMapFragment.getMapAsync(this);

        textView_dropLocation = (TextView) findViewById(R.id.textView_dropLocation);
        floatingActionButton_navigate = (FloatingActionButton) findViewById(R.id.floatingActionButton_navigate);
        floatingActionButton_navigate.setOnClickListener(this);
        Snackbar.make(textView_dropLocation, "Can't find Google Maps App for Navigation!",
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Log.v(LOG_TAG, "in onMapReady()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v(LOG_TAG, "both PERMISSION_DENIED");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_REQUEST_CODE);
            return;
        }
        Log.v(LOG_TAG, "both PERMISSION_GRANTED");
        this.googleMap.setMyLocationEnabled(true);

        if (textView_dropLocation != null) {
            moveMapCameraTo_HudaCityCenterGurgaon();
        }
    }

    private void moveMapCameraTo_HudaCityCenterGurgaon() {
        String strDropLocationName = "HUDA City Center Metro Station, Gurgaon, Haryana";
        Log.v(LOG_TAG, "in moveMapCameraTo_HudaCityCenterGurgaon()");
        textView_dropLocation.setText(strDropLocationName);
        googleMap.addMarker(new MarkerOptions().position(latLng_HUDA).title(strDropLocationName));

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition_HudaCityCenterGurgaon));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(LOG_TAG, "in onRequestPermissionsResult() requestCode:" + requestCode + " , permissions.length:"
                + permissions.length + " ,grantResults.length" + grantResults.length);

        for (int i = 0; i < permissions.length; i++) {
            Log.v(LOG_TAG, "permission requested: " + permissions[i] + "grantResults: " + grantResults[i]);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton_navigate:

                startNavigationInGoogleMapsIntentWithLatLng(latLng_HUDA.latitude, latLng_HUDA.longitude);
                break;
        }
    }

    private void startNavigationInGoogleMapsIntentWithLatLng(double latitude, double longitude) {

        // src: https://developers.google.com/maps/documentation/android-api/intents
        //Uri.parse("geo:37.7749,-122.4194(myLabel)?q=" + Uri.encode("1st & Pike, Seattle")");
        Uri uri = Uri.parse("google.navigation:q=" + latitude + "," + longitude + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(mapIntent, REQUEST_CODE_GOOGLE_MAPS_NAVIGATION, null);
        } else {
            Log.e(LOG_TAG, "mapIntent.resolveActivity(getPackageManager()) returns null");
            Snackbar.make(textView_dropLocation, "Can't find Google Maps App for Navigation!",
                    Snackbar.LENGTH_LONG).show();
        }
    }

}