package com.example.greenmeter;

/**
 * Mypage Main
 **/

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
    private FirebaseAuth authService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mypage, container, false);
        authService = FirebaseAuth.getInstance();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        TextView usernameTextView = view.findViewById(R.id.name_edit);
        TextView nicknameTextView = view.findViewById(R.id.nickname_edit);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbCommand dbcommand = new dbCommand();
        dbcommand.getNameFromDatabase(userId, new dbCommand.OnNameRetrievedListener() {
            @Override
            public void onNameRetrieved(String username) {
                // 닉네임을 사용하여 TextView의 텍스트를 설정합니다.
                usernameTextView.setText(username);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 에러 처리를 수행합니다.
                Log.e("Firebase", "Error: " + errorMessage);
            }
        });
        dbcommand.getNicknameFromDatabase(userId, new dbCommand.OnNicknameRetrievedListener() {
            @Override
            public void onNicknameRetrieved(String nickname) {
                // 닉네임을 사용하여 TextView의 텍스트를 설정합니다.
                nicknameTextView.setText(nickname);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 에러 처리를 수행합니다.
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