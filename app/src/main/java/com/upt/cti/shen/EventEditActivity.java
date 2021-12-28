package com.upt.cti.shen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.shen.utils.CalendarUtils;
import com.upt.cti.shen.utils.Event;
import com.upt.cti.shen.utils.FirebaseObject;

import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity {
    private EditText eventNameET, eventTimeStart, eventTimeEnd;
    private TextView eventDateTV;
    @SuppressLint("VisibleForTests")
    private FirebaseFirestore db;
    private DatabaseReference root;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch driving, anniversary, gallery;
    private Button bt_save;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
    }

    private void initWidgets() {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeStart = findViewById(R.id.eventTimeStartField);
        eventTimeEnd = findViewById(R.id.eventTimeEndField);
        driving = findViewById(R.id.driving);
        anniversary = findViewById(R.id.anniversary);
        gallery = findViewById(R.id.gallery);
        bt_save = findViewById(R.id.bt_save);

        db = FirebaseFirestore.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEventAction(View view) {
        switch (view.getId()) {
            case R.id.bt_save:
                String eventName = eventNameET.getText().toString();
                String eventStart = eventTimeStart.getText().toString();
                String eventEnd = eventTimeEnd.getText().toString();
                Boolean driving = this.driving.isChecked();
                Boolean anniversary = this.anniversary.isChecked();
                Boolean gallery = this.gallery.isChecked();
                Event newEvent = new Event(eventName, CalendarUtils.selectedDate, LocalTime.parse(eventStart), LocalTime.parse(eventEnd), driving, anniversary, gallery);
                Event.eventsList.add(newEvent);
                saveEventToFirebase(newEvent);
                if (anniversary) {
                    openAnniversary(view);
                } else if (driving) {
                    openDrivingIndications(view);
                } else if (gallery) {
                    openGallery(view);
                }
                finish();
                break;
            default:
                System.out.println(view.getId());
        }
    }

    private void saveEventToFirebase(Event newEvent) {
        FirebaseObject firebaseEvent = new FirebaseObject(newEvent.getName(),
                newEvent.getDate().toString(), newEvent.getStart(), newEvent.getEnd(),
                newEvent.getDriving(), newEvent.getAnniversary(), newEvent.getGallery(), newEvent.isGift(), newEvent.isLike_address(), newEvent.getLocation_txt(), newEvent.getmArrayUri());
        db.collection("activities").document(newEvent.getName())
                .set(firebaseEvent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });

    }

    public void openAnniversary(View view){
        Intent intent = new Intent(this, AnniversaryEventActivity.class);
        startActivity(intent);
    }

    public void openGallery(View view){
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    public void openDrivingIndications(View view){
        Intent intent = new Intent(this, MapLocationActivity.class);
        startActivity(intent);
    }
}
