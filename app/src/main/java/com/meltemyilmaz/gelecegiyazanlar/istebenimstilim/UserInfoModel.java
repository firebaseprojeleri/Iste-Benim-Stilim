package com.meltemyilmaz.gelecegiyazanlar.istebenimstilim;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Meltem YILMAZ on 30.04.2017.
 */

public class UserInfoModel {

    public String nameSurname;
    public String nickName;
    public String phone;
    public String imageURL;
    //public Map<String, Boolean> result = new HashMap<>();

    public UserInfoModel() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public UserInfoModel(String nameSurname, String nickName, String phone, String imageURL) {
        this.nameSurname = nameSurname;
        this.nickName = nickName;
        this.phone = phone;
        this.imageURL = imageURL;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nameSurname", nameSurname);
        result.put("nickName", nickName);
        result.put("phone", phone);
        result.put("imageURL", imageURL);
        return result;
    }
}
