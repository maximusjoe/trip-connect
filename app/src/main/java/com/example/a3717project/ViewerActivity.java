package com.example.a3717project;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewerActivity extends AppCompatActivity implements OnMapReadyCallback {
    private EditText enterTripCode, enterTripPassword;
    private Button buttonSearchCode;
    String tripCode, tripPassword;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private TextView textView;


    private Trip foundTrip;
    private GoogleMap map;

    private SupportMapFragment mapFragment;

    private double x_coor;
    private double y_coor;

    private String codeToSearch;

    CardView container;


    private boolean exists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        enterTripCode = findViewById(R.id.enterTripCode);
        firebaseDatabase = FirebaseDatabase.getInstance();
        // get the reference to the JSON tree
        databaseReference = firebaseDatabase.getReference();
        buttonSearchCode = findViewById(R.id.buttonSearchCode);
        enterTripPassword = findViewById(R.id.enterTripPassword);
        textView = findViewById(R.id.textView);
        container = findViewById(R.id.cardInViewer);

        buttonSearchCode.setOnClickListener(view -> {
            checkExists();
            if (foundTrip != null) {
                Toast.makeText(ViewerActivity.this, foundTrip.getPassword() + '\n' + foundTrip.getTrip_code() + '\n' + foundTrip.getUser().getUsername() + '\n', Toast.LENGTH_LONG).show();

            }
        });



        if (foundTrip != null) {
            Log.d("after the code check", foundTrip.getPassword());
        }

        BottomNavigationView bottomNavBar = findViewById(R.id.navbar);
        bottomNavBar.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.flights:
                    startActivity(new Intent(getApplicationContext(), EnterTripTypeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.settings:
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

    public boolean checkExists() {
        tripCode = enterTripCode.getText().toString().trim();
        tripPassword = enterTripPassword.getText().toString().trim();
        Toast.makeText(ViewerActivity.this, tripCode, Toast.LENGTH_LONG).show();
        if(TextUtils.isEmpty(tripCode)) {
            Toast.makeText(ViewerActivity.this, "Please enter a trip code", Toast.LENGTH_LONG).show();
        }
        databaseReference.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip trip = snapshot.getValue(Trip.class);
                    if(tripCode.equals(trip.getTrip_code()) && tripPassword.equals(trip.getPassword())) {
                        foundTrip = trip;
                        exists = true;
                    }
                }
                if(!exists) {
                    Toast.makeText(ViewerActivity.this, "Trip not found. Code or/and password entered is wrong.", Toast.LENGTH_LONG).show();
                } else {
                    x_coor = foundTrip.getX_coor();
                    y_coor = foundTrip.getY_coor();
                    codeToSearch = foundTrip.getTrip_code();
                    container.setVisibility(View.VISIBLE);
                    textView.setText("Watching " + foundTrip.getUser().getUsername() + "\'s trip");
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapInViewer);
                    mapFragment.getMapAsync(ViewerActivity.this);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return exists;
    }

}