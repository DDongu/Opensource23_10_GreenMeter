package com.example.greenmeter;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.greenmeter.database.dbCommand;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
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
import com.google.maps.DirectionsApiRequest;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Greenmeter extends Fragment implements OnMapReadyCallback {
    private dbCommand dbcommand;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private GoogleMap mMap;
    private MapView mapView;
    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    private LatLng currentLocation;
    private LatLng startLocation, destinationLocation;

    private EditText startEditText, destinationEditText;
    private TextView kmtextCar, kmtextMotorcycle, CO2textCar, CO2textMotorcycle;
    private Button carButton, transitButton;
    private ImageButton resetLocatonButton, swapLocationButton;
    private String carName = "BMW_320d";
    private Double CO2num;
    private Double totalCO2;


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

        // 탄소배출량 가져오기
        dbCommand.CO2numCallback callback = new dbCommand.CO2numCallback() {
            @Override
            public void onCO2numRetrieved(Double result) {
                // Handle the retrieved CO2num value
                CO2num = result;
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle the error
            }
        };
        dbCommand.GetCO2num(carName, callback);

        // 목적지 입력과 경로 표시를 위한 뷰 초기화
        startEditText = view.findViewById(R.id.start_edit_text);
        destinationEditText = view.findViewById(R.id.destination_edit_text);
        kmtextCar = view.findViewById(R.id.car_km_text);
        kmtextMotorcycle = view.findViewById(R.id.motorcycle_km_text);
        CO2textCar = view.findViewById(R.id.car_emission_co2);
        CO2textMotorcycle = view.findViewById(R.id.motorcycle_emission_co2);
        carButton = view.findViewById(R.id.car_button);
        transitButton = view.findViewById(R.id.transit_button);
        resetLocatonButton = view.findViewById(R.id.reset_location_button);
        swapLocationButton = view.findViewById(R.id.swap_location_button);

        // 경로 표시 버튼 클릭 리스너 등록
        carButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.carxml, 0, 0, 0);
                carButton.setPadding(15, 0, 0, 0);
                carButton.setBackground(getResources().getDrawable(R.drawable.radius));
                showRoute(TravelMode.DRIVING, carButton, CO2textCar, kmtextCar);
            }
        });

        transitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.motorcyclexml, 0, 0, 0);
                transitButton.setPadding(15, 0, 0, 0);
                transitButton.setBackground(getResources().getDrawable(R.drawable.radius));
                showRoute(TravelMode.BICYCLING, transitButton, CO2textMotorcycle, kmtextMotorcycle);
            }
        });

        resetLocatonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEditText.setText("");
                destinationEditText.setText("");
            }
        });

        swapLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = startEditText.getText().toString();
                startEditText.setText(destinationEditText.getText().toString());
                destinationEditText.setText(temp);
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
        double initialLatitude = 35.71287783280877; // 사용자의 초기 위도 값
        double initialLongitude = 139.76191389495995; // 사용자의 초기 경도 값
        float DEFAULT_ZOOM = 15.0f; // 지도의 초기 줌 레벨

        LatLng initialLocation = new LatLng(initialLatitude, initialLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, DEFAULT_ZOOM));

        // 현재 위치 표시 설정
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == PERMISSION_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates();
//            } else {
//                // 권한이 거부되었을 때 처리
//            }
//        }
//    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(true)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void showRoute(TravelMode travelMode, Button clickedButton, TextView clickedView, TextView kmtext) {
        String start = startEditText.getText().toString();
        String destination = destinationEditText.getText().toString();

        // 목적지 위치 검색을 위한 Geocoding API 등을 사용하여 주소를 좌표로 변환
        /* 출발지 */
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(start, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                startLocation = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 도착지 */
        try {
            List<Address> addressList = geocoder.getFromLocationName(destination, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                destinationLocation = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 지도가 준비되었을 때 경로 표시 */
        if (mMap != null) {
            showRouteOnMap(travelMode, clickedButton, clickedView, kmtext);
        }
    }

    private DirectionsResult getDirectionsResult(DirectionsApiRequest apiRequest) throws ApiException {
        try {
            return apiRequest.await();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showRouteOnMap(TravelMode travelMode, Button clickedButton, TextView clickedView, TextView kmtext) {
        if(startLocation == null || destinationLocation == null) {
            return;
        }
        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "showRouteOnMap() called WITH " + travelMode.toString() + " MODE");
        // 출발지와 목적지의 좌표를 문자열로 변환
        String start = startLocation.latitude + "," + startLocation.longitude;
        String destination = destinationLocation.latitude + "," + destinationLocation.longitude;
        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "showRouteOnMap: origin= "  + start + ", destination= " + destination);

        // API 요청을 보내고 응답을 처리합니다.
        DirectionsApiRequest apiRequest = DirectionsApi.newRequest(getGeoContext())
                .mode(travelMode)
                .origin(start)
                .destination(destination);

        DistanceMatrixApiRequest matrixRequest = DistanceMatrixApi.newRequest(getGeoContext())
                .mode(travelMode)
                .origins(start)
                .destinations(destination);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    DirectionsResult result = getDirectionsResult(apiRequest);
                    Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "onResult: result=" + result);
                    if (result != null) {
                        // API 응답에서 경로 정보를 추출합니다.
                        Iterable<LatLng> points = extractRoutePoints(result);
                        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "onResult: points=" + points);

                        DistanceMatrix distanceMatrix = getDistanceMatrix(matrixRequest);
                        DistanceMatrixRow[] rows = distanceMatrix.rows;
                        if (rows != null && rows.length > 0) {
                            DistanceMatrixElement[] elements = rows[0].elements;
                            if (elements != null && elements.length > 0) {
                                Distance distance = elements[0].distance;
                                String distanceString = distance.humanReadable;
                                Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "onResult: distanceString=" + distanceString);
                                String[] distanceNUMstr = distanceString.split(" ");
                                Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "onResult: distanceNUMstr=" + distanceNUMstr[0] + ".");
                                Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "CO2num=" + CO2num);
                                totalCO2 = Double.parseDouble(distanceNUMstr[0]) * CO2num;
                                Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "totalCO2=" + totalCO2);

                                // 경로간 거리 업데이트
                                if (clickedButton == carButton) {
                                    clickedButton.setText(Double.toString(totalCO2) + " g/km");
                                    clickedView.setText(Double.toString(totalCO2));
                                    kmtext.setText(distanceNUMstr[0]);
                                } else {
                                    clickedButton.setText("0 g/km");
                                    clickedView.setText("0");
                                    kmtext.setText(distanceNUMstr[0]);
                                }
                            }
                        }

                        int linecolor = 0;
                        if (clickedButton == carButton) {
                            linecolor = Color.BLUE;
                        } else {
                            linecolor = Color.RED;
                        }
                        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "points: " + points);
                        // Polyline을 생성하고 지도에 추가합니다.
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .addAll(points)
                                .color(linecolor)
                                .width(10f);
                        Polyline polyline = mMap.addPolyline(polylineOptions);
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }, 1); //딜레이 타임 조절




        // 출발지와 목적지 사이의 경로를 가져오는 API 요청
        // API 키를 사용하여 Google Maps Directions API에 요청을 보냅니다.
        // 요청에는 출발지와 목적지의 좌표를 전달해야 합니다.

        // API 응답을 받아와서 파싱하여 경로를 가져옵니다.
        // 응답에서 경로 정보를 추출하고, 출발지와 목적지 사이의 좌표 리스트를 생성합니다.

        // 출발지와 목적지를 지도에 마커로 표시합니다
        mMap.addMarker(new MarkerOptions().position(startLocation).title("출발지"));
        mMap.addMarker(new MarkerOptions().position(destinationLocation).title("목적지"));

        // 출발지와 목적지가 표시된 지도 영역으로 이동합니다
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startLocation);
        builder.include(destinationLocation);
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));


    }

    private List<LatLng> extractRoutePoints(DirectionsResult directionsResult) {
        List<LatLng> points = new ArrayList<>();
        if (directionsResult.routes != null && directionsResult.routes.length > 0) {
            DirectionsRoute route = directionsResult.routes[0];
            if (route.legs != null && route.legs.length > 0) {
                DirectionsLeg leg = route.legs[0];
                if (leg.steps != null && leg.steps.length > 0) {
                    for (DirectionsStep step : leg.steps) {
                        EncodedPolyline encodedPolyline = step.polyline;
                        List<com.google.maps.model.LatLng> decodedPolyline = encodedPolyline.decodePath();
                        for (com.google.maps.model.LatLng decodedLatLng : decodedPolyline) {
                            LatLng latLng = new LatLng(decodedLatLng.lat, decodedLatLng.lng);
                            points.add(latLng);
                        }
                    }
                }
            }
        }
        return points;
    }

    private DistanceMatrix getDistanceMatrix(DistanceMatrixApiRequest request) {
        try {
            return request.await();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyABsH0QiTkHB5AH9DQXtM4Dov4-AJVFYJo")
                .build();
        return geoApiContext;
    }
}