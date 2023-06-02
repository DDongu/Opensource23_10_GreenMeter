package com.example.greenmeter.database;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class dbCommand extends Application {
    // Get a reference to the Firebase Realtime Database
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private FirebaseUser user;
    private int lastID;
    private String idToken;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
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
//
//    public String getCurrentUserToken() {
//        user = FirebaseAuth.getInstance().getCurrentUser();
//        if(user != null) {
//            Toast.makeText(this, "로그인부터 하세요", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "DEBUG: 사용자 정보 가져오기 성공!", Toast.LENGTH_SHORT).show();
//        }
//
//        user.getIdToken(true).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                idToken = task.getResult().getToken();
//                Toast.makeText(this, "DEBUG: 토큰 가져오기 성공! " + idToken, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "DEBUG: 토큰 가져오기 실패!", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return idToken;
//    }

}