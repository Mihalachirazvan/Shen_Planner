package com.upt.cti.shen;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.upt.cti.shen.utils.CalendarUtils;
import com.upt.cti.shen.utils.Event;
import com.upt.cti.shen.utils.FirebaseObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import notifications.NotificationService;

public class EventEditActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "my_channel";
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
        createNotificationChannel();
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
                    scheduleNotification(this, 0, newEvent, false);
                    openAnniversary(view, newEvent);
                } else if (driving) {
                    scheduleNotification(this, 0, newEvent, true);
                    openDrivingIndications(view, newEvent);
                } else if (gallery) {
                    scheduleNotification(this, 0, newEvent, false);
                    openPlacesEvent(view,newEvent);
                } else {
                    scheduleNotification(this, 0, newEvent, true);
                }
                finish();
                break;
            default:
                System.out.println(view.getId());
        }
    }

    private void saveEventToFirebase(Event newEvent) {
        FirebaseObject firebaseEvent = new FirebaseObject(newEvent.getName(),
                newEvent.getDate().toString(), newEvent.getStart().toString(), newEvent.getEnd().toString(),
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

    public void openAnniversary(View view, Event event) {
        Intent intent = new Intent(this, AnniversaryEventActivity.class);
        intent.putExtra("eventName", event.getName());
        intent.putExtra("eventWithGift", event.isGift());
        startActivity(intent);
    }

    public void openDrivingIndications(View view , Event event) {
        Intent intent = new Intent(this, MapLocationActivity.class);
        intent.putExtra("eventName", event.getName());
        startActivity(intent);
    }
    public void openPlacesEvent(View view , Event event) {
        Intent intent = new Intent(this, PlaceEventActivity.class);
        intent.putExtra("eventName", event.getName());
        startActivity(intent);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SHEN";
            String description = "SHEN";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void scheduleNotification(Context context, int notificationId, Event event, boolean modifyHour) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(event.getName())
                .setContentText("You need to attend to" + event.getName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, EventEditActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (modifyHour) {
            calendar.set(Calendar.HOUR_OF_DAY, event.getStart().getHour() + 1);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, event.getStart().getHour());
        }
        calendar.set(Calendar.MINUTE, event.getStart().getMinute());
        calendar.set(Calendar.SECOND, event.getStart().getSecond());


        Intent notificationIntent = new Intent(context, NotificationService.class);
        notificationIntent.putExtra(NotificationService.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationService.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
