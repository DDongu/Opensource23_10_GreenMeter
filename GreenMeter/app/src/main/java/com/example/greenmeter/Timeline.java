package com.example.greenmeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.greenmeter.database.dbCommand;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class Timeline extends Fragment implements OnMapReadyCallback, LocationListener {
    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private final int MIN_TIME = 2000;
    private final int MIN_DISTANCE = 5; // 업데이하는 기준이동거리
    private int zm = 14;
    private dbCommand dbcommand;
    private String userID;
    private CO2Calculation co2Calculation;
    private String carName = "BMW_320d";
    private List<LatLng> pathPoints; // List to store path points
    private LatLng currentLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout and initialize the map
        view = inflater.inflate(R.layout.timeline, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Initialize the FusedLocationProviderClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        client = LocationServices.getFusedLocationProviderClient(requireContext());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    // 위치 업데이트 처리
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        };


        // Initialize the LocationManager
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GreenMeter");
        dbcommand = new dbCommand();
        co2Calculation = new CO2Calculation();

        pathPoints = new ArrayList<>(); // Initialize pathPoints

        return view;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in CBNU
        LatLng myLocation = new LatLng(36.6283933, 127.459223);

        mMap.addMarker(new MarkerOptions()
                .position(myLocation)
                .title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, zm));

    }

    @Override
    public void onResume() {
        super.onResume();
        // Request location updates when the fragment is resumed
        startLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop location updates when the fragment is stopped
        stopLocationUpdates();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request the permission
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission is granted, start location updates
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        }
    }

    private void stopLocationUpdates() {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start location updates
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Update map with the new location
        updateMapWithLocation(location);
    }

    private void updateMapWithLocation(Location location) {
        if (mMap != null) {
            // Get the current location
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            pathPoints.add(currentLocation); // Add currentLocation to pathPoints list

            // Draw polyline connecting all the path points
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.GREEN)
                    .width(5)
                    .addAll(pathPoints);
            mMap.addPolyline(polylineOptions);

            // Move the camera to the current location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }

    // ... Rest of the code
}