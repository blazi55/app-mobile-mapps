package com.example.mapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivitySecond extends AppCompatActivity implements  SeekBar.OnSeekBarChangeListener,
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    //Initialize Variable
    private String name;
    private GoogleMap gmp;
    private CheckBox checkBox;
    private SeekBar seekRed, seekGreen, seekBlue;
    private Button btDraw, btClear, btFind;
    private Polygon polygon = null;
    private List<LatLng> latLngList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private LocationListener locationListener;
    private LocationManager locationManager;
    private final long MIN_TIME = 1000;
    private final long Min_DISTANCE = 5;
    private FloatingActionButton floatingActionButton;
    private FusedLocationProviderClient fusedLocationProviderClient;

    int red=0, green=0, blue=0;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign Variable
        checkBox = findViewById(R.id.check_box);
        seekRed = findViewById(R.id.seek_red);
        seekGreen = findViewById(R.id.seek_green);
        seekBlue = findViewById(R.id.seek_blue);
        btDraw = findViewById(R.id.bt_draw_polygon);
        btClear = findViewById(R.id.bt_draw_polyline);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        //Initialize SupportMapFragment
        initMap();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PackageManager.PERMISSION_GRANTED);

        fusedLocationProviderClient = new FusedLocationProviderClient(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLoc();
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //Get CheckBox State
                if (b) {
                    if (polygon == null) return;
                    //Fill Polygon Color
                    polygon.setFillColor(Color.rgb(red, green, blue));
                } else {
                    //UnFill Polygon Color if unchecked
                    polygon.setFillColor(Color.TRANSPARENT);

                }
            }
        });

        btDraw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Draw Polyline on Map
                if (polygon != null) {
                    polygon.remove();
                }
                //Create PolygonOptions
                PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList)
                        .clickable(true);
                polygon = gmp.addPolygon(polygonOptions);
                //Set Polygon Stroke Color
                polygon.setStrokeColor(Color.rgb(red, green, blue));
                if (checkBox.isChecked()) {
                    //Fill Polygon Color
                    polygon.setFillColor(Color.rgb(red, green, blue));
                }
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Clear All
                if (polygon != null) {
                    polygon.remove();
                }
                for (Marker marker: markerList) marker.remove();
                latLngList.clear();
                markerList.clear();
                checkBox.setChecked(false);
                seekBlue.setProgress(0);
                seekGreen.setProgress(0);
                seekRed.setProgress(0);
            }
        });

        seekRed.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);

//        btFind.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLoc() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                Location location = task.getResult();
                gotoLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f);
        gmp.addMarker(new MarkerOptions().position(latLng).title("My Position"));
        gmp.moveCamera(cameraUpdate);
        gmp.setMapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gmp = googleMap;
        LatLng wngig = new LatLng(52.46409983438716, 16.94205167330793);
        gmp.moveCamera(CameraUpdateFactory.newLatLngZoom(wngig, 17f));
        gmp.moveCamera(CameraUpdateFactory.newLatLng(wngig));
        //gmp.setMyLocationEnabled(true);

//        locationListener = new LocationListener() {
//
//            @Override
//            public void onLocationChanged (@NonNull Location location) {
//                try {
//                    if (latLng_point == null) {
//                        latLng_point = new LatLng(location.getLatitude(), location.getLongitude());
//                        gmp.addMarker(new MarkerOptions().position(latLng_point).title("My Position"));
//                        gmp.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng_point, 17f));
//                        gmp.moveCamera(CameraUpdateFactory.newLatLng(latLng_point));
//                    } else {
//                        latLng_point = new LatLng(location.getLatitude(), location.getLongitude());
//                        gmp.addMarker(new MarkerOptions().position(latLng_point).title("My Position"));
//                        gmp.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng_point, 17f));
//                        gmp.moveCamera(CameraUpdateFactory.newLatLng(latLng_point));
//                    }
//
//                } catch (SecurityException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        };

        gmp.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                //Create MarkerOption
                //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_ikona_apki);
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);//.icon(icon);
                //Create Marker
                Marker marker = gmp.addMarker(markerOptions);
                //Add LanLng and Marker
                latLngList.add(latLng);
                markerList.add(marker);
            }
        });
//
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        try {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, Min_DISTANCE,
//                    locationListener);
//        } catch(SecurityException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seek_red:
                red = i;
                break;
            case R.id.seek_blue:
                blue = i;
                break;
            case R.id.seek_green:
                green = i;
                break;
        }
        if (polygon != null) {
            //Set Polygon Stroke Color
            polygon.setStrokeColor(Color.rgb(red, green, blue));
            if (checkBox.isChecked())
                //Fill Polygon Color
                polygon.setFillColor(Color.rgb(red, green, blue));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}