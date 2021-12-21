package services;

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
import com.upt.cti.shen.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import notifications.RouteLocation;


public class MapLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView input_search;
    private SupportMapFragment mapFragment;
    private Button btNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        input_search = (SearchView) findViewById(R.id.input_search);
        btNotification = (Button) findViewById(R.id.btNotification);
        RouteLocation.createNotificationRoute(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapLocation.this, RouteLocation.class);
                intent.putExtra("Location", "Cluj");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MapLocation.this,0,intent,0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                long timeAtButtonClick = System.currentTimeMillis();

                long time5Seconds = 5 * 1000;

                alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClick + time5Seconds,pendingIntent);
            }
        });
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
                    Geocoder geocoder = new Geocoder(MapLocation.this);
                    try {
                        addressList = geocoder.getFromLocationName(location,2);
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
}