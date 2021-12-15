package com.upt.cti.shen;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.shen.utils.CalendarUtils;
import com.upt.cti.shen.utils.Event;

import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET, eventTimeStart, eventTimeEnd;
    private TextView eventDateTV;
    @SuppressLint("VisibleForTests")
    private FirebaseFirestore db;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch driving, anniversary, gallery;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
    }

    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeStart = findViewById(R.id.eventTimeStartField);
        eventTimeEnd = findViewById(R.id.eventTimeEndField);
        driving = findViewById(R.id.driving);
        anniversary = findViewById(R.id.anniversary);
        gallery = findViewById(R.id.gallery);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
        String eventStart = eventTimeStart.getText().toString();
        String eventEnd = eventTimeEnd.getText().toString();
        Boolean driving = this.driving.isChecked();
        Boolean anniversary = this.anniversary.isChecked();
        Boolean gallery = this.gallery.isChecked();
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, LocalTime.parse(eventStart), LocalTime.parse(eventEnd), driving, anniversary, gallery);
        Event.eventsList.add(newEvent);
        //saveEventToFirebase(newEvent);
        finish();
    }

    private void saveEventToFirebase(Event newEvent) {
        db.collection("activities").document("doc")
                .set(newEvent)
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
}
