package com.upt.cti.shen.utils;

import android.net.Uri;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

public class FirebaseObject implements Serializable {
    public String name;
    public String date;
    public LocalTime start;
    public LocalTime end;
    public boolean driving;
    public boolean anniversary;
    public boolean gallery;
    public boolean gift = false;
    public boolean like_address = false;
    public String location_txt="";
    public transient ArrayList<Uri> mArrayUri = new ArrayList<>();

    public FirebaseObject(String name, String date, LocalTime start, LocalTime end, boolean driving, boolean anniversary, boolean gallery, boolean gift, boolean like_address, String location_txt, ArrayList<Uri> mArrayUri) {
        this.name = name;
        this.date = date;
        this.start = start;
        this.end = end;
        this.driving = driving;
        this.anniversary = anniversary;
        this.gallery = gallery;
        this.gift = gift;
        this.like_address = like_address;
        this.location_txt = location_txt;
        this.mArrayUri = mArrayUri;
    }
}
