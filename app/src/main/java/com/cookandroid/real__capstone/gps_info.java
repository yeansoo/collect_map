package com.cookandroid.real__capstone;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;

@IgnoreExtraProperties
public class gps_info {
    public Double longitude;
    public Double langitude;
    public int slope;
    public String realtime;

    public gps_info(){}

    public gps_info(String realtime, Double longitude, Double langitude, int slope){
        this.realtime=realtime;
        this.longitude=longitude;
        this.langitude=langitude;
        this.slope=slope;
    }
}
