package com.example.greenmeter;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CarListMain extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private List<String> carTypeList;
    private EditText editTextSearch;
    private ListView listViewCarType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase 데이터베이스의 "Transportation" 노드에 대한 참조를 가져옵니다.
        mDatabase = FirebaseDatabase.getInstance().getReference("Transportation");

        View view = new View(this);
        // UI 요소 찾기
        editTextSearch = view.findViewById(R.id.editTextSearch);
        listViewCarType = view.findViewById(R.id.listViewCarType);

        // 차종 목록 어댑터 생성 및 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carTypeList);
        listViewCarType.setAdapter(adapter);

        // 검색어 입력 이벤트 처리
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString().toLowerCase();
                searchCarTypes(searchQuery);
            }
        });

        // 차종 선택 이벤트 처리
        listViewCarType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedCarType = carTypeList.get(position);
                showCarbonValue(selectedCarType);
            }
        });
    }

    private void searchCarTypes(String searchQuery) {
        Query query = mDatabase.orderByKey().startAt(searchQuery).endAt(searchQuery + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                carTypeList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String carType = snapshot.getKey();
                    carTypeList.add(carType);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CarListMain.this, android.R.layout.simple_list_item_1, carTypeList);
                listViewCarType.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리를 수행합니다.
            }
        });
    }

    private void showCarbonValue(String carType) {
        Query query = mDatabase.child(carType);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer carbonValue = dataSnapshot.getValue(Integer.class);

                if (carbonValue != null) {
                    String message = "차종: " + carType + "\n탄소값: " + carbonValue;
                    // 탄소값을 원하는 방식으로 출력하거나 처리할 수 있습니다.
                    // 예시로 텍스트뷰에 출력합니다.
                    // textViewResult.setText(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리를 수행합니다.
            }
        });
    }
}