package com.example.administrator.huha.Gayeon;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

public class RestActivity extends BaseActivity {

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference();
    ProgressBar mprogressBar;
    ProgressBar background;

    int count = 0;
    double persent = 0;
    int whole_count = 124;

    EditText edit_count, whole;
    ImageButton plus, save, minus;
    ImageView reset;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    String name, date, availity;

    EditText name_edit, date_edit, availity_edit;

    Toolbar mToolbar;

    boolean data = false;


    final Calendar c = Calendar.getInstance();
    int myear=0,mmonth,mday;
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    String temp;
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
        minus = (ImageButton) findViewById(R.id.minus);
        save = (ImageButton) findViewById(R.id.save);
        edit_count = (EditText) findViewById(R.id.edit_count);
        reset = (ImageView) findViewById(R.id.reset);

        name_edit = (EditText) findViewById(R.id.name);
        date_edit = (EditText) findViewById(R.id.date);
        availity_edit = (EditText) findViewById(R.id.availity);
        whole = (EditText) findViewById(R.id.whole);

        mprogressBar.setProgress(count);
        edit_count.setText(String.valueOf(count));
        whole.setText(" / "+String.valueOf(whole_count)+"회");


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

                    whole_count = Integer.parseInt(availity);
                    whole.setText(" / "+String.valueOf(whole_count)+"회");

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
                    Toast.makeText(RestActivity.this, "흡입기의 약을 다 사용하셨습니다!", Toast.LENGTH_LONG).show();
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tokenID = FirebaseInstanceId.getInstance().getToken();
                sendData SendData = new sendData();
                mReference = mDatabase.getReference("Date");
                String time = getTime().toString().trim();

                if (count != 0) {
                    count--;
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
                    //Toast.makeText(RestActivity.this, "흡입기 사용 횟수가 0", Toast.LENGTH_LONG).show();
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
                resultIntent.putExtra("whole_count", whole_count);
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
        resultIntent.putExtra("whole_count", whole_count);
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
            whole_count = pref.getInt("whole_count", 124);

            myear = pref.getInt("myear", 0);
            mmonth = pref.getInt("mmonth", 0);
            mday = pref.getInt("mday", 0);

            name_edit.setText(name);
            temp = Integer.toString(countdday(myear,mmonth,mday));
            temp = temp+"일 지났습니다.";
            date_edit.setText(temp);

            availity_edit.setText(availity);
            whole.setText(" / "+String.valueOf(whole_count)+"회");

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
        //editor.putString("date", date);
        editor.putString("date", temp);
        editor.putString("availity", availity);
        editor.putInt("myear", myear);
        editor.putInt("mmonth", mmonth);
        editor.putInt("mday", mday);
        editor.putInt("whole_count", whole_count);
        editor.putBoolean("data", data);
        editor.commit();
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                DatePickerDialog dpd = new DatePickerDialog
                        (RestActivity.this, // 현재화면의 제어권자
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(android.widget.DatePicker view,
                                                          int year, int monthOfYear, int dayOfMonth) {
                                        //date_edit.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                                        myear = year;
                                        mmonth = monthOfYear+1;
                                        mday = dayOfMonth;

                                        temp = Integer.toString(countdday(myear,mmonth,mday));
                                        temp = temp+"일 지났습니다.";
                                        date_edit.setText(temp);
                                    }
                                },
                                year, month, day);
                return dpd;
        }

        return super.onCreateDialog(id);
    }

    public int countdday(int myear, int mmonth, int mday) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar todaCal = Calendar.getInstance(); //오늘날자 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날자를 가져와 변경시킴

            mmonth -= 1; // 받아온날자에서 -1을 해줘야함.
            ddayCal.set(myear,mmonth,mday);// D-day의 날짜를 입력
            //Log.e("테스트",simpleDateFormat.format(todaCal.getTime()) + "");
            //Log.e("테스트",simpleDateFormat.format(ddayCal.getTime()) + "");

            long today = todaCal.getTimeInMillis()/86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis()/86400000;
            long count = today - dday; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.
            return (int) count;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
    }

}
