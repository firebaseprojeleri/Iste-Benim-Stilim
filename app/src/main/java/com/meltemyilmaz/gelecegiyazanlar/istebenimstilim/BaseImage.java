package com.meltemyilmaz.gelecegiyazanlar.istebenimstilim;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Meltem YILMAZ on 28.04.2017.
 */

public class BaseImage {

    public String information;
    public String userInformation;
    public String imageURL;
    public int favoriteCount = 0;

    public BaseImage() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public BaseImage(String information, String userInformation, String imageURL, int favoriteCount) {
        super();
        this.information = information;
        this.userInformation = userInformation;
        this.imageURL = imageURL;
        this.favoriteCount = favoriteCount;
    }

    public String getInformation(){
        return information;
    }
    public String getUserInformation(){
        return userInformation;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("information", information);
        result.put("userInformation", userInformation);
        result.put("imageURL", imageURL);
        result.put("favoriteCount", favoriteCount);
        return result;
    }
}
