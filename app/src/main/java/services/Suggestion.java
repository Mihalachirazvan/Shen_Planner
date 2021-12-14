package services;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.RankBy;
import com.upt.cti.shen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import utils.JsonParser;

public class Suggestion extends AppCompatActivity {
    private Spinner sp_type;
    private SupportMapFragment mapView;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location currentLocation;
    double currentLat = 0,currentLong = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion_page);

        //Assign variable
        sp_type = (Spinner) findViewById(R.id.sp_type);
        mapView = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);

        //Initialize array of places
        final String[] placeTypeList = {"restaurant", "movie_theater", "atm", "hospital"};
        final String[] placeNameList = {"Restaurant", "Movie Theater", "ATM", "Hospital"};
        //set adapter on spinner
        sp_type.setAdapter(new ArrayAdapter<>(
                Suggestion.this, R.layout.support_simple_spinner_dropdown_item, placeNameList));

        //Initialize fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(
                Suggestion.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(Suggestion.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                        "?location=" + currentLat + "," + currentLong +
                        "&radius=5000" +
                        "&type=" + placeTypeList[i] +
                        "&sensor=true" +
                        "&key=" + getResources().getString(R.string.nearby_api);

                System.out.println(url);
                //execute place task method to dowload json data
                new PlaceTask().execute(url);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("not access granted");
            return;
        }
        //Initialize task Location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    //Sync Map
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat,currentLong),10));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            //initialize data
            String data = null;
            try {
                 data = downloadUrl(strings[0]);
                 System.out.println(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //execute parser task
            new ParserTask().execute(s);
        }

        private String downloadUrl(String string) throws IOException {
            URL url = new URL(string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while((line = reader.readLine())!=null) {
                builder.append(line);

            }
            String data = builder.toString();
            reader.close();
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //Create Json parser class;
            JsonParser jsonParser = new JsonParser();
            //Initialize hash map
            List<HashMap<String,String>> mapList = null;
            //Initialize json object
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                //Parse json object
                mapList = jsonParser.parseResult(object);
                System.out.println(mapList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            //clear map
            map.clear();
            for(int i=0;i<hashMaps.size();i++) {
                HashMap<String,String> hashMapList = hashMaps.get(i);
                //Get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                //Get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));
                //Get Name
                String name = hashMapList.get("name");
                //Concat lat and lng
                LatLng latLng = new LatLng(lat,lng);
                //Initialize marker options
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                //Set title
                options.title(name);
                //add marker
                map.addMarker(options);
            }
        }
    }
}