package com.upt.cti.shen;


import static com.upt.cti.shen.utils.CalendarUtils.selectedDate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.upt.cti.shen.utils.CalendarUtils;
import com.upt.cti.shen.utils.Event;
import com.upt.cti.shen.utils.HourAdapter;
import com.upt.cti.shen.utils.HourEvent;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity
{

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        CalendarUtils.selectedDate = LocalDate.now();
        initWidgets();

        hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HourEvent x = (HourEvent) hourListView.getItemAtPosition(i);
                if (x.getEvents().get(0).getAnniversary()) {
                    openAnniversary(view, x.getEvents().get(0));
                } else if (x.getEvents().get(0).getDriving()) {
                    openDrivingIndications(view, x.getEvents().get(0));
                } else if (x.getEvents().get(0).getGallery()) {
                    openPlacesEvent(view, x.getEvents().get(0));
                }
            }
        });
    }

    private void initWidgets()
    {
        monthDayText = findViewById(R.id.monthDayText);
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        hourListView = findViewById(R.id.hourListView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume()
    {
        super.onResume();
        setDayView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDayView()
    {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHourAdapter()
    {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<HourEvent> hourEventList()
    {
        ArrayList<HourEvent> list = new ArrayList<>();

        for(int hour = 0; hour < 24; hour++)
        {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }

        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousDayAction(View view)
    {
        selectedDate = selectedDate.minusDays(1);
        setDayView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextDayAction(View view)
    {
        selectedDate = selectedDate.plusDays(1);
        setDayView();
    }

    public void newEventAction(View view)
    {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    public void openWeeklyCalendar(View view){
        Intent intent = new Intent(this, WeekViewActivity.class);
        startActivity(intent);
    }

    public void openSuggestion(View view) {
        Intent intent = new Intent(this, SuggestionActivity.class);
        startActivity(intent);
    }

    public void openAnniversary(View view, Event event) {
        System.out.println(event.toString());
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
}