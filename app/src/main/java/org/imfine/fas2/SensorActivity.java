package org.imfine.fas2;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class SensorActivity extends AppCompatActivity{
    TextView textView;

    SensorManager manager;
    List<Sensor> sensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        textView = findViewById(R.id.sensor_tv);

        Button button = findViewById(R.id.sensor_btn_sensorlist);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSensorList();
            }
        });

        Button button2 = findViewById(R.id.sensor_btn_firstsensor);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFirstSensor();
            }
        });
    }

    public void getSensorList() {
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensors = manager.getSensorList(Sensor.TYPE_ALL);

        int index = 0;
        for (Sensor sensor : sensors) {
            println("#" + index + " : " + sensor.getName());
        }
    }

    public void registerFirstSensor() {
        manager.registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        String output = "Sensor Timestamp : " + event.timestamp + "\n\n";
                        for(int index = 0; index < event.values.length; ++index) {
                            output += ("Sensor Value #" + (index + 1) + " : " + event.values[index] + "\n");
                        }
                        println(output);
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                },
                sensors.get(0),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void println(String data) {
        textView.append(data + "\n");
    }

}
