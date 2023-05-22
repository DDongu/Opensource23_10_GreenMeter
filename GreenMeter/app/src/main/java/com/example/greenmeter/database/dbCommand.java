package com.example.greenmeter.database;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dbCommand extends Application {
    // Get a reference to the Firebase Realtime Database
    private DatabaseReference reference; //실시간 데이터베이스
    private int lastID;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }

    public String GetAutoIncrement(String node) {
        /** AutoIcrement 기능 함수(ID를 자동 생성해서 return 해줌)
         *
         *  사용법)
         *  private dbCommand dbdommand;                                변수 선언
         *  dbcommand = new dbCommand();                                onCreate()에 초기화
         *  String id = dbcommand.GetAutoIncrement("DB테이블이름");       id가 필요한 곳에 변수 선언 후 사용.
         *
         * */
        reference = FirebaseDatabase.getInstance().getReference("GreenMeter/"+node);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long count = dataSnapshot.getChildrenCount();
                    DataSnapshot lastChildSnapshot = null;

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (count == 1) {
                            lastChildSnapshot = childSnapshot;
                            break;
                        }
                        count--;
                    }

                    if (lastChildSnapshot != null) {
                        String lastKey = lastChildSnapshot.getKey();
                        lastID = Integer.parseInt(lastKey);
                        Log.d("Firebase", "Last Key: " + lastKey);
                    } else {
                        Log.d("Firebase", "No child nodes found");
                    }
                } else {
                    lastID = -1;
                    Log.d("Firebase", "No data found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

        return String.valueOf(lastID+1);
    }
}