package com.example.administrator.huha;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver receiver;
    Intent intentMyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 액션 바 Title 설정
        ActionBar ab = getSupportActionBar();

        // 타이틀 바 숨기기
        ab.hide();

        ab.setTitle("Huha 메인 화면 액션 바");

        Log.d("MpMainActivity", "service start!!!!!!");
        //가연
        // immortal service 등록
        intentMyService = new Intent(this, PersistentService.class);
        //  intentMyService.putExtra("command", "start");
        // 리시버 등록
        receiver = new RestartService();

        try
        {
            // xml에서 정의해도 됨
            // 지워도 됨
            IntentFilter mainFilter = new IntentFilter("com.hamon.GPSservice.ssss");
            // 리시버 저장
            registerReceiver(receiver, mainFilter);
            // 서비스 시작
            Toast.makeText(this, "Start!", Toast.LENGTH_LONG).show();
            startService(intentMyService);

        } catch (Exception e) {

            Log.d("MpMainActivity", e.getMessage()+"");
            e.printStackTrace();
        }

    }

    public void OnDestroy() {
        Log.d("MpMainActivity", "Service Destroy");

        // 리시버 삭제를 하지 않으면 에러
        unregisterReceiver(receiver);

        super.onDestroy();
    }
}
