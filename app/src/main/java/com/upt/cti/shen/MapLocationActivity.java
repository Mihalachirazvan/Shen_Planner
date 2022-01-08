package com.upt.cti.shen;

import static com.upt.cti.shen.utils.Event.eventsList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;
import com.upt.cti.shen.utils.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import notifications.RouteLocation;


public class MapLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView input_search;
    private SupportMapFragment mapFragment;
    private Button btNotification;
    private Event event = new Event("", false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        input_search = (SearchView) findViewById(R.id.input_search);
        btNotification = (Button) findViewById(R.id.btNotification);

        RouteLocation.createNotificationRoute(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
        btNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEventAddress();
                Intent intent = new Intent(MapLocationActivity.this, RouteLocation.class);
                intent.putExtra("Location", eventsList.get(positionEvent()).getLocation_txt());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MapLocationActivity.this,0,intent,0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();

                long time5Seconds = 5 * 1000;

                alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClick + time5Seconds,pendingIntent);
            }
        });
    }
    private void init() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.event = new Event(extras.getString("eventName"), false);
        }
        if(eventsList.get(positionEvent()).getLocation_txt().length()!=0) {
            System.out.println(eventsList.get(positionEvent()).getLocation_txt());
            input_search.setQuery(eventsList.get(positionEvent()).getLocation_txt(), true);
        }
    }

    private int positionEvent() {
        for(int j=0 ;j < eventsList.size();j++) {
            if(eventsList.get(j).getName().equals(event.getName())) {
                return j;
            }
        }
        return -1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        input_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = input_search.getQuery().toString();
                List<Address> addressList = new ArrayList<>();

                if(location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapLocationActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,2);
                        eventsList.get(positionEvent()).setLocation_txt(location);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addressList.isEmpty()) {
                        Toast.makeText(getApplicationContext(),"Input a proper location ",Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

    }

    public void saveEventAddress() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("activities").document(eventsList.get(positionEvent()).getName()).
                update("location_txt", eventsList.get(positionEvent()).getLocation_txt());

    }
}