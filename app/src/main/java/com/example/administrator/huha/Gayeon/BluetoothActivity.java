package com.example.administrator.huha.Gayeon;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.huha.GoogleMap.Googlemap;
import com.example.administrator.huha.PersistentService;
import com.example.administrator.huha.R;
import com.example.administrator.huha.SplashActivity;
import com.example.administrator.huha.jaehun.WeatherRepo;
import com.example.administrator.huha.jaehun.day3Repo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BluetoothActivity extends Base2Activity implements LocationListener {

    double longitude;
    double latitude;
    LocationManager locationManager;
    ImageView sky1, sky2, sky3;
    TextView temp1, temp2, temp3;
    LinearLayout weather;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mReference = mDatabase.getReference();
    private ChildEventListener mChild;

    static final int REQUEST_ENABLE_BT = 10;
    static final int REQUEST_INTENT = 30;
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
    EditText mEditReceive, whole;

    Thread mWorkerThread = null;
    byte[] readBuffer;
    int readBufferPosition;

    Button Btn_Connect;

    String BT_NAME = "OSDS";

    int count = 0;
    double persent = 0;
    int whole_count = 124;

    EditText edit_count;
    Button plus;
    ImageView circle;

    Button test; //weatheractivity test용 버튼

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");


    ProgressBar mprogressBar;
    ProgressBar background;

    boolean check = false;

    // Noti 알람 클릭 시 이동하는 화면 인텐트 정의.
    Intent Noti_intent = new Intent(this, SplashActivity.class);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        whole = (EditText)findViewById(R.id.whole);

        ImageButton button = findViewById(R.id.find_hospital);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Googlemap.class);
                startActivity(intent);
            }
        });
        weather = (LinearLayout) findViewById(R.id.weather);

        initView();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        requestLocation();
        weather.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), com.example.administrator.huha.jaehun.weatherActivity.class);
                startActivity(intent2);
            }
        });

        plus = (Button) findViewById(R.id.plus);

        edit_count = (EditText) findViewById(R.id.edit_count);
        circle = (ImageView) findViewById(R.id.circle);

        background = (ProgressBar) findViewById(R.id.circular_progress_bar);
        background.setProgress(100);
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar_background);
        mprogressBar.setProgress(count);

        mprogressBar.setRotation(270);

        plus.setVisibility(View.INVISIBLE);
        //circle.setVisibility(View.INVISIBLE);
        circle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestActivity.class);
                intent.putExtra("count", count);
                startActivityForResult(intent, REQUEST_INTENT);
            }
        });

        edit_count.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestActivity.class);
                intent.putExtra("count", count);
                startActivityForResult(intent, REQUEST_INTENT);
            }
        });

        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String tokenID = FirebaseInstanceId.getInstance().getToken();
                sendData SendData = new sendData();
                mReference = mDatabase.getReference("Date");
                String time = getTime().toString().trim();
                if (count != whole_count) {
                    count++;
                    /////firebase에 데이터 저장////
                    if (!TextUtils.isEmpty(tokenID)) {
                        SendData.count = count;
                        SendData.firebaseKey = tokenID;
                        mReference.child(tokenID).child(time).setValue(SendData);
                    }
                    mprogressBar.setProgress(count);
                    edit_count.setText(String.valueOf(whole_count - count));


//                    Toast.makeText(MainActivity.this, time +" : "+count, Toast.LENGTH_LONG).show();

                    persent = (double) count / (double) whole_count;

                    if (persent > 0.9) {
                        showNotification(getApplicationContext(), "Hu-Ha", "흡입기의 약이 얼마 남지 않았어요 !", Noti_intent);
                    }
                } else {
                    Toast.makeText(BluetoothActivity.this, "흡입기의 약을 다 사용하셨습니다!", Toast.LENGTH_LONG).show();
                }
            }
        });


        checkBluetooth();
        //히히
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

                                                Toast.makeText(BluetoothActivity.this, "흡입기 사용", Toast.LENGTH_SHORT).show();

                                                if (count != whole_count) {
                                                    count++;
                                                    mprogressBar.setProgress(count);
                                                    edit_count.setText(String.valueOf(whole_count - count));
                                                    saveState();

                                                    String time = getTime();
//                                                  Toast.makeText(MainActivity.this, time +" : "+count, Toast.LENGTH_LONG).show();

                                                    persent = (double) count / (double) whole_count;

                                                    if (persent > 0.9) {
                                                        showNotification(getApplicationContext(), "Hu-Ha", "흡입기의 약이 얼마 남지 않았어요 !", Noti_intent);
                                                    }
                                                } else {
                                                    Toast.makeText(BluetoothActivity.this, "흡입기의 약을 다 사용하셨습니다!", Toast.LENGTH_LONG).show();
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

    // 노티 알람 함수.
    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.noti_image)
                .setContentTitle(title)
                .setContentText(body);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    // 노티 알람 함수
    /*
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
    */

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

            Intent intentMyService = new Intent(this, PersistentService.class);
            startService(intentMyService);

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

            case REQUEST_INTENT:
                count = data.getIntExtra("count", 0);
                whole_count = data.getIntExtra("whole_count", 124);

                edit_count.setText(String.valueOf(whole_count - count));
                whole.setText(" / "+String.valueOf(whole_count)+"회");

                mprogressBar.setProgress(count);
                check = true;
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreState();
    }

    protected void restoreState() {

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        if (check) {
            check = false;
        } else {
            if (pref != null) {
                whole_count = pref.getInt("whole_count", 124);
                whole.setText(" / "+String.valueOf(whole_count)+"회");

                count = pref.getInt("count", 0);
                edit_count.setText(String.valueOf(whole_count - count));
                mprogressBar.setProgress(count);
            }
        }
    }

    protected void saveState() {
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt("count", count);
        editor.putInt("whole_count", whole_count);

        editor.commit();
    }

    @Override
    public void onLocationChanged(Location location) {
            /*현재 위치에서 위도경도 값을 받아온뒤 우리는 지속해서 위도 경도를 읽어올것이 아니니
            날씨 api에 위도경도 값을 넘겨주고 위치 정보 모니터링을 제거한다.*/
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        //        sinceOntime.setText(String.valueOf(latitude));
        //        dust_text.setText(String.valueOf(longitude));
        //날씨 가져오기 통신

        String lat = latitude + "";
        String lon = longitude + "";
        getWeather(lat, lon);
        //위치정보 모니터링 제거
        locationManager.removeUpdates(BluetoothActivity.this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void requestLocation() {
        //사용자로 부터 위치정보 권한체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);

        }
    }

    public void getWeather(String latitude, String longitude) {
        final day3Repo[] day3Repos = new day3Repo[1];

        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api2.sktelecom.com/").addConverterFactory(GsonConverterFactory.create()).build();
        day3Repo.days3ApiInterface service = retrofit.create(day3Repo.days3ApiInterface.class);
        Call<day3Repo> call = service.get_3days_retrofit(1, latitude, longitude);
        call.enqueue(new Callback<day3Repo>() {
            @Override
            public void onResponse(Call<day3Repo> call, Response<day3Repo> response) {
                day3Repos[0] = response.body();
                switch (String.valueOf(day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getSky().getCode25hour())) {
                    case "SKY_S01":
                        sky2.setImageResource(R.drawable.main_sunny);
                        break;
                    case "SKY_S02":
                        sky2.setImageResource(R.drawable.main_cloud);
                        break;
                    case "SKY_S03":
                        sky2.setImageResource(R.drawable.main_clouds);
                        break;
                    case "SKY_S04":
                        sky2.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_S05":
                        sky2.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_S06":
                        sky2.setImageResource(R.drawable.main_rainsnow);
                        break;
                    case "SKY_S07":
                        sky2.setImageResource(R.drawable.main_clouds);
                        break;
                    case "SKY_S08":
                        sky2.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_S09":
                        sky2.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_S10":
                        sky2.setImageResource(R.drawable.main_rainsnow);
                        break;
                    case "SKY_S11":
                        sky2.setImageResource(R.drawable.main_thunder);
                        break;
                    case "SKY_S12":
                        sky2.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_S13":
                        sky2.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_S14":
                        sky2.setImageResource(R.drawable.main_rainsnow);
                        break;
                }

                switch (String.valueOf(day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getSky().getCode49hour())) {
                    case "SKY_S01":
                        sky3.setImageResource(R.drawable.main_sunny);
                        break;
                    case "SKY_S02":
                        sky3.setImageResource(R.drawable.main_cloud);
                        break;
                    case "SKY_S03":
                        sky3.setImageResource(R.drawable.main_clouds);
                        break;
                    case "SKY_S04":
                        sky3.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_S05":
                        sky3.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_S06":
                        sky3.setImageResource(R.drawable.main_rainsnow);
                        break;
                    case "SKY_S07":
                        sky3.setImageResource(R.drawable.main_clouds);
                        break;
                    case "SKY_S08":
                        sky3.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_S09":
                        sky3.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_S10":
                        sky3.setImageResource(R.drawable.main_rainsnow);
                        break;
                    case "SKY_S11":
                        sky3.setImageResource(R.drawable.main_thunder);
                        break;
                    case "SKY_S12":
                        sky3.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_S13":
                        sky3.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_S14":
                        sky3.setImageResource(R.drawable.main_rainsnow);
                        break;
                }
                int index = day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getTemperature().getTemp25hour().indexOf(".");
                temp2.setText(String.valueOf(day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getTemperature().getTemp25hour()).substring(0, index) + "℃");
//                temp3.setText(String.valueOf(day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getTemperature().getTemp46hour()));
                index = day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getTemperature().getTemp49hour().indexOf(".");
                temp3.setText(String.valueOf(day3Repos[0].getWeather().getForecast().get(0).getFcst3hour().getTemperature().getTemp49hour()).substring(0, index) + "℃");
            }

            @Override
            public void onFailure(Call<day3Repo> call, Throwable t) {

            }
        });

        final WeatherRepo[] weatherRepo = new WeatherRepo[1];

        Retrofit client = new Retrofit.Builder().baseUrl("https://api2.sktelecom.com/").addConverterFactory(GsonConverterFactory.create()).build();
        WeatherRepo.WeatherApiInterface service2 = client.create(WeatherRepo.WeatherApiInterface.class);
        Call<WeatherRepo> call2 = service2.get_Weather_retrofit(1, latitude, longitude);
        call2.enqueue(new Callback<WeatherRepo>() {
            @Override
            public void onResponse(Call<WeatherRepo> call, Response<WeatherRepo> response) {
                weatherRepo[0] = response.body();
                switch(String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getSky().getCode())){
                    case "SKY_O01":
                        sky1.setImageResource(R.drawable.main_sunny);
                        break;
                    case "SKY_O02":
                        sky1.setImageResource(R.drawable.main_cloud);
                        break;
                    case "SKY_O03":
                        sky1.setImageResource(R.drawable.main_clouds);
                        break;
                    case "SKY_O04":
                        sky1.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_O05":
                        sky1.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_O06":
                        sky1.setImageResource(R.drawable.main_rainsnow);
                        break;
                    case "SKY_O07":
                        sky1.setImageResource(R.drawable.main_clouds);
                        break;
                    case "SKY_O08":
                        sky1.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_O09":
                        sky1.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_O10":
                        sky1.setImageResource(R.drawable.main_rainsnow);
                        break;
                    case "SKY_O11":
                        sky1.setImageResource(R.drawable.main_thunder);
                        break;
                    case "SKY_O12":
                        sky1.setImageResource(R.drawable.main_rain);
                        break;
                    case "SKY_O13":
                        sky1.setImageResource(R.drawable.main_snow);
                        break;
                    case "SKY_O14":
                        sky1.setImageResource(R.drawable.main_rainsnow);
                        break;
                }

                int index2 = String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getTemperature().getTc()).indexOf(".");
                temp1.setText(String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getTemperature().getTc()).substring(0, index2) + "℃");
            }

            @Override
            public void onFailure(Call<WeatherRepo> call, Throwable t) {

            }
        });

    }

    public void initView() {
        sky1 = findViewById(R.id.sky1);
        sky2 = findViewById(R.id.sky2);
        sky3 = findViewById(R.id.sky3);
        temp1 = findViewById(R.id.temp1);
        temp2 = findViewById(R.id.temp2);
        temp3 = findViewById(R.id.temp3);
    }
}