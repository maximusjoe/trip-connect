package com.example.a3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewAndModifyTripActivity extends AppCompatActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private double x_coor = 49.25010954461797;
    private double y_coor = -123.00275621174804;
    private String hex;
    private String destination;
    private String departure;

    private boolean exists = false;

    private GoogleMap map;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_and_modify_trip);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.mapAPI);
        mapFragment.getMapAsync(this);

        username = LoginActivity.getLoginActivity().getCurrentUser().getUsername();

        TextView tv = findViewById(R.id.flightTrackTextView);

        Button back = findViewById(R.id.flightTrackBackButton);
        back.setOnClickListener(view -> {
            Intent backIntent = new Intent(this, EnterTripTypeActivity.class);
            startActivity(backIntent);
        });

        databaseReference.child("Trips").child(username).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                Log.d("firebase", String.valueOf(task.getResult().child("x_coor")));
                x_coor = task.getResult().child("x_coor").getValue(Double.class);
                y_coor = task.getResult().child("y_coor").getValue(Double.class);
                hex = task.getResult().child("hex").getValue(String.class);
                destination = task.getResult().child("destination").getValue(String.class);
                departure = task.getResult().child("departure").getValue(String.class);
                if (map != null){
                    LatLng flightLocation = new LatLng(x_coor, y_coor);
                    map.addMarker(new MarkerOptions().position(flightLocation).title("Flight"));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(flightLocation, 5));
                }
                String parsedText = "Flight: " + hex + ", " + departure + " -> " + destination;
                tv.setText(parsedText);
            }
        });

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
        map.addMarker(new MarkerOptions().position(flightLocation).title("Flight"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(flightLocation, 5));
    }

}