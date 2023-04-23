package com.example.a3717project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnterTripTypeActivity extends AppCompatActivity {
    private boolean hasFlight;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Button findFlight;
    private Button viewTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_trip_type);
        Button delete = findViewById(R.id.btnDelete);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        findFlight = findViewById(R.id.btnFlight);
        viewTrip = findViewById(R.id.btnViewRegisteredTrip);

        findFlight.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().
                    beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new FlightSearchFragment());
            fragmentTransaction.commit();
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check to see if trip exist and deletes
                findAndDeleteTrip();
            }
        });

        viewTrip.setOnClickListener(v -> {
            Log.d("view trip btn", "about to take u to edit/view trip page");
            Intent viewModifyIntent = new Intent(this,
                    ViewAndModifyTripActivity.class);
            startActivity(viewModifyIntent);
        });
        checkExists();

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

    private void toastHelper(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void deleteTrip() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference().child("Trips");
        User currentUser = LoginActivity.getLoginActivity().getCurrentUser();
        databaseReference.child(currentUser.getUsername()).removeValue();
        toastHelper("deleted Trip");
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }

    private void findAndDeleteTrip() {
        User currentUser = LoginActivity.getLoginActivity().getCurrentUser();
        hasFlight = false;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                getReference().child("Trips");
        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            if (dsp.getKey().toString().trim().equals(currentUser.
                                    getUsername().trim())) {
                                hasFlight = true;
                                deleteTrip();
                            }
                        }
                        if (!hasFlight) {
                            toastHelper("No Trip Was Found");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void checkExists() {
        Log.d("checking", "the method is called?");
        User currentUser = LoginActivity.getLoginActivity().getCurrentUser();
        databaseReference.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean exists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip tripToCheck = snapshot.getValue(Trip.class);
                    if (currentUser.getUsername().equals(tripToCheck.getUser().getUsername())) {
                        exists = true;
                        Log.d("checking", "found!");
                    }
                }
                if (exists) {
                    findFlight.setVisibility(View.GONE);
                    viewTrip.setVisibility(View.VISIBLE);
                    Toast.makeText(EnterTripTypeActivity.this, "Trip " +
                            "created by this user already exists. has to " +
                            "end it or delete it to make a new one", Toast.LENGTH_LONG).show();
                } else {
                    findFlight.setVisibility(View.VISIBLE);
                    viewTrip.setVisibility(View.GONE);
                }
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("somehow cancelled?");
            }
        });


    }
}