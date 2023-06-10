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
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenmeter.database.dbCommand;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class Timeline extends Fragment implements OnMapReadyCallback, LocationListener {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private final int MIN_TIME = 2000;
    private final int MIN_DISTANCE = 5; // 업데이트하는 기준이동거리
    private int zm = 14;
    private dbCommand dbcommand;
    private String userID;
    private CO2Calculation co2Calculation;
    private String carName = "BMW_320d";
    private List<LatLng> pathPoints; // List to store path points
    private LatLng currentLocation;
    private ImageButton timelineDetailButton;
    private TimelineDetail timelineDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout and initialize the map
        view = inflater.inflate(R.layout.timeline, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // 위치 업데이트 요청
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

        timelineDetail = new TimelineDetail();
        timelineDetailButton = view.findViewById(R.id.timeline_detail_btn);
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        timelineDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the new fragment
                ft.replace(R.id.main_frame, timelineDetail);

                // Optional: Add the transaction to the back stack
                ft.addToBackStack("TimelineMain");

                // Commit the transaction
                ft.commit();
            }
        });

        return view;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 초기 위치 설정
        double initialLatitude = 36.6283933; // 사용자의 초기 위도 값
        double initialLongitude = 127.459223; // 사용자의 초기 경도 값
        float DEFAULT_ZOOM = 15.0f; // 지도의 초기 줌 레벨

        LatLng initialLocation = new LatLng(initialLatitude, initialLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, DEFAULT_ZOOM));

        // 현재 위치 표시 설정 버튼
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        startLocationUpdates();
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

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

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
                    .width(10f)
                    .addAll(pathPoints);
            mMap.addPolyline(polylineOptions);

            // Move the camera to the current location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }
}