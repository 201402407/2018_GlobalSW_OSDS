package com.example.administrator.huha.Gayeon;

import android.app.Activity;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.administrator.huha.R;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mRemoteDevie;

    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\r\n";
    char mCharDelimiter = '\n';
    EditText mEditReceive;

    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    Button Btn_Connect;

    String BT_NAME = "OSDS";

    int count = 0;
    double persent = 0;
    final int whole_count = 124;

    EditText edit_count;
    Button reset, plus;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");


    ProgressBar mprogressBar;
    ProgressBar background;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        plus = (Button) findViewById(R.id.plus);

        edit_count = (EditText) findViewById(R.id.edit_count);
        reset = (Button) findViewById(R.id.reset);

        background = (ProgressBar) findViewById(R.id.circular_progress_bar_background);
        background.setProgress(100);
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        mprogressBar.setProgress(count);

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
                if (count != 124) {
                    count++;
                    mprogressBar.setProgress(count);
                    edit_count.setText(String.valueOf(count));

                    String time = getTime();
//                    Toast.makeText(MainActivity.this, time +" : "+count, Toast.LENGTH_LONG).show();

                    persent = (double) count / (double) whole_count;

                    if (persent > 0.9) {
                        noti();
                    }
                } else {
                    Toast.makeText(BluetoothActivity.this, "흡입기의 약을 다 사용하셨습니다!", Toast.LENGTH_LONG).show();
                }
            }
        });


        checkBluetooth();
    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;
        for (BluetoothDevice deivce : mDevices) {
            if (name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    // 문자열 전송하는 함수
    public void sendData(String msg) {
        try {
            mOutputStream.write(msg.getBytes()); // 문자열 전송
        } catch (Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
//            Btn_Connect.setVisibility(View.VISIBLE);
//            Btn_RESET.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();
        }
    }

    public void connectToSelectedDevice(String selectedDeviceName) {
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            Toast.makeText(this, "블루투스 연결 성공!", Toast.LENGTH_LONG).show();

//            Btn_Connect.setVisibility(View.INVISIBLE);
//            Btn_RESET.setVisibility(View.VISIBLE);

            // 데이터 수신 준비.
            beginListenForData();

        } catch (Exception e) {
            Toast.makeText(this, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
//            Btn_Connect.setVisibility(View.VISIBLE);
//            Btn_RESET.setVisibility(View.INVISIBLE);
        }
    }

    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    public void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;
        readBuffer = new byte[1024];

        // 문자열 수신 쓰레드.
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int byteAvailable = mInputStream.available();
                        if (byteAvailable > 0) {
                            byte[] packetBytes = new byte[byteAvailable];
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == mCharDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {

                                            if (data.equals("c")) {


                                                try {
                                                    mWorkerThread.sleep(1000);

                                                    Toast.makeText(BluetoothActivity.this, "흡입기 사용", Toast.LENGTH_SHORT).show();

                                                    if (count != 124) {
                                                        count++;
                                                        mprogressBar.setProgress(count);
                                                        edit_count.setText(String.valueOf(count));

                                                        String time = getTime();
//                                                  Toast.makeText(BluetoothActivity.this, time +" : "+count, Toast.LENGTH_LONG).show();

                                                        persent = (double) count / (double) whole_count;

                                                        if (persent > 0.9) {
                                                            noti();
                                                        }
                                                    } else {
                                                        Toast.makeText(BluetoothActivity.this, "흡입기의 약을 다 사용하셨습니다!", Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }


                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception e) {    // 데이터 수신 중 오류 발생.
                        Toast.makeText(BluetoothActivity.this, "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show();
                        Btn_Connect.setVisibility(View.VISIBLE);
//                        Btn_RESET.setVisibility(View.INVISIBLE);
                    }
                }
            }

        });

        mWorkerThread.start();

    }

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // 노티 알람 함수
    private void noti() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(BluetoothActivity.this)
                        .setSmallIcon(R.drawable.ic_action_noti)
                        .setContentTitle("HU-HA")
                        .setContentText("흡입기의 약이 얼마 남지 않았어요!!")
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void selectDevice() {
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
            Toast.makeText(this, "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
//            Btn_Connect.setVisibility(View.VISIBLE);
//            Btn_RESET.setVisibility(View.INVISIBLE);
        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("블루투스 장치 선택");
//
//            List<String> listItems = new ArrayList<String>();
//            for (BluetoothDevice device : mDevices) {
//                listItems.add(device.getName());
//            }
//            listItems.add("취소");
//
//
//            final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
//            listItems.toArray(new CharSequence[listItems.size()]);
//
//            builder.setItems(items, new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int item) {
//                    // TODO Auto-generated method stub
//                    if (item == mPariedDeviceCount) {
//                        Toast.makeText(this, "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
////                    finish();
//                    } else {
////                        connectToSelectedDevice(items[item].toString());
//                        connectToSelectedDevice("OSDS");
//                    }
//                }
//
//            });
//
//            builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지
//            AlertDialog alert = builder.create();
//            alert.show();
            connectToSelectedDevice(BT_NAME);
        }
    }


    public void checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();  // 앱종료
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else
                selectDevice();
        }
    }


    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            count = pref.getInt("count", 0);
            edit_count.setText(String.valueOf(count));
            mprogressBar.setProgress(count);
        }

    }

    protected void saveState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("count", count);

        editor.commit();
    }
}
