package services;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

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

public class MapLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SearchView input_search;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        input_search = (SearchView) findViewById(R.id.input_search);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Implement an search View listener for getting new locations via Google Map
        input_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String location = input_search.getQuery().toString();
                List<Address> addressList = new ArrayList<>();

                if(location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapLocation.this);
                    try {
                        addressList = geocoder.getFromLocationName("Romania",2);
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
