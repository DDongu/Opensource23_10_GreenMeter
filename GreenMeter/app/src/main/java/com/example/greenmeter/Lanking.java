package com.example.greenmeter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.greenmeter.database.dbCommand;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Lanking extends Fragment {
    View view;
    RelativeLayout LankingLayout;
    LinearLayout itemContainer;
    private TextView usernameTextView, userlankTextView;
    private static DatabaseReference mDatabaseRef; //실시간 데이터베이스


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanㅅceState) {
        view = inflater.inflate(R.layout.lanking, container, false);
        LankingLayout = view.findViewById(R.id.lankingLayout);

        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1860));

        // 화면 세팅
        itemContainer = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 140, 0, 0);
        itemContainer.setLayoutParams(layoutParams);
        itemContainer.setOrientation(LinearLayout.VERTICAL);

        // 유저 랭킹 정보
        usernameTextView = view.findViewById(R.id.lanking_myname);
        userlankTextView = view.findViewById(R.id.lanking_myrank);
        userlankTextView.setText("1");
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
        
        // 다른 유저 랭킹 정보 추가
        addItem();
        scrollView.addView(itemContainer);
        LankingLayout.addView(scrollView);
        return view;
    }
    
    public void addItem() {
        String[] usernames = new String[]{"김동규", "황유림", "김태현", "김동민"};
        String[] carbonValues = new String[]{"124.0", "134.0", "153.0", "185.0"};
        
        for(int i = 0; i < 4; i++) {
            LinearLayout itemLayout = createItem(usernames[i], carbonValues[i], i+1);
            itemContainer.addView(itemLayout);
        }
    }

    public LinearLayout createItem(String username, String carbonValue, int lank) {
        LinearLayout itemLayout = new LinearLayout(this.getContext());
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 260));
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.timeline_detail));
        itemLayout.setPadding(0, 20, 0, 0);

        LinearLayout itemHeader = new LinearLayout(this.getContext());
        itemHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemHeader.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView1 = new TextView(this.getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(40, 0, 0, 0);
        textView1.setLayoutParams(params1);
        textView1.setTextSize(23);
        textView1.setTypeface(null, Typeface.BOLD);
        if(lank == 1)
            textView1.setTextColor(Color.parseColor("#FFDC4A"));
        else if(lank == 2)
            textView1.setTextColor(Color.parseColor("#DDDCD5"));
        else if(lank == 3)
            textView1.setTextColor(Color.parseColor("#CCB259"));
        else
            textView1.setTextColor(Color.parseColor("#78AFAF"));

        textView1.setText(lank+"위 ");

        TextView textView2 = new TextView(this.getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView2.setLayoutParams(params2);
        textView2.setTextSize(23);
        textView2.setTypeface(null, Typeface.BOLD);
        textView2.setText(username);

        TextView textView3 = new TextView(this.getContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(50, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView3.setLayoutParams(params3);
        textView3.setText(" 님");

//        ImageButton imageButton = new ImageButton(this.getContext());
//        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(60, 60);
//        params4.setMargins(540, 20, -100, 0);
//        imageButton.setLayoutParams(params4);
//        imageButton.setBackground(null);
//        imageButton.setImageResource(R.drawable.arrow_forward_ios_fill0_wght400_grad0_opsz48);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "랭킹상세페이지 버튼이 눌림", Toast.LENGTH_SHORT).show();
//            }
//        });



        itemHeader.addView(textView1);
        itemHeader.addView(textView2);
        itemHeader.addView(textView3);
//        itemHeader.addView(imageButton);

        // 바디
        LinearLayout itemBody = new LinearLayout(this.getContext());
        itemBody.setLayoutParams(new LinearLayout.LayoutParams(400, 150));
        itemBody.setOrientation(LinearLayout.HORIZONTAL);
        itemBody.setPadding(0, 10, 0, 0);

        TextView textView4 = new TextView(this.getContext());
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params5.setMargins(40, 20, 0, 0);
        textView4.setLayoutParams(params5);
        textView4.setTextSize(18);
        textView4.setTypeface(null, Typeface.BOLD);
        textView4.setText(carbonValue);

        TextView textView5 = new TextView(this.getContext());
        LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params6.setMargins(0, 20, 0, 0);
        textView5.setLayoutParams(params6);
        textView5.setText(" g/km");

        itemBody.addView(textView4);
        itemBody.addView(textView5);

        itemLayout.addView(itemHeader);
        itemLayout.addView(itemBody);

        itemLayout.setId(View.generateViewId());

        return itemLayout;
    }
}