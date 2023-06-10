package com.example.greenmeter.database;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class dbCommand extends Application {
    // Get a reference to the Firebase Realtime Database
    private static DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private FirebaseUser user;
    private int lastID;
    private String idToken;
    private Double CO2num;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    public String GetAutoIncrement(String node) {
        /** AutoIcrement 기능 함수(ID를 자동 생성해서 return 해줌)
         *
         *  사용법)
         *  private dbCommand dbcommand;                                변수 선언
         *  dbcommand = new dbCommand();                                onCreate()에 초기화
         *  String id = dbcommand.GetAutoIncrement("DB테이블이름");       id가 필요한 곳에 변수 선언 후 사용.
         *
         * */
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(node);
        Log.d("Debugging", "node: " + node);

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
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
                        Log.d("Debugging", "Last Key: " + lastKey);
                    } else {
                        lastID = -1;
                        Log.d("Firebase", "No child nodes found");
                    }
                } else {
                    Log.d("Firebase", "No data found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });
        Log.d("Debugging", "lastID: " + lastID);

        return String.valueOf(lastID+1);
    }


    public interface OnNameRetrievedListener { // 새로 추가된 인터페이스
        void onNameRetrieved(String name);
        void onFailure(String errorMessage);
    }

    public void getNameFromDatabase(String userId, OnNameRetrievedListener listener) { // 새로 추가된 메서드

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GreenMeter").child("UserAccount").child(userId);
        mDatabaseRef.child("username").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String username = dataSnapshot.getValue(String.class);
                        listener.onNameRetrieved(username);

                    } else {
                        listener.onFailure("username not found");
                    }
                } else {
                    String errorMessage = task.getException().getMessage();
                    Log.e("Firebase", "Error: " + errorMessage);
                    listener.onFailure(errorMessage);
                }
            }
        });
    }

    public interface OnNicknameRetrievedListener { // 새로 추가된 인터페이스
        void onNicknameRetrieved(String nickname);
        void onFailure(String errorMessage);
    }

    public void getNicknameFromDatabase(String userId, OnNicknameRetrievedListener listener) { // 새로 추가된 메서드

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GreenMeter").child("UserAccount").child(userId);
        mDatabaseRef.child("nickname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String nickname = dataSnapshot.getValue(String.class);
                        listener.onNicknameRetrieved(nickname);
                        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "db 내의 nickname: " + nickname);

                    } else {
                        listener.onFailure("Nickname not found");
                    }
                } else {
                    String errorMessage = task.getException().getMessage();
                    Log.e("Firebase", "Error: " + errorMessage);
                    listener.onFailure(errorMessage);
                }
            }
        });
    }

    public interface CO2numCallback {
        void onCO2numRetrieved(Double CO2num);
        void onFailure(String errorMessage);
    }

    public static void GetCO2num(String carName, CO2numCallback callback) {
        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "GetCO2num 시작");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Transportation");
        mDatabaseRef.child(carName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot != null) {
                        Log.d("firebase", String.valueOf(dataSnapshot.getValue()));
                        Double CO2num = Double.parseDouble(String.valueOf(dataSnapshot.getValue()));
                        Log.d("ㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷㄷ", "db 내의 CO2num: " + CO2num);
                        callback.onCO2numRetrieved(CO2num);
                    }
                } else {
                    Log.e("firebase", "Error getting data", task.getException());
                    callback.onFailure("Error getting data");
                }
            }
        });
    }
}