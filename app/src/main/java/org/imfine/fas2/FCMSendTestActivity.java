package org.imfine.fas2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSendTestActivity extends AppCompatActivity {

    EditText et;
    Button btn;
    TextView tv;

    static RequestQueue requestQueue;
    static String regId="eNhYhgLMHKs:APA91bH7uUnQ2NJ87DZLW-B4wMcAvKhYlgWIGDWxaFF6n4FZsl2QKSAVVlD6bt-SPH91oWs7D5iqGPFqSM8fE2pYJa0_4Q0kk53d6oTgKFxvLM_9vW7nYkzcXnsHsbFvFlDbBiizn2AN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmsend_test);

        et = findViewById(R.id.fcm_et);
        btn = findViewById(R.id.fcm_btn);
        tv = findViewById(R.id.fcm_tv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = et.getText().toString();
                send(input);
            }
        });

        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }


    }

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

        sendData(requestData, new SendResponseListener() {
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

    public void sendData(JSONObject requestData, final SendResponseListener listener){
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




}
