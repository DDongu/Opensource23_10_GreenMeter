package com.example.greenmeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Greenmeter greenmeter;
    private Timeline timeline;
    private Lanking lanking;
    private Mypage mypage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavi);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.tab_greenmeter:
                        setFrag(0);
                        break;
                    case R.id.tab_timeline:
                        setFrag(1);
                        break;
                    case R.id.tab_lanking:
                        setFrag(2);
                        break;
                    case R.id.tab_mypage:
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        greenmeter = new Greenmeter();
        timeline = new Timeline();
        lanking = new Lanking();
        mypage = new Mypage();
        setFrag(0); // 첫 프래그먼트 화면 지정
    }

    //프래그먼트 교체가 이러는 곳
    private void setFrag(int n) {
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch (n) {
            case 0:
                ft.replace(R.id.main_frame, greenmeter);
                ft.commit();
                break;
            case 1:
                ft.replace(R.id.main_frame, timeline);
                ft.commit();
                break;
            case 2:
                ft.replace(R.id.main_frame, lanking);
                ft.commit();
                break;
            case 3:
                ft.replace(R.id.main_frame, mypage);
                ft.commit();
                break;
        }
    }

}