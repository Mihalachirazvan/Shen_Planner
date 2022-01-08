package com.upt.cti.shen.utils;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import static com.upt.cti.shen.utils.Event.eventsList;

import android.net.Uri;
import android.os.Build;
import android.util.Log;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseObject implements Serializable {
    public String name;
    public String date;
    public String start;
    public String end;
    public boolean driving;
    public boolean anniversary;
    public boolean gallery;
    public boolean gift = false;
    public boolean like_address = false;
    public String location_txt="";
    public ArrayList<String> mArrayUri = new ArrayList<>();

    public FirebaseObject() {

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public FirebaseObject(String name, String date, String start, String end, boolean driving, boolean anniversary, boolean gallery, boolean gift, boolean like_address, String location_txt, ArrayList<Uri> mArrayUri) {
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
        mArrayUri.stream().forEach(x -> this.mArrayUri.add(x.toString()));
    }
    public static void getListItems() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("activities")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<FirebaseObject> types = documentSnapshots.toObjects(FirebaseObject.class);
                            for(FirebaseObject fb:types) {
                                convert_to_events(fb);
                            }
                            // Add all to your list
                            System.out.println(types);
                            //Log.d(TAG, "onSuccess: " + eventsList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error getting data!!!", e);
                    }
                });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void convert_to_events(FirebaseObject fb) {
        Event event = new Event(fb.name, LocalDate.parse(fb.date),LocalTime.parse(fb.start),LocalTime.parse(fb.end),
                fb.driving, fb.anniversary, fb.gallery);
              event.setLocation_txt(fb.location_txt);
              event.setGift(fb.gift);
              event.setLike_address(fb.like_address);
              ArrayList<Uri> aux = new ArrayList<>();
              for(int i = 0; i<fb.mArrayUri.size();i++) {
                  Uri myUri = Uri.parse(fb.mArrayUri.get(i));
                  aux.add(myUri);
              }
              event.setmArrayUri(aux);
              System.out.println(event.toString());
              eventsList.add(event);
    }
}
