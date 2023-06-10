/**
 * 코드 작성자
 * 김동민 - 전체 코드 작성
 * */

package com.example.greenmeter;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.greenmeter.database.LocationData;
import com.example.greenmeter.database.TimelineData;
import com.example.greenmeter.database.dbCommand;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;

public class CO2Calculation extends Application {
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private DatabaseReference mDatabaseRef2; //실시간 데이터베이스
    private FirebaseUser firebaseUser;
    private dbCommand dbcommand;

    private String userID;
    private static Double[] lat = new Double[2];
    private static Double[] lng = new Double[2];
    private String[] time = new String[2];
    private String trans;
    private int i = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = mFirebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GreenMeter/UserLocation/"+userID);
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("GreenMeter/UserTimeline/"+userID);
        dbcommand = new dbCommand();


    }

    public void setTimelineData(String locationID, String userID) {
        getLocationData(String.valueOf(Integer.parseInt(locationID) - 1));
        String timelineID = dbcommand.GetAutoIncrement("GreenMeter/UserTimeline/"+userID);
        LocalDate today = LocalDate.now();

        TimelineData timeline_data = new TimelineData();
        timeline_data.setRecode_date(String.valueOf(today));
        timeline_data.setMv_time(5.0);                              // 시간은 나중에 제대로 넣기
        timeline_data.setMv_distance(calculateDistance());
        timeline_data.setMv_trans(trans);
        timeline_data.setCarbonEm(11.0);                            // 나중에 탄소배출량 계산하기

        mDatabaseRef2.child(timelineID).setValue(timeline_data);
    }

    public static double calculateDistance() {
        Location location1 = new Location("");
        location1.setLatitude(lat[0]);
        location1.setLongitude(lng[0]);

        Location location2 = new Location("");
        location2.setLatitude(lat[1]);
        location2.setLongitude(lng[1]);

        return location1.distanceTo(location2);
    }



    public void getLocationData(String id) {
//        ValueEventListener postListner = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//                LocationData data = dataSnapshot.getValue(LocationData.class);
//                lat[i] = data.getLat();
//                lng[i] = data.getLng();
//                time[i] = data.getRecode_time();
//                trans = data.getType_trans();
//                Log.d("Debugging", "lat["+i+"] : " + String.valueOf(lat[i]));
//                Log.d("Debugging", "lng["+i+"] : " + String.valueOf(lng[i]));
//                Log.d("Debugging", "time["+i+"] : " + time[i]);
//                Log.d("Debugging", "trans : " + trans);
//
//                i = i % 2;
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//            }
//        };

        Log.d("Debugging", "id : " + id);
//            mDatabaseRef.child(id).addValueEventListener(postListner);
//            mDatabaseRef.child(String.valueOf(Integer.parseInt(id) - 1)).addValueEventListener(postListner);

        mDatabaseRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LocationData data = snapshot.getValue(LocationData.class);
                lat[0] = data.getLat();
                lng[0] = data.getLng();
                time[0] = data.getRecode_time();
                trans = data.getType_trans();
                Log.d("Debugging", "lat["+0+"] : " + String.valueOf(lat[0]));
                Log.d("Debugging", "lng["+0+"] : " + String.valueOf(lng[0]));
                Log.d("Debugging", "time[\"+0+\"] : " + time[0]);
                Log.d("Debugging", "trans : " + trans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });

        mDatabaseRef.child(String.valueOf(Integer.parseInt(id) - 1)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LocationData data = snapshot.getValue(LocationData.class);
                lat[1] = data.getLat();
                lng[1] = data.getLng();
                time[1] = data.getRecode_time();
                trans = data.getType_trans();
                Log.d("Debugging", "lat["+1+"] : " + String.valueOf(lat[1]));
                Log.d("Debugging", "lng["+1+"] : " + String.valueOf(lng[1]));
                Log.d("Debugging", "time["+1+"] : " + time[1]);
                Log.d("Debugging", "trans : " + trans);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
        // 데이터 처리 후 사용데이터 삭제
//        mDatabaseRef.child("1").removeValue();
    }

}
