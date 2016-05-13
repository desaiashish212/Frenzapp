package com.rishi.frendzapp_core.models;

import com.rishi.frendzapp_core.utils.ConstsCore;

import java.io.Serializable;

public class UserCustomData implements Serializable {

    public static String TAG_AVATAR_URL = "avatar_url";
    public static String TAG_STATUS = "status";
    public static String TAG_AUDIO_STATUS = "audio_status";
    public static String TAG_DOB = "dob";
    public static String TAG_CITY = "city";
    public static String TAG_IS_IMPORT = "is_import";

    private String avatar_url;
    private String status;
    private String audio_status;
    private String dob;
    private String city;
    private String is_import;

    public UserCustomData() {
        avatar_url = ConstsCore.EMPTY_STRING;
        status = ConstsCore.EMPTY_STRING;
        audio_status = ConstsCore.EMPTY_STRING;
        dob = ConstsCore.EMPTY_STRING;
        city = ConstsCore.EMPTY_STRING;
    }

    public UserCustomData(String avatar_url, String status,String audio_status,String dob,String city, String is_import) {
        this.avatar_url = avatar_url;
        this.status = status;
        this.audio_status = audio_status;
        this.dob = dob;
        this.city = city;
        this.is_import = is_import;
    }

    public String isIs_import() {
        return is_import;
    }

    public void setIs_import(String is_import) {
        this.is_import = is_import;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAudio_status(){
        return audio_status;
    }

    public void setAudio_status(String audio_status){
        this.audio_status = audio_status;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}