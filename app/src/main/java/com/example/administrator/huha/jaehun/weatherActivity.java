package com.example.administrator.huha.jaehun;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.huha.R;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public class weatherActivity extends AppCompatActivity implements LocationListener {//, View.OnClickListener {
    LocationManager locationManager;
    double latitude;
    double longitude;
    TextView sinceOntime, grid, temperature, humidity_text, wind_text, dust_text, sky_name;
    ImageView weather, humidity, wind, dust;
//    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initView();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        requestLocation();

    }

    private void initView() {
        //뷰세팅
        weather = (ImageView) findViewById(R.id.sky);
        sinceOntime = (TextView) findViewById(R.id.sinceOntime);
        grid = (TextView) findViewById(R.id.grid);
        temperature = (TextView) findViewById(R.id.temperature);
        humidity_text = (TextView) findViewById(R.id.humidity_text);
        wind_text = (TextView) findViewById(R.id.wind_text);
        dust_text = (TextView) findViewById(R.id.dust_text);
        sky_name = (TextView) findViewById(R.id.skyname);
        humidity = (ImageView) findViewById(R.id.humidity);
        wind = (ImageView) findViewById(R.id.wind);
        dust = (ImageView) findViewById(R.id.dust);

        humidity.setImageResource(R.drawable.humidity);
        wind.setImageResource(R.drawable.wind);
        dust.setImageResource(R.drawable.dust);
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
        locationManager.removeUpdates(weatherActivity.this);
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
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            //버튼 클릭시 현재위치의 날씨를 가져온다
//            case R.id.button:
//                if (locationManager != null) {
//                    requestLocation();
//                }
//
//                break;
//        }
//    }

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


//    private interface ApiService {
//        //베이스 Url
//        String BASEURL = "https://api2.sktelecom.com/";
//        String APPKEY = "e35f4512-2486-4f52-aec2-2bef6d636191";
//
//        //get 메소드를 통한 http rest api 통신
//        @GET("weather/current/minutely")
//        Call<JsonObject> getMinutely(@Header("appkey") String appKey, @Query("version") int version,
//                                   @Query("lat") double lat, @Query("lon") double lon);
//
//    }

    private void getWeather(String latitude, String longitude) {
        //sk planet api 이용하여 날씨 정보 받아오기
        final WeatherRepo[] weatherRepo = new WeatherRepo[1];

        Retrofit client = new Retrofit.Builder().baseUrl("https://api2.sktelecom.com/").addConverterFactory(GsonConverterFactory.create()).build();
        WeatherRepo.WeatherApiInterface service = client.create(WeatherRepo.WeatherApiInterface.class);
        Call<WeatherRepo> call = service.get_Weather_retrofit(1, latitude, longitude);
        call.enqueue(new Callback<WeatherRepo>() {
            @Override
            public void onResponse(Call<WeatherRepo> call, Response<WeatherRepo> response) {
                if (response.isSuccessful()) {
                    weatherRepo[0] = response.body();
                    if (weatherRepo[0].getResult().getCode().equals("9200")) { // 9200 = 성공
                        switch (String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getSky().getCode())) {
                            case "SKY_O01":
                                weather.setImageResource(R.drawable.sunny);
                                break;
                            case "SKY_O02":
                                weather.setImageResource(R.drawable.cloud);
                                break;
                            case "SKY_O03":
                                weather.setImageResource(R.drawable.clouds);
                                break;
                            case "SKY_O04":
                                weather.setImageResource(R.drawable.rain);
                                break;
                            case "SKY_O05":
                                weather.setImageResource(R.drawable.snow);
                                break;
                            case "SKY_O06":
                                weather.setImageResource(R.drawable.rainsnow);
                                break;
                            case "SKY_O07":
                                weather.setImageResource(R.drawable.clouds);
                                break;
                            case "SKY_O08":
                                weather.setImageResource(R.drawable.rain);
                                break;
                            case "SKY_O09":
                                weather.setImageResource(R.drawable.snow);
                                break;
                            case "SKY_010":
                                weather.setImageResource(R.drawable.rainsnow);
                                break;
                            case "SKY_011":
                                weather.setImageResource(R.drawable.thunder);
                                break;
                            case "SKY_012":
                                weather.setImageResource(R.drawable.rain);
                                break;
                            case "SKY_013":
                                weather.setImageResource(R.drawable.snow);
                                break;
                            case "SKY_014":
                                weather.setImageResource(R.drawable.rainsnow);
                                break;
                        }

                        switch (String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getPrecipitation().getType())) {
                            case "0":
                                sinceOntime.setText("강수정보없음 " + String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getPrecipitation().getSinceOntime()) + "mm");
                                break;
                            case "1":
                                sinceOntime.setText("비" + String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getPrecipitation().getSinceOntime()) + "mm");
                                break;
                            case "2":
                                sinceOntime.setText("비/눈" + String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getPrecipitation().getSinceOntime()) + "mm");
                                break;
                            case "3":
                                sinceOntime.setText("눈" + String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getPrecipitation().getSinceOntime()) + "cm");
                                break;
                        }

                        sky_name.setText(String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getSky().getName()));
                        temperature.setText(String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getTemperature().getTc()) + "℃");
                        humidity_text.setText("습도 " + String.valueOf(weatherRepo[0].getWeather().getHourly().get(0).getHumidity()) + "%");
                        wind_text.setText("풍속 " + weatherRepo[0].getWeather().getHourly().get(0).getWind().getWspd() + "m/s");
                        grid.setText(weatherRepo[0].getWeather().getHourly().get(0).getGrid().getCounty() + " " + weatherRepo[0].getWeather().getHourly().get(0).getGrid().getVillage());
                    } else {
                        temperature.setText("fail");
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherRepo> call, Throwable t) {

            }
        });

    }
}
