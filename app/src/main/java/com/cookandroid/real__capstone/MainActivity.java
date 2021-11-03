package com.cookandroid.real__capstone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mySensorManager;
    private Sensor s;

    TextView x,y,z;
    double longitude;
    double latitude;
    double altitude;
    double pitch;

    TextView txtResult;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("정보 수집용");

        //위도 경도 받기용

        txtResult = (TextView)findViewById(R.id.txtResult);



        //각도 받기용
        x=(TextView)findViewById(R.id.x);
        y=(TextView)findViewById(R.id.y);
        z=(TextView)findViewById(R.id.z);


        mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        s=mySensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
            // lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);
    }

    public void onButton1Clicked(View view){
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://eminwon.asan.go.kr/emwp/gov/mogaha/ntis/web/emwp/cmmpotal/action/EmwpMainMgtAction.do"));
        startActivity(intent);
    }

    public void onButton2Clicked(View view){

        databaseReference.child("test").push().setValue(1);
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onProviderEnabled(Location location) {
            databaseReference.child("test").push().setValue(3);
        }

        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            altitude = location.getAltitude();

            txtResult.setText("위치정보 : " + provider + "\n" +
                    "위도 : " + longitude + "\n" +
                    "경도 : " + latitude + "\n" +
                    "고도  : " + altitude);

            writegps(LocalDateTime.now(),longitude, latitude, (int)pitch );
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("provider", provider + status);
        }

        public void onProviderEnabled(String provider) {
            Log.i("enabled", provider);
        }

        public void onProviderDisabled(String provider) {
            Log.i("disabled", provider);
        }
    };

    @Override
    protected  void onResume(){
        super.onResume();
        mySensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_UI);
        //mySensorManager.registerListener(gyroListener, myGyroscope, SensorManager.SENSOR_DELAY_UI);

        while ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000,
                1,
                gpsLocationListener);
    }

    protected void onPause(){
        super.onPause();
        lm.removeUpdates(gpsLocationListener);
        mySensorManager.unregisterListener(this);
        //mySensorManager.unregisterListener(gyroListener);
    }

    protected void onStop(){
        super.onStop();
    }

    private void writegps(LocalDateTime realtime, Double longg, Double langg, int slopee)
    {
        gps_info information=new gps_info(realtime.toString(), longg, langg, slopee);

        databaseReference.child("gps").push().setValue(information);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){
//            String str="방향센서값 \n\n"
//                    +"\n방위각 : "+event.values[0]
//                    +"\n피치 : "+event.values[1]
//                    +"\n롤 : "+event.values[2];
                pitch=event.values[1];

                     x.setText("[각도]" + String.format("%.1f",  event.values[1]));
                     y.setText("[방위]" + String.format("%.1f", event.values[0]));
                    z.setText("[좌우회전]" + String.format("%.1f",  event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
