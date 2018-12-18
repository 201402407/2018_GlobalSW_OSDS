package com.example.administrator.huha;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class PersistentService extends Service implements Runnable {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 서비스가 죽었을때 알람 등록
        super.onDestroy();
    }

    // 실행 메뉴에서 지울 때 (Stopself() 함수 삭제함)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        // 푸시 알람 테스트
        Intent mintent = new Intent(this, SplashActivity.class);
        showNotification(this, "Hu-Ha", "앱이 종료되었어요! 다시 실행해주세요.", mintent);

        stopSelf();
    }

    /**
     * (non-Javadoc)
     * @see Service#onStart(Intent, int)
     *
     * 서비스가 시작되었을때 run()이 실행되기까지 delay를 handler를 통해서 주고 있다.
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // mStartId = startId;

        // mHandler = new Handler();
      //  mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);

        return START_NOT_STICKY;
    }

    /**
     * (non-Javadoc)
     * @see Runnable#run()
     *
     * 서비스가 돌아가고 있을때 실제로 내가 원하는 기능을 구현하는 부분
     */
    @Override
    public void run() {

       // mHandler.postDelayed(this, LOCATION_UPDATE_DELAY);
    }

    // 푸시(노티) 알람을 받는 기능 + 해당 노티 누르면 띄우는 인텐트 포함.
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

    /**
     * 서비스가 시스템에 의해서 또는 강제적으로 종료되었을 때 호출되어
     * 알람을 등록해서 10초 후에 서비스가 실행되도록 한다.
     */

    /*
    private void registerRestartAlarm() {

        Log.d("PersistentService", "registerRestartAlarm()");

        Intent intent = new Intent(this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += REBOOT_DELAY_TIMER; // 10초 후에 알람이벤트 발생

   //     Toast.makeText(this, "종료되도 알람서비스로 다시시작한다!", Toast.LENGTH_SHORT).show();

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,REBOOT_DELAY_TIMER, sender);
    }
    */

    /**
     * 기존 등록되어있는 알람을 해제한다.
     */

    /*
    private void unregisterRestartAlarm() {

        Log.d("PersistentService", "unregisterRestartAlarm()");
        Intent intent = new Intent(PersistentService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(PersistentService.this, 0, intent, 0);

    //    Toast.makeText(this, "알람서비스 끈다!", Toast.LENGTH_SHORT).show();
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    */

}
