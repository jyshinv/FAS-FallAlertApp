package org.imfine.fas2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;
    String ID, name = "", gender = "";
    long age = 0;
    String sort = "id";
    EditText ageET, nameET, genderET, idET;
    Button btn;
    ListView listView;
    ArrayList<String> data;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<String>();
        ageET = (EditText)findViewById(R.id.ageet);
        nameET = (EditText)findViewById(R.id.nameet);
        genderET = (EditText)findViewById(R.id.genderet);
        idET = (EditText)findViewById(R.id.idet);
        btn = (Button)findViewById(R.id.button);
        listView = (ListView)findViewById(R.id.datalist);
        mPostReference = FirebaseDatabase.getInstance().getReference();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ID = idET.getText().toString();
                name = nameET.getText().toString();
                gender = genderET.getText().toString();
                String aget = ageET.getText().toString();
                if ((ID.length() * name.length() * gender.length() * aget.length()) == 0) {
                    Toast.makeText(MainActivity.this, "Data is missing", Toast.LENGTH_SHORT).show();
                } else {
                    age = Long.parseLong(aget);
                    postFirebaseDatabase(true);
                }
            }
        });
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        getFirebaseDatabase();




    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is Updated");
                data.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    String[] info = {get.id, get.name, String.valueOf(get.age), get.gender};
                    String result = info[0] + " : " + info[1] + "(" + info[3] + ", " + info[2] + ")";
                    data.add(result);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2] + info[3]);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(data);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mPostReference.child("id_list").addValueEventListener(postListener);
    }

    public void postFirebaseDatabase(boolean add){
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(ID, name, age, gender);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + ID, postValues);
        mPostReference.updateChildren(childUpdates);
        clearET();
    }

    public void clearET () {
        genderET.setText("");
        ageET.setText("");
        nameET.setText("");
        idET.setText("");
        gender = "";
        age = 0;
        name = "";
        ID = "";
    }



}












