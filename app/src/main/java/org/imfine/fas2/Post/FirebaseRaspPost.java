package org.imfine.fas2.Post;

import java.util.HashMap;
import java.util.Map;

public class FirebaseRaspPost {

    public String fallcheck;

    public FirebaseRaspPost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }
    public FirebaseRaspPost(String fallcheck) {
        this.fallcheck = fallcheck;

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fallcheck", fallcheck);
        return result;
    }
}
