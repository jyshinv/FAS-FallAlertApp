package org.imfine.fas2.Post;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFASPost {

   public String value;
   public String name;
   public String time;

    public FirebaseFASPost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }
    public FirebaseFASPost(String time, String name, String value) {
       this.value = value;
       this.name = name;
       this.time = time;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("value", value);
        result.put("time",time);
        return result;
    }
}
