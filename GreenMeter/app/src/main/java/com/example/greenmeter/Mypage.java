/**
 * 코드 작성자
 * 황유림 - 전체 코드 작성
 * */

package com.example.greenmeter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.greenmeter.database.dbCommand;
import com.google.firebase.auth.FirebaseAuth;

public class Mypage extends Fragment {
    private View view;
    private FirebaseAuth authService;;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mypage, container, false);
        authService = FirebaseAuth.getInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        TextView usernameTextView = view.findViewById(R.id.name_edit);
        TextView nicknameTextView = view.findViewById(R.id.nickname_edit);
        TextView my_co2 = view.findViewById(R.id.my_co2);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbCommand dbcommand = new dbCommand();
        dbcommand.getNameFromDatabase(userId, new dbCommand.OnNameRetrievedListener() {
            @Override
            public void onNameRetrieved(String username) {
                usernameTextView.setText(username);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Firebase", "Error: " + errorMessage);
            }
        });
        dbcommand.getNicknameFromDatabase(userId, new dbCommand.OnNicknameRetrievedListener() {
            @Override
            public void onNicknameRetrieved(String nickname) {
                nicknameTextView.setText(nickname);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Firebase", "Error: " + errorMessage);
            }
        });
        dbcommand.getTotalco2FromDatabase(userId, new dbCommand.OnTotalco2RetrievedListener() {
            @Override
            public void onTotalco2Retrieved(Double totalco2) {
                my_co2.setText(Double.toString(totalco2));
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("Firebase", "Error: " + errorMessage);
            }
        });


        Button btn_logout = view.findViewById(R.id.logout_btn);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authService.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                fragmentManager.beginTransaction().remove(Mypage.this).commit();
                fragmentManager.popBackStack();
            }
        });

        Button btn_delete_account = view.findViewById(R.id.delete_account_btn);
        btn_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authService.getCurrentUser().delete();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                fragmentManager.beginTransaction().remove(Mypage.this).commit();
                fragmentManager.popBackStack();
            }
        });
        return view;
    }

}