/**
 * 코드 작성자
 * 황유림 - 전체 코드 작성
 * */

package com.example.greenmeter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenmeter.database.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText mEtEmail, mEtpwd, mEtusername, mEtph, mEtnickname; //회원가입 입력필드
    private Button mBtnRegister; //회원가입 버튼
    private ImageButton mBtnBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("GreenMeter");

        mEtEmail = findViewById(R.id.email_text_edit);
        mEtpwd = findViewById(R.id.password_text_edit);
        mEtusername = findViewById(R.id.name_text_edit);
        mEtph = findViewById(R.id.phonenumber_text_edit);
        mEtnickname = findViewById(R.id.nickname_text_edit);
        mBtnRegister = findViewById(R.id.button_register);
        mBtnBackLogin = findViewById(R.id.back_login_button);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입 처리 시작
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtpwd.getText().toString();
                String strUsername = mEtusername.getText().toString();
                String strPh = mEtph.getText().toString();
                String strNickname = mEtnickname.getText().toString();
                Double carbonEm = 0.0;
                Double total_distance = 0.0;
                Double total_carbonEm = 0.0;
                Double avg_carbonEm = 0.0;

                //firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailId(firebaseUser.getEmail());
                            account.setPassword(strPwd);
                            account.setUsername(strUsername);
                            account.setPh(strPh);
                            account.setNickname(strNickname);
                            account.setCarbonEm(carbonEm);
                            account.setTotal_distance(total_distance);
                            account.setTotal_carbonEm(total_carbonEm);
                            account.setAvg_carbonEm(avg_carbonEm);

                            // setvalue : datebase에 inset(삽입) 입력
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(RegisterActivity.this, "환영합니다!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); //로그인 화면으로 이동
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        });

        mBtnBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class); //로그인 화면으로 이동
                finish();
            }
        });

    }


}