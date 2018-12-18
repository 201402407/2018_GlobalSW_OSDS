package com.example.administrator.huha.Gayeon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.administrator.huha.R;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class Tmap extends AppCompatActivity {

    TMapView tmapview;
    TMapGpsManager tmapgps = null;
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    TMapMarkerItem markerItem1 = new TMapMarkerItem();
    TMapPoint tMapPoint1 = new TMapPoint(127.345955, 36.370203);

    // 마커 아이콘
    Bitmap bitmap = BitmapFactory.decodeResource(Tmap.this.getResources(), R.drawable.marker);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmap);

        FrameLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        tmapview = new TMapView(this);

        tmapview.setSKTMapApiKey("ef89c5c4-fe59-4469-9dca-4792af9f3d22");

        //tmapview.setCompassMode(true);
        tmapview.setIconVisibility(false); //현재 위치로 표시될 아이콘을 표시할지 여부를 설정
        tmapview.setZoomLevel(15);
        //tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN); //언어 선택(기본언어는 한국어)
        tmapview.setCenterPoint(127.345955, 36.370203);
        linearLayoutTmap.addView(tmapview);



        markerItem1.setIcon(bitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
        markerItem1.setName("충남대학교"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가


        //tmapview.setTrackingMode(true);
        //tmapview.setSightVisible(true);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
                Log.d("locationTest","동의알림");
            }
            return;
        }
        setGps();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d("locationTest","onLocationChanged");
            //현재위치의 좌표를 알수있는 부분
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tmapview.setLocationPoint(longitude, latitude);
                tmapview.setCenterPoint(longitude, latitude);
                Log.d("locationTest", longitude+","+latitude);

            }

        }

        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    public void setGps() {

        Log.d("locationTest","setGps");
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setGps();

                } else {
                    Log.d("locationTest","동의거부함");
                }
                return;
            }

        }
    }


}
