package com.example.administrator.huha.Gayeon;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.huha.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
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
    Button plus;
    ImageView reset;

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();
        count = intent.getIntExtra("count", 0);

        background = (ProgressBar) findViewById(R.id.circular_progress_bar_background);
        background.setProgress(100);
        mprogressBar = (ProgressBar) findViewById(R.id.circular_progress_bar);
        mprogressBar.setProgress(count);

        plus = (Button) findViewById(R.id.plus);
        edit_count = (EditText) findViewById(R.id.edit_count);
        reset = (ImageView) findViewById(R.id.reset);

        mprogressBar.setProgress(count);
        edit_count.setText(String.valueOf(count));

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
                    if(!TextUtils.isEmpty(tokenID)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.menu_home){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("count", count);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        return true;
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

}
