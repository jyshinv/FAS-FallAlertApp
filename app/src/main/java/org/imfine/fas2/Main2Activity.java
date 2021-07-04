package org.imfine.fas2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.preference.PreferenceManager;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.imfine.fas2.Post.FirebaseFASPost;
import org.imfine.fas2.Post.FirebaseRaspPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.max;


public class Main2Activity extends AppCompatActivity implements SensorEventListener {

    //list 확인 버튼
    Button btn;
    Intent intent;


    //파이어베이스 db연동을 위한 코드
    private DatabaseReference mPostReference;
    String NAME = "";
    String VALUE = "";
    String TIME = "";

    //파이어베이스 db연동 라즈베리파이
    String FALLCHECK = "false";

    //현재 날짜와 시간을 위한 변수
    long now;
    private DecimalFormat df = new DecimalFormat("#.###");


    //기존 알고리즘 변수
    private String boundary_key;
    private String phoneNumber_key;
    private String notificationMethod_key;

    private TextView last_x_textView;
    private TextView last_y_textView;
    private TextView last_z_textView;
    private TextView x_textView;
    private TextView y_textView;
    private TextView z_textView;
    private TextView current_change_amount_textView;
    private TextView current_boundary_textView;
    private TextView gyro1;
    private TextView gyro2;
    private TextView gyro3;
    private TextView acc_svm;
    private TextView av_svm;
    private TextView timeUI;
    private TextView maxACC;
    private TextView maxAV;

    private Sensor sensor;
    private Sensor Gsensor;
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;

    private double[] last_gravity = new double[3];
    private double[] gravity = new double[3];
    private double[] ggravity = new double[3];
    private boolean firstChange = true;
    public int warningBoundary = 700;
    private double changeAmount = 0;


    //fas알고리즘을 위한 변수
    double var_acc_svm;
    double result_acc;
    double var_av_svm;
    double result_av;
    boolean check = true;
    int i;

    //logcat avsvm, accsvm 나타내기 위한 변수
    double accsvm_max = 0;
    double avsvm_max = 0;

    //fcm을 위한 변수
    static RequestQueue requestQueue;
    static String regId="eDT6FiOyaMQ:APA91bG0GgDtAfovt4sy210IZT-y0Xt1RTeHZ6cWDW4vVLFiF0B_j6_KJeugFeCqj8ubt9aeda_KHKXzC84b_8qswuwnyPY2vxiIwnEw43mc5kRkuwR5m0dzcjimYrLR0ZnqWsEUstWe";
    TextView tv;
    //군우아버지 폰 : eNhYhgLMHKs:APA91bH7uUnQ2NJ87DZLW-B4wMcAvKhYlgWIGDWxaFF6n4FZsl2QKSAVVlD6bt-SPH91oWs7D5iqGPFqSM8fE2pYJa0_4Q0kk53d6oTgKFxvLM_9vW7nYkzcXnsHsbFvFlDbBiizn2AN"
    //myphone : cEYRkBd6PSc:APA91bGap-XU9q8EYy3rjQblGHdZ9G5jkL3O1VOgpQd-WaBbC9rvwyiFag3ZYL_YbgT5Ca7tDlZZ7lDw8fjkTX4H9HDsNql_6eKSdEqgg4YyV8W6Y-pWlHim6dwdpWR7rtpFSMB2i-Oa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        btn = findViewById(R.id.sensor_list_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplication(), FallAlertActivity.class);
                startActivity(intent);
            }
        });

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        tv = findViewById(R.id.main2_fcm_tv);


        //파이어베이스
        mPostReference = FirebaseDatabase.getInstance().getReference();

        //기존 알고리즘
        boundary_key = getString(R.string.key_boundary);
        phoneNumber_key = getString(R.string.key_phone_number);
        notificationMethod_key = getString(R.string.key_notification_method);
        initialUI();
        initialSensor();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        warningBoundary = Integer.parseInt(sharedPreferences.getString(boundary_key, "20"));




    } // end of onCreate


    public void initialUI() {
        last_x_textView = (TextView)findViewById(R.id.last_x_value);
        last_y_textView = (TextView)findViewById(R.id.last_y_value);
        last_z_textView = (TextView)findViewById(R.id.last_z_value);
        x_textView = (TextView)findViewById(R.id.x_value);
        y_textView = (TextView)findViewById(R.id.y_value);
        z_textView = (TextView)findViewById(R.id.z_value);
        current_change_amount_textView = (TextView)findViewById(R.id.current_change_amount);
        current_boundary_textView = (TextView)findViewById(R.id.current_boundary);
        gyro1 = findViewById(R.id.gyro1);
        gyro2 = findViewById(R.id.gyro2);
        gyro3 = findViewById(R.id.gyro3);
        acc_svm = findViewById(R.id.acc_svm);
        av_svm = findViewById(R.id.av_svm);
        timeUI = findViewById(R.id.time);
        maxACC = findViewById(R.id.maxACC);
        maxAV = findViewById(R.id.maxAV);

    }

    public void initialSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        sensorManager.registerListener(this,sensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,Gsensor,sensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if(sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

            //기존 알고리즘
            last_gravity[0] = gravity[0];
            last_gravity[1] = gravity[1];
            last_gravity[2] = gravity[2];

            gravity[0] = Double.parseDouble(String.format("%.2f",event.values[0]));
            gravity[1] = Double.parseDouble(String.format("%.2f",event.values[1]));
            gravity[2] = Double.parseDouble(String.format("%.2f",event.values[2]));

            changeAmount = Math.pow((gravity[0]-last_gravity[0]), 2) +
                    Math.pow((gravity[1]-last_gravity[1]), 2) +
                    Math.pow((gravity[2]-last_gravity[2]), 2);


            //fas 알고리즘
            var_acc_svm =
                    (Math.pow(gravity[0], 2)) + (Math.pow(gravity[1], 2)) + (Math.pow(gravity[2], 2));

            result_acc = Math.sqrt(var_acc_svm);
            result_acc = Double.parseDouble(String.format("%.2f",result_acc));
            acc_svm.setText("acc_svm : " +result_acc );

            //max값 띄우기
            accsvm_max = max(accsvm_max, result_acc);
            maxACC.setText("maxACC : " + accsvm_max);



        }


        if(sensor.getType() == Sensor.TYPE_GYROSCOPE){

            ggravity[0] = Double.parseDouble(String.format("%.2f",event.values[0]));
            ggravity[1] = Double.parseDouble(String.format("%.2f",event.values[1]));
            //ggravity[2] = Double.parseDouble(String.format("%.2f",event.values[2]));

            var_av_svm = (Math.pow(ggravity[0], 2)) + (Math.pow(ggravity[1], 2));
            result_av = (Math.sqrt(var_av_svm));
            result_av = Double.parseDouble(String.format("%.2f",result_av));

            av_svm.setText("av_svm : " +result_av);

            //logcat
            avsvm_max= max(avsvm_max, result_av);
            maxAV.setText("maxAV : " + avsvm_max);



        }


        //실시간 값 변동 보여주는 함수
        updateSensorView();

        //fas 알고리즘 계산 함수
        fall_detect();//값이 바뀔때마다 계속 불러냄

        //warningBoundary 설정은 여기서!!
        //warningBoundary = Integer.parseInt(sharedPreferences.getString(boundary_key, "2000"));

        //낙상이 감지 되었을 경우 코드
        //if (!firstChange && changeAmount >= warningBoundary) {


//            Toast.makeText(this, "낙상이 감지되었습니다.", Toast.LENGTH_SHORT).show();
//            now = System.currentTimeMillis();
//            Date date = new Date(now);
//            SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            TIME = sdfnow.format(date);
//            NAME = "김옥례 할머니";
//            VALUE = "낙상 감지됨!!!";
//            FBRegister();

        //}

        //firstChange = false;



    }

    class Time extends Thread{

        public void run(){
            i=0;
            while(true){
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss") ;
                String disp = sdf.format(dt);
                i++;
                System.out.println( i + "회 현재시간" + disp);
                try{ Thread.sleep(1000);} catch(Exception ex){}

            }//while end

        }//run() end

    }//class END

    public void fall_detect() {

        if( result_av > 10 )
        {
            Time t = new Time();
            t.start();
            Timestamp time_st = new Timestamp(System.currentTimeMillis());
            timeUI.setText("시간 : " + time_st.toString());

            long start = System.currentTimeMillis();//시작시간

                if( result_acc > 10 )
                {
                    long end = System.currentTimeMillis();//끝난시간.
                    if(end-start <= 50000) { //50초이내 되야함.
                        //Toast.makeText(this, "낙상이다 새꺄", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(this, "낙상이 감지되었습니다.", Toast.LENGTH_SHORT).show();
                        now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        TIME = "낙상 시간 : "+sdfnow.format(date);
                        NAME = "낙상인 : 김옥례 할머니";
                        VALUE = "낙상 의심";
                        FALLCHECK = "true";

                        //fcm을 위한 send함수
                        send(VALUE);

                        //라즈베리파이에 true값 보내는 함수
                        FBtoRasp();

                        //파이어베이스에 낙상 시 낙상정보 올리는 함수
                        FBRegister();



                    }
                }
        }


    } // end of fall detect()

    //fcm 구현 코드
    public void send(String input){

        JSONObject requestData = new JSONObject();

        try{
            requestData.put("priority","high");
            JSONObject dataObj = new JSONObject();
            dataObj.put("contents",input);
            requestData.put("data",dataObj);

            JSONArray idArray = new JSONArray();
            idArray.put(0,regId);
            requestData.put("registration_ids",idArray);

        }catch (Exception e){
            e.printStackTrace();

        }

        sendData(requestData, new FCMSendTestActivity.SendResponseListener() {
            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨");

            }

            @Override
            public void onRequesCompleted() {
                println("onRequestCompleted() 호출됨");

            }

            @Override
            public void onRequestWithError(VolleyError error) {
                println("onRequestWithError() 호출됨");
            }
        });




    }//end of send()

    public void println(String data){
        tv.append(data + "\n");
    }

    public interface SendResponseListener{
        public void onRequestStarted();
        public void onRequesCompleted();
        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final FCMSendTestActivity.SendResponseListener listener){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequesCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //return super.getParams();
                Map<String, String> params = new HashMap<String,String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //return super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",
                        "key=AAAAzsqzkm0:APA91bGVcTX8EbKAYC0fxicmK0Jjv1lB9xeDBpEh6N0y3TYCDk5SqrtjBIjcvBT2Ji8_vNS3SfIInh1SU-WaW8YqOQdATMgOoBnRQ5aNH73nOZdPWGFlthEfZYLD6Eb_YiMPdYm-S0SY");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                //return super.getBodyContentType();
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);

    }//end of sendData




    //리스너 등록
    @Override
    protected void onResume() {
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, Gsensor,sensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    //리스너 해제
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    //센서값을 보여주기 위한 함수
    private void updateSensorView() {
        last_x_textView.setText("Last X = "+last_gravity[0]);
        last_y_textView.setText("Last Y = "+last_gravity[1]);
        last_z_textView.setText("Last Z = "+last_gravity[2]);
        x_textView.setText("X = "+gravity[0]);
        y_textView.setText("Y = "+gravity[1]);
        z_textView.setText("Z = "+gravity[2]);
        current_change_amount_textView.setText("Current change amount = "+df.format(changeAmount));
        current_boundary_textView.setText("Current boundary = "+ warningBoundary);
        gyro1.setText("gyro x : " + ggravity[0]);
        gyro2.setText("gyro y : " + ggravity[1]);
        gyro3.setText("gyro z : " + ggravity[2]);


    }

    //낙상 정보 db에 기록하기
    public void FBRegister(){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        FirebaseFASPost post = new FirebaseFASPost(TIME,NAME,VALUE);
        postValues = post.toMap();

        childUpdates.put("/alarm_list/"+ TIME, postValues);
        mPostReference.updateChildren(childUpdates);

        Toast.makeText(this,"update completed",Toast.LENGTH_SHORT).show();

    }

    //라즈베리파이 카메라에 보낼 값 설정
    public void FBtoRasp(){
        Map<String,Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        FirebaseRaspPost post = new FirebaseRaspPost(FALLCHECK);
        postValues = post.toMap();

        childUpdates.put("/FallCheckToRasp/", postValues);
        mPostReference.updateChildren(childUpdates);

    }


} // end of activity
