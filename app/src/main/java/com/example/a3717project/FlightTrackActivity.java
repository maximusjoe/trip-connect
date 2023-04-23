package com.example.a3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class FlightTrackActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    // TODO: assign actual coordinates of the current user location
    private double x_coor = 49.25010954461797;
    private double y_coor = -123.00275621174804;
    private String hex = "";
    private String dep = "";
    private String des = "";

    private boolean exists = false;

    private GoogleMap map;

    private String codeToSearch;
    private TextView pw, code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_track);
        Button back = findViewById(R.id.flightTrackBackButton);
        Button save = findViewById(R.id.saveAsTripButton);
        Bundle bundle = getIntent().getExtras();
        pw = findViewById(R.id.pw);
        code = findViewById(R.id.code);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

        //Code to search API for.
        codeToSearch = bundle.getString("codeToSearch");

        String apiKey = getString(R.string.API_KEY);

        TextView tv = findViewById(R.id.flightTrackTextView);

        back.setOnClickListener(view -> {
            Intent backIntent = new Intent(this, EnterTripTypeActivity.class);
            startActivity(backIntent);
        });

        save.setOnClickListener(view -> {
            saveAsTrip();
        });

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(apiKey);

        BottomNavigationView bottomNavBar = findViewById(R.id.navbar);
        bottomNavBar.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case (R.id.home):
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case (R.id.flights):
                    startActivity(new Intent(getApplicationContext(), EnterTripTypeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case (R.id.settings):
                    startActivity(new Intent(getApplicationContext(), ViewerActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        LatLng flightLocation = new LatLng(x_coor, y_coor);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(flightLocation, 10));
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            RequestQueue queue = Volley.newRequestQueue(FlightTrackActivity.this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, strings[0], null, response -> {

                System.out.println(response.toString());

                try {
                    JSONArray resultArray = response.getJSONArray("response");

                    //FOR THE FIRST 5 ENTRIES IN THE RESULT ARRAY
                    int index = 0;
                    JSONObject obj = resultArray.getJSONObject(index);

                    while (index < resultArray.length()) {
                        try {
                            if (obj.getString("hex").equals(codeToSearch)) {
                                break;
                            }
                            obj = resultArray.getJSONObject(index);
                            System.out.println(index + ": hex number: " + obj.getString("hex"));
                        } catch (Exception e) {
                            e.toString();
                        }
                        ++index;
                    }

                    //DO SOMETHING WITH RESPONSES HERE
                    //MAKE THE CALL ONLY ONCE, SOMEWHERE ELSE, AND SEARCH THE ARRAY HERE
                    //System.out.println(obj.toString(2));
                    TextView tv = findViewById(R.id.flightTrackTextView);
                    String newText = "Flight: ";
                    try {
                        newText = "Flight: " + obj.getString("hex") + ", " + obj.getString("dep_iata");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        des = obj.getString("arr_iata");
                        newText = "Flight: " + obj.getString("hex") + ", " + obj.getString("dep_iata") + " -> " + des;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tv.setText(newText);

                    x_coor = obj.getDouble("lat");
                    y_coor = obj.getDouble("lng");
                    hex = obj.getString("hex");
                    dep = obj.getString("dep_iata");

                    LatLng flightLocation = new LatLng(x_coor, y_coor);
                    map.addMarker(new MarkerOptions().position(flightLocation).title("Flight"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(flightLocation, 5));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(FlightTrackActivity.this, error.toString(), Toast.LENGTH_SHORT).show());
            queue.add(request);
            return null;
        }
    }

    public String generateRandomAlphaNumeric(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    private void saveAsTrip() {
        User currentUser = LoginActivity.getLoginActivity().getCurrentUser();


        databaseReference.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip tripToCheck = snapshot.getValue(Trip.class);
                    if (currentUser.getUsername().equals(tripToCheck.getUser().getUsername())) {
                        exists = true;
                        Log.d("checking", "found!");
                    }
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("somehow cancelled?");
            }
        });

        if (exists) {
            Toast.makeText(FlightTrackActivity.this, "Trip " +
                    "created by this user already exists. has to " +
                    "end it or delete it to make a new one", Toast.LENGTH_LONG).show();
        } else {
            // otherwise make a new trip with the username key
            String id = databaseReference.child("Trips").child(currentUser.getUsername()).getKey();

            Trip newTrip = new Trip();
            //TODO: will be fetched from the editText or the prvious activity?
            String trip_code = generateRandomAlphaNumeric(1);
            String password = generateRandomAlphaNumeric(1);

            // set all the necessary info for this trip instnace
            newTrip.setTrip_code(trip_code);
            newTrip.setX_coor(x_coor);
            newTrip.setY_coor(y_coor);
            newTrip.setUser(currentUser);
            newTrip.setPassword(password);
            newTrip.setDeparture(dep);
            newTrip.setHex(hex);
            newTrip.setDestination(des);
            pw.setText("Trip Password: " + password);
            code.setText("Trip Code: " + trip_code);

            databaseReference.child("Trips").child(id).setValue(newTrip);

            Toast.makeText(this, "Trip Successfully Saved", Toast.LENGTH_SHORT).show();
        }
    }
}

