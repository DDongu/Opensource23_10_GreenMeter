package com.example.greenmeter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Greenmeter extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private GoogleMap mMap;
    private MapView mapView;
    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private LatLng destinationLocation;

    private EditText destinationEditText;
    private Button showRouteButton;
    private TextView routeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.greenmeter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

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

        // 목적지 입력과 경로 표시를 위한 뷰 초기화
        destinationEditText = view.findViewById(R.id.destination_edit_text);
        showRouteButton = view.findViewById(R.id.show_route_button);
        routeTextView = view.findViewById(R.id.route_text_view);

        // 경로 표시 버튼 클릭 리스너 등록
        showRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoute();
            }
        });

        // 권한 요청
        checkPermission();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // 초기 위치 설정
        double initialLatitude = 36.6283933; // 사용자의 초기 위도 값
        double initialLongitude = 127.459223; // 사용자의 초기 경도 값
        float DEFAULT_ZOOM = 15.0f; // 지도의 초기 줌 레벨

        LatLng initialLocation = new LatLng(initialLatitude, initialLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, DEFAULT_ZOOM));

        // 현재 위치 표시 설정
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        // 목적지 위치가 설정되어 있으면 경로를 표시
        if (destinationLocation != null) {
            showRouteOnMap();
        }

        startLocationUpdates();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // 권한이 거부되었을 때 처리
            }
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void showRoute() {
        String destination = destinationEditText.getText().toString();

        // 목적지 위치 검색을 위한 Geocoding API 등을 사용하여 주소를 좌표로 변환
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(destination, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                destinationLocation = new LatLng(address.getLatitude(), address.getLongitude());

                // 지도가 준비되었을 때 경로 표시
                if (mMap != null) {
                    showRouteOnMap();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRouteOnMap() {
        // 출발지와 목적지 사이의 경로를 가져오는 API 요청
        // API 키를 사용하여 Google Maps Directions API에 요청을 보냅니다.
        // 요청에는 출발지와 목적지의 좌표를 전달해야 합니다.

        // API 응답을 받아와서 파싱하여 경로를 가져옵니다.
        // 응답에서 경로 정보를 추출하고, 출발지와 목적지 사이의 좌표 리스트를 생성합니다.
        List<LatLng> points = getRoutePointsFromApiResponse();

        // Polyline을 생성하고 지도에 추가합니다.
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(points)
                .color(Color.BLUE)
                .width(5f);
        Polyline polyline = mMap.addPolyline(polylineOptions);

        // 출발지와 목적지를 지도에 마커로 표시합니다
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("출발지"));
        mMap.addMarker(new MarkerOptions().position(destinationLocation).title("목적지"));

        // 출발지와 목적지가 표시된 지도 영역으로 이동합니다
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(currentLocation);
        builder.include(destinationLocation);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    private List<LatLng> getRoutePointsFromApiResponse() {
        List<LatLng> points = new ArrayList<>();

        // Create a GeoApiContext instance with your Google Maps API key
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAumVtgc-UEhK8yO71VmCahWNdtsS_L6xo")
                .build();

        // Define the directions request
        com.google.maps.model.LatLng origin = new com.google.maps.model.LatLng(currentLocation.latitude, currentLocation.longitude);
        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(destinationLocation.latitude, destinationLocation.longitude);

        try {
            // Execute the directions request
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .mode(TravelMode.DRIVING)
                    .origin(origin)
                    .destination(destination)
                    .await();

            // Get the first route from the result
            DirectionsRoute route = result.routes[0];

            // Extract the polyline points from the route
            com.google.maps.model.LatLng[] routePoints = route.overviewPolyline.decodePath().toArray(new com.google.maps.model.LatLng[0]);

            // Convert the route points to LatLng and add them to the points list
            for (com.google.maps.model.LatLng routePoint : routePoints) {
                points.add(new LatLng(routePoint.lat, routePoint.lng));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        }

        return points;
    }
}