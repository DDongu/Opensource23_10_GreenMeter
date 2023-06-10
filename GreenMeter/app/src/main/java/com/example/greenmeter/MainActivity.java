/**
 * 코드 작성자
 * 황유림 - 전체 코드 작성
 * */

package com.example.greenmeter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
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
        });

        greenmeter = new Greenmeter();
        timeline = new Timeline();
        lanking = new Lanking();
        mypage = new Mypage();
        setFrag(0); // 첫 프래그먼트 화면 지정
    }

    //프래그먼트 교체가 일어나는 곳
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