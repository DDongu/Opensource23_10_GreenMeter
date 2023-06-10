/**
 * 코드 작성자
 * 김동규 - 전체 코드 작성
 * */

package com.example.greenmeter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.greenmeter.database.dbCommand;
import com.google.firebase.auth.FirebaseAuth;

public class TimelineDetail extends Fragment {
    private View view;
    private ImageButton backToTimelineBtn;
    private TextView car_km, car_co2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.timeline_detail, container, false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        car_co2 = view.findViewById(R.id.car_co2);
        car_km = view.findViewById(R.id.car_km);

        backToTimelineBtn = view.findViewById(R.id.back_to_timeline_btn);
        backToTimelineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        // DB에서 total_distance & total_carbonEm불러오기
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbCommand dbcommand = new dbCommand();
        dbcommand.getTotalkmFromDatabase(userId, new dbCommand.OnTotalkmRetrievedListener() {
            @Override
            public void onTotalkmRetrieved(Double totalkm) {
                car_km.setText(Double.toString(totalkm));
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Firebase", "Error: " + errorMessage);
            }
        });
        dbcommand.getTotalco2FromDatabase(userId, new dbCommand.OnTotalco2RetrievedListener() {
            @Override
            public void onTotalco2Retrieved(Double totalco2) {
                car_co2.setText(Double.toString(totalco2));
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Firebase", "Error: " + errorMessage);
            }
        });
        return view;
    }
}
