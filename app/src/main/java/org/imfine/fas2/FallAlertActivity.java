package org.imfine.fas2;


import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class FallAlertActivity extends AppCompatActivity implements SensorEventListener {

    Button btn;
    Intent intent;

    int accelXValue;
    int accelYValue;
    int accelZValue;

    int gyroX;
    int gyroY;
    int gyroZ;


    private SensorManager mSensorManager;
    private Sensor mGyroscope;
    private Sensor accSensor;
    private TextView acc_x_textView;
    private TextView acc_y_textView;
    private TextView acc_z_textView;
    private TextView gyro_x_textView;
    private TextView gyro_y_textView;
    private TextView gyro_z_textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_alert);
        btn = findViewById(R.id.backbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(),Main2Activity.class);
                startActivity(intent);
            }
        });

        acc_x_textView = (TextView)findViewById(R.id.acc_x_value);
        acc_y_textView = (TextView)findViewById(R.id.acc_y_value);
        acc_z_textView = (TextView)findViewById(R.id.acc_z_value);

        gyro_x_textView = (TextView)findViewById(R.id.gyro_x_value);
        gyro_y_textView = (TextView)findViewById(R.id.gyro_y_value);
        gyro_z_textView = (TextView)findViewById(R.id.gyro_z_value);

        //센서 매니저 얻기
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //자이로스코프 센서(회전)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //엑셀로미터 센서(가속)
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = Math.round(event.values[0] * 1000);
            gyroY = Math.round(event.values[1] * 1000);
            gyroZ = Math.round(event.values[2] * 1000);
            gyro_x_textView.setText("gyro x : "+ gyroX);
            gyro_y_textView.setText("gyro y : "+ gyroY);
            gyro_z_textView.setText("gyro z : "+ gyroZ);



        }
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelXValue = (int) event.values[0];
            accelYValue = (int) event.values[1];
            accelZValue = (int) event.values[2];
            acc_x_textView.setText("acc x : "+ accelXValue);
            acc_y_textView.setText("acc y : "+ accelYValue);
            acc_z_textView.setText("acc z : "+ accelZValue);

        }

    }

    //사용안함
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 주기 설명
    // SENSOR_DELAY_UI 갱신에 필요한 정도 주기
    // SENSOR_DELAY_NORMAL 화면 방향 전환 등의 일상적인  주기
    // SENSOR_DELAY_GAME 게임에 적합한 주기
    // SENSOR_DELAY_FASTEST 최대한의 빠른 주기


    //리스너 등록
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    //리스너 해제
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



}