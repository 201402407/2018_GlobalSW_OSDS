package com.example.administrator.huha;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.huha.Gayeon.BluetoothActivity;
import com.example.administrator.huha.GoogleMap.Database;
import com.example.administrator.huha.GoogleMap.Googlemap;
import com.google.android.gms.maps.GoogleMap;

public class MainActivity extends AppCompatActivity {
    BroadcastReceiver receiver;
    Intent intentMyService;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///맵으로 화면이동시키기 위한 임시 버튼클릭 이벤트123///
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, BluetoothActivity.class));
            }
        });
        ///맵으로 화면이동시키기 위한 임시 버튼클릭 이벤트///

        /*
        // 액션 바 Title 설정
        ActionBar ab = getSupportActionBar();

        // 타이틀 바 숨기기
        ab.hide();

        ab.setTitle("Huha 메인 화면 액션 바");

        Log.d("MpMainActivity", "service start!!!!!!");
        //대원
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
        */
    }

    public void OnDestroy() {
        Log.d("MpMainActivity", "Service Destroy");

        // 리시버 삭제를 하지 않으면 에러
        unregisterReceiver(receiver);

        super.onDestroy();
    }

}
