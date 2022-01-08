package com.upt.cti.shen.utils;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    public static ArrayList<Event> eventsList = new ArrayList<>();

    public Event(String name, boolean gift) {
        this.name = name;
        this.gift = gift;
    }

    public Event(String name, boolean gift ,boolean like_address) {
        this.name = name;
        this.gift = gift;
        this.like_address = like_address;
    }

    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventsList) {
            if (event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventsList) {
            int eventHour = event.start.getHour();
            int cellHour = time.getHour();
            if (event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }

        return events;
    }


    private String name;
    private LocalDate date;
    private LocalTime start;
    private LocalTime end;
    private String address;
    private boolean driving;
    private boolean anniversary;
    private boolean gallery;
    private boolean gift = false;
    private boolean like_address = false;
    private String location_txt = "";
    private transient ArrayList<Uri> mArrayUri = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    public Boolean getDriving() {
        return driving;
    }

    public void setDriving(Boolean driving) {
        this.driving = driving;
    }

    public Boolean getAnniversary() {
        return anniversary;
    }

    public void setAnniversary(Boolean anniversary) {
        this.anniversary = anniversary;
    }

    public Boolean getGallery() {
        return gallery;
    }

    public void setGallery(Boolean gallery) {
        this.gallery = gallery;
    }


    public Event() {

    }
    public Event(String name, LocalDate date, LocalTime start, LocalTime end, Boolean driving, Boolean anniversary, Boolean gallery) {
        this.name = name;
        this.date = date;
        this.start = start;
        this.end = end;
        this.driving = driving;
        this.anniversary = anniversary;
        this.gallery = gallery;
    }


    public String getLocation_txt() {
        return location_txt;
    }

    public void setLocation_txt(String location_txt) {
        this.location_txt = location_txt;
    }

    public boolean isLike_address() {
        return like_address;
    }

    public void setLike_address(boolean like_address) {
        this.like_address = like_address;
    }

    public boolean isGift() {
        return gift;
    }

    public void setGift(boolean gift) {
        this.gift = gift;
    }

    public ArrayList<Uri> getmArrayUri() {
        return mArrayUri;
    }

    public void setmArrayUri(ArrayList<Uri> mArrayUri) {
        this.mArrayUri = mArrayUri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", start=" + start +
                ", end=" + end +
                ", address='" + address + '\'' +
                ", driving=" + driving +
                ", anniversary=" + anniversary +
                ", gallery=" + gallery +
                ", gift=" + gift +
                ", like_address=" + like_address +
                ", location_txt='" + location_txt + '\'' +
                ", mArrayUri=" + mArrayUri +
                '}';
    }
}