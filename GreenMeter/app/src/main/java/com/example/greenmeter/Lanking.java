package com.example.greenmeter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class Lanking extends Fragment {
    int lank = 1;
    LinearLayout MainLayout;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        LinearLayout MainLayout = new LinearLayout(getActivity());
//        MainLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        MainLayout.setOrientation(LinearLayout.VERTICAL);

//        MainLayout.addView(createItem());

        MainLayout = new LinearLayout(getActivity());
        MainLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2330));
        MainLayout.setOrientation(LinearLayout.VERTICAL);
        MainLayout.setPadding(0, 0, 0, 0);
        addItem();
        return MainLayout;
    }

    public void addItem() {
        MainLayout.addView(createItem());
    }

    public LinearLayout createItem() {
        LinearLayout itemLayout = new LinearLayout(this.getContext());
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 260));
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setBackground(ContextCompat.getDrawable(this.getContext(), R.drawable.timeline_detail));
        itemLayout.setPadding(0, 40, 0, 0);

        LinearLayout itemHeader = new LinearLayout(this.getContext());
        itemHeader.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        itemHeader.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView1 = new TextView(this.getContext());
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(40, 0, 0, 0);
        textView1.setLayoutParams(params1);
        textView1.setTextSize(23);
        textView1.setTypeface(null, Typeface.BOLD);
        textView1.setTextColor(Color.parseColor("#FFCE00"));
        textView1.setText(lank+"위 ");

        TextView textView2 = new TextView(this.getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView2.setLayoutParams(params2);
        textView2.setTextSize(23);
        textView2.setTypeface(null, Typeface.BOLD);
        textView2.setText("뱅뱅숑");

        TextView textView3 = new TextView(this.getContext());
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(50, LinearLayout.LayoutParams.WRAP_CONTENT);
        textView3.setLayoutParams(params3);
        textView3.setText(" 님");

        ImageButton imageButton = new ImageButton(this.getContext());
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(60, 60);
        params4.setMargins(540, 20, 0, 0);
        imageButton.setLayoutParams(params4);
        imageButton.setBackground(null);
        imageButton.setImageResource(R.drawable.arrow_forward_ios_fill0_wght400_grad0_opsz48);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "랭킹상세페이지 버튼이 눌림", Toast.LENGTH_SHORT).show();
            }
        });



        itemHeader.addView(textView1);
        itemHeader.addView(textView2);
        itemHeader.addView(textView3);
        itemHeader.addView(imageButton);

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
        textView4.setText("11,609");

        TextView textView5 = new TextView(this.getContext());
        LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params6.setMargins(0, 20, 0, 0);
        textView5.setLayoutParams(params6);
        textView5.setText(" g/km");

        itemBody.addView(textView4);
        itemBody.addView(textView5);

        itemLayout.addView(itemHeader);
        itemLayout.addView(itemBody);

        return itemLayout;
    }

//    public LinearLayout createItem() {
//        LinearLayout itemLayout = new LinearLayout(this.getContext()); //수평 레이아웃 생성
//        itemLayout.setOrientation(LinearLayout.VERTICAL); //수직 레이아웃 속성 설정
//
//        LinearLayout Header = createitemHeader();
//        LinearLayout Body = createitembody();
//
//        itemLayout.addView(Header); //수평 레이아웃에 텍스트뷰 추가
//        itemLayout.addView(Body); //수평 레이아웃에 텍스트뷰 추가
//        return itemLayout;
//    }
//
//    public LinearLayout createitemHeader() {
//        LinearLayout itemHeader = new LinearLayout(this.getContext()); //수평 레이아웃 생성
//        itemHeader.setOrientation(LinearLayout.HORIZONTAL); //수평 레이아웃 속성 설정
//        itemHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150));
//
//        //Item Header Element
//        TextView textLank = new TextView(this.getContext()); //텍스트뷰 생성
//        TextView textName = new TextView(this.getContext()); //텍스트뷰 생성
//        TextView textNamePlus = new TextView(this.getContext()); //텍스트뷰 생성
//        ImageButton showDetailBtn = new ImageButton(this.getContext()); //이미지버튼 생성
//
//        textLank.setText(lank + "위"); //텍스트뷰에 텍스트 추가
//        textLank.setTextColor(Color.parseColor("#FFD00C")); //텍스트뷰에 텍스트 색상 추가
//        textLank.setTextSize(30);
//        textLank.layout(40, 20, 0, 0);
//        textLank.setLayoutParams(new LinearLayout.LayoutParams(130, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        textName.setText("김동규"); //텍스트뷰에 텍스트 추가
//        textName.setTextSize(30);
//        textName.setLayoutParams(new LinearLayout.LayoutParams(210, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        textNamePlus.setText("님"); //텍스트뷰에 텍스트 추가
//
//        showDetailBtn.setImageResource(R.drawable.arrow_forward_ios_fill0_wght400_grad0_opsz48); //이미지버튼에 이미지 추가
//        showDetailBtn.setBackground(null); //이미지버튼 배경 삭제
//        showDetailBtn.setLayoutParams(new LinearLayout.LayoutParams(60, 60));
//        showDetailBtn.setLeft(540);
//        showDetailBtn.setTop(40);
//
//        itemHeader.addView(textLank); //수평 레이아웃에 텍스트뷰 추가
//        itemHeader.addView(textName); //수평 레이아웃에 텍스트뷰 추가
//        itemHeader.addView(textNamePlus); //수평 레이아웃에 텍스트뷰 추가
//        itemHeader.addView(showDetailBtn); //수평 레이아웃에 이미지버튼 추가
//
//        return itemHeader;
//    }
//
//    public LinearLayout createitembody() {
//        LinearLayout itemBody = new LinearLayout(this.getContext()); //수평 레이아웃 생성
//        itemBody.setOrientation(LinearLayout.HORIZONTAL); //수평 레이아웃 속성 설정
//        itemBody.setLayoutParams(new LinearLayout.LayoutParams(400, 150));
//
//        TextView carbonNUM = new TextView(this.getContext()); //텍스트뷰 생성
//        TextView carbonUnit = new TextView(this.getContext()); //텍스트뷰 생성
//
//        carbonNUM.setLayoutParams(new LinearLayout.LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
//
//        carbonNUM.setText("11390");
//        carbonNUM.setTextSize(60);
//        carbonNUM.setLeft(40);
//        carbonNUM.setTextColor(Color.parseColor("#FFD00C")); //텍스트뷰에 텍스트 색상 추가
//
//        carbonUnit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
//        carbonUnit.setText("g/km");
//
//        itemBody.addView(carbonNUM); //수평 레이아웃에 텍스트뷰 추가
//        itemBody.addView(carbonUnit); //수평 레이아웃에 텍스트뷰 추가
//        return itemBody;
//    }
}