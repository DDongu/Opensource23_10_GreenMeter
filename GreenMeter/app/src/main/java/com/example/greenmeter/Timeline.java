/**
 * 코드 작성자
 * 김동규 - 초기 맵 설정 및 전반적인 주요 기능 함수 작성
 * 김태현 - 오류 코드 수정
 * */

package com.example.greenmeter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenmeter.database.LocationData;
import com.example.greenmeter.database.dbCommand;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Timeline extends Fragment implements OnMapReadyCallback, LocationListener {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private DatabaseReference mDatabaseRef, mUserAccountRef; //실시간 데이터베이스
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private final int MIN_TIME = 2000;
    private final int MIN_DISTANCE = 5; // 업데이트하는 기준이동거리
    private int zm = 14;
    private dbCommand dbcommand;
    private String userID;
    private CO2Calculation co2Calculation;
    private String carName = "볼보_S90B5";
    private Double CO2num;
    private List<LatLng> pathPoints; // List to store path points
    private LatLng currentLocation;
    private ImageButton timelineDetailButton;
    private TimelineDetail timelineDetail;
    private int N = 5; // 위치정보 N개 가져오기
    private List<LatLng> geoDataList;
    private double day_distance = 0;
    private TextView day_distance_text, day_CO2_text;
    private Double total_distance;
    private Double total_CO2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout and initialize the map
        view = inflater.inflate(R.layout.timeline, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GreenMeter");
//        mUserAccountRef = FirebaseDatabase.getInstance().getReference("GreenMeter").child("UserAccount");
        mFirebaseAuth = FirebaseAuth.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbcommand = new dbCommand();
        co2Calculation = new CO2Calculation();
        pathPoints = new ArrayList<>();
        geoDataList = new ArrayList<>(); // 데이터를 저장할 리스트 생성
        day_distance_text = view.findViewById(R.id.day_km);
        day_CO2_text = view.findViewById(R.id.day_co2);

        // 사용자 ID토큰
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();

        // 타임라인 상세보기 버튼
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
        return view;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 초기 위치 설정
        double initialLatitude = 33.62223869484045; // 사용자의 초기 위도 값
        double initialLongitude = 130.48042941022328; // 사용자의 초기 경도 값
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
        // 비동기 방지 때문에 여기에서 초기화
        mUserAccountRef = FirebaseDatabase.getInstance().getReference("GreenMeter").child("UserAccount").child(userID);

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
        String id;
        Date currentDate = new Date();
        String now = currentDate.toString();

        // DB에 저장할 정보 생성

        id = dbcommand.GetAutoIncrement("GreenMeter/UserLocation/"+userID);
        LocationData location_data = new LocationData();
        location_data.setRecode_time(now);
        location_data.setLat(location.getLatitude());
        location_data.setLng(location.getLongitude());
        location_data.setType_trans(carName);

        // DB에 위치 정보 저장
        mDatabaseRef.child("UserLocation").child(userID).child(id).setValue(location_data);


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

        calcDistance();
    }

    // 좌표 사이 거리구하기
    public double DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2){
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos)/1000;

        return distance;
    }

    public void calcDistance() {
        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "calcDistance() 호출");
        // 위치정보 N개 가져오기
        Query query = mDatabaseRef.child("UserLocation").child(userID).limitToFirst(N);
        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "userID: " + userID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();
                    // 데이터를 HashMap으로 변환하여 가져옵니다.
                    Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "dataMap: " + dataMap);
                    // 데이터를 처리하는 로직을 추가합니다.
                    // 예시: HashMap에서 필요한 값을 추출하여 처리하는 방법은 아래와 같습니다.
                    LatLng latLng = new LatLng((Double) dataMap.get("lat"), (Double) dataMap.get("lng"));
                    Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "data: " + latLng);
                    geoDataList.add(latLng); // 데이터를 리스트에 추가합니다.
                    snapshot.getRef().removeValue();
                }
                Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "geoDataList.size(): " + geoDataList.size());
                Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "geoDataList: " + geoDataList);

                // 계산 및 DB 처리 코드
                if(geoDataList.size() == N) {
                    for(int i=0; i<N-1; i++) {
                        day_distance = DistanceByDegreeAndroid(geoDataList.get(i).latitude, geoDataList.get(i).longitude, geoDataList.get(i + 1).latitude, geoDataList.get(i + 1).longitude);
                        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "day_distance: " + day_distance);
                    }
                    for(int i=0; i<N-1; i++) {
                        geoDataList.remove(0);
                    }

                    // DB에서 total_distance & total_carbonEm불러오기
                    // DB에서 total_distance & total_carbonEm불러오기
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    dbCommand dbcommand = new dbCommand();
                    dbcommand.getTotalkmFromDatabase(userId, new dbCommand.OnTotalkmRetrievedListener() {
                        @Override
                        public void onTotalkmRetrieved(Double totalkm) {
                            total_distance = totalkm;
                            Log.d("ㄷㄷㄷㄷㄷㄷㄷ", "total_distance: " + total_distance);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("Firebase", "Error: " + errorMessage);
                        }
                    });
                    dbcommand.getTotalco2FromDatabase(userId, new dbCommand.OnTotalco2RetrievedListener() {
                        @Override
                        public void onTotalco2Retrieved(Double totalco2) {
                            total_CO2 = totalco2;
                            Log.d("ㄷㄷㄷㄷㄷㄷㄷ", "total_CO2: " + total_CO2);
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Log.e("Firebase", "Error: " + errorMessage);

                        }
                    });

                    if (total_distance == null & total_CO2 == null) {
                        total_distance = 0.0;
                        total_CO2 = 0.0;
                    }

                    // 거리계산 및 DB에 저장
                    Double tmp_distance = Math.round(day_distance*1000)/1000.0;
                    Double tmp_CO2 = Math.round(day_distance*CO2num*10)/10.0;
                    total_distance += tmp_distance;
                    total_CO2 += tmp_CO2;

                    day_distance_text.setText(Double.toString(Math.round(total_distance*1000)/1000.0));
                    day_CO2_text.setText(Double.toString(Math.round(total_CO2*10)/10.0));

                    // DB에 위치 정보 저장
                    mDatabaseRef.child("UserAccount").child(userID).child("total_distance").setValue(Math.round(total_distance*1000)/1000.0);
                    mDatabaseRef.child("UserAccount").child(userID).child("total_carbonEm").setValue(Math.round(total_CO2*10)/10.0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
            }
        });
    }
}