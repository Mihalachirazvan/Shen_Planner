package com.upt.cti.shen;

import static com.upt.cti.shen.utils.CalendarUtils.daysInWeekArray;
import static com.upt.cti.shen.utils.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upt.cti.shen.utils.CalendarAdapter;
import com.upt.cti.shen.utils.CalendarUtils;
import com.upt.cti.shen.utils.Event;
import com.upt.cti.shen.utils.EventAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        setWeekView();
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event x = (Event) eventListView.getItemAtPosition(i);
                if (x.getAnniversary()) {
                    openAnniversary(view, x);
                } else if (x.getDriving()) {
                    openDrivingIndications(view, x);
                } else if (x.getGallery()) {
                    openPlacesEvent(view, x);
                }
            }
        });
    }

    private void initWidgets()
    {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        eventListView = findViewById(R.id.eventListView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdpater();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view)
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setEventAdpater();
    }

    private void setEventAdpater()
    {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalendarUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    public void newEventAction(View view)
    {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    public void dailyAction(View view)
    {
        startActivity(new Intent(this, DailyCalendarActivity.class));
    }

    public void openMonthlyCalendar(View view){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
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
    public void openSuggestion(View view) {
        Intent intent = new Intent(this, SuggestionActivity.class);
        startActivity(intent);
    }
}