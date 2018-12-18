package com.example.administrator.huha;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.administrator.huha.Gayeon.BluetoothActivity;
import com.example.administrator.huha.GoogleMap.Googlemap;

public class SplashActivity extends Activity {
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MainActivity.class 자리에 다음에 넘어갈 액티비티를 넣어주기
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), BluetoothActivity.class);
                intent.putExtra("state", "launch");
                startActivity(intent);
                finish();
            }
        };

        // Handler를 통한 대기시간 설정
        // (임의로 출력 확인을 위해 적은것 뿐, 원래는 앱 로딩 끝나면 바로 넘어가게 되어있음)
        mHandler = new Handler();
        // mRunnable 내부 run() 실행하려면 기다려야 하는 delayMillis
        mHandler.postDelayed(mRunnable, 2000);
    }
}