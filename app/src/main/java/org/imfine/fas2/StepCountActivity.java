package org.imfine.fas2;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.imfine.fas2.Post.FirebaseFASPost;

import java.util.HashMap;
import java.util.Map;

public class StepCountActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    TextView tvStepCounter;
    TextView tvStepCounter2;
    private int mStepDetector;
    private DatabaseReference mPostReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);
        mPostReference = FirebaseDatabase.getInstance().getReference();

        tvStepCounter = (TextView)findViewById(R.id.tvStepCount);
        tvStepCounter2 = (TextView)findViewById(R.id.tvStepCount2);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if(stepDetectorSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor", Toast.LENGTH_SHORT).show();
        }

        if(mStepDetector == 10){
            tvStepCounter2.setText("30달성!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if(event.values[0] == 1.0f) {
                mStepDetector++;
                tvStepCounter.setText("Step Detect : " + String.valueOf(mStepDetector));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    public void FBRegister(String Alarm){
//        Map<String, Object> childUpdates = new HashMap<>();
//        Map<String, Object> postValues = null;
//
//        FirebaseFASPost post = new FirebaseFASPost(Alarm);
//        postValues = post.toMap();
//
//        childUpdates.put("/alarm_list/" + Alarm, postValues);
//        mPostReference.updateChildren(childUpdates);
//
//    }

} //end of activity