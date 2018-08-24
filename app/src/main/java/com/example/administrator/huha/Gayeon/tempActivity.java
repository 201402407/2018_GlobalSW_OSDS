package com.example.administrator.huha.Gayeon;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.huha.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class tempActivity extends BaseActivity {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference();
    ProgressBar mprogressBar;
    ProgressBar background;

    int count = 0;
    double persent = 0;
    final int whole_count = 124;

    EditText edit_count;
    ImageButton plus, save;
    ImageView reset;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    String name, date, availity;

    EditText name_edit, date_edit, availity_edit;

    Toolbar mToolbar;

    boolean data = false;


    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 표시 안하게 하기.
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_icon);

        Intent intent = getIntent();
        count = intent.getIntExtra("count", 0);

        background = (ProgressBar) findViewById(R.id.circular_progress_bar_background);
        background.setProgress(100);
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        mprogressBar.setProgress(count);

        mprogressBar.setRotation(270);

        plus = (ImageButton) findViewById(R.id.plus);
        save = (ImageButton) findViewById(R.id.save);
        edit_count = (EditText) findViewById(R.id.edit_count);
        reset = (ImageView) findViewById(R.id.reset);

        name_edit = (EditText) findViewById(R.id.name);
        date_edit = (EditText) findViewById(R.id.date);
        availity_edit = (EditText) findViewById(R.id.availity);

        mprogressBar.setProgress(count);
        edit_count.setText(String.valueOf(count));

        date_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(1);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data) {
                    name = name_edit.getText().toString();
                    date = date_edit.getText().toString();
                    availity = availity_edit.getText().toString();

                    save.setImageResource(R.drawable.edit);

                    name_edit.setEnabled(false);
                    date_edit.setEnabled(false);
                    availity_edit.setEnabled(false);

                    data = true;
                } else {
                    name_edit.setEnabled(true);
                    date_edit.setEnabled(true);
                    availity_edit.setEnabled(true);

                    save.setImageResource(R.drawable.save);

                    data = false;
                }
            }

        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                persent = 0;
                edit_count.setText("0");
                mprogressBar.setProgress(count);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tokenID = FirebaseInstanceId.getInstance().getToken();
                sendData SendData = new sendData();
                mReference = mDatabase.getReference("Date");
                String time = getTime().toString().trim();

                if (count != 124) {
                    count++;
                    if (!TextUtils.isEmpty(tokenID)) {
                        SendData.count = count;
                        SendData.firebaseKey = tokenID;
                        mReference.child(tokenID).child(time).setValue(SendData);
                    }
                    mprogressBar.setProgress(count);
                    edit_count.setText(String.valueOf(count));


//                    Toast.makeText(MainActivity.this, time +" : "+count, Toast.LENGTH_LONG).show();

                    persent = (double) count / (double) whole_count;

                } else {
                    Toast.makeText(tempActivity.this, "흡입기의 약을 다 사용하셨습니다!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    // 액션 바 메뉴 생성 함수
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    */
/*

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.home){
            Toast.makeText(this,"asd", Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("count", count);
            setResult(RESULT_OK, resultIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    // 액션 바 메뉴 클릭 이벤트 함수.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작

                Intent resultIntent = new Intent();
                resultIntent.putExtra("count", count);
                setResult(RESULT_OK, resultIntent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("count", count);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    protected void onResume() {
        super.onResume();
        restoreState();
    }

    protected void restoreState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        if (pref != null) {
            name = pref.getString("name", "");
            date = pref.getString("date", "");
            availity = pref.getString("availity", "");

            name_edit.setText(name);
            date_edit.setText(date);
            availity_edit.setText(availity);

            data = pref.getBoolean("data", false);

            if (data){
                save.setImageResource(R.drawable.edit);
                name_edit.setEnabled(false);
                date_edit.setEnabled(false);
                availity_edit.setEnabled(false);

            }
        }

    }

    protected void saveState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("name", name);
        editor.putString("date", date);
        editor.putString("availity", availity);

        editor.putBoolean("data", data);
        editor.commit();
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                DatePickerDialog dpd = new DatePickerDialog
                        (tempActivity.this, // 현재화면의 제어권자
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(android.widget.DatePicker view,
                                                          int year, int monthOfYear, int dayOfMonth) {
                                        date_edit.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                                    }
                                },
                                year, month, day);
                return dpd;
        }

        return super.onCreateDialog(id);
    }

}
