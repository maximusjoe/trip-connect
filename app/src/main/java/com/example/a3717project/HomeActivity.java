package com.example.a3717project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String username = LoginActivity.getLoginActivity().getCurrentUser().getUsername();
        String parsedUserName;
        if (!username.isEmpty()) {

            parsedUserName = "Welcome, " + username;

        } else {
            parsedUserName = "Welcome";
        }


        TextView welcome = findViewById(R.id.welcome);
        welcome.setText(parsedUserName);


        Button enterTrip = findViewById(R.id.btnTrip);
        enterTrip.setOnClickListener(view -> {
            Intent enterTripIntent = new Intent(this, EnterTripTypeActivity.class);
            startActivity(enterTripIntent);
        });

        Button viewTrip = findViewById(R.id.btnOthers);
        viewTrip.setOnClickListener(view -> {
            Intent viewTripIntent = new Intent(this, ViewerActivity.class);
            startActivity(viewTripIntent);
        });

        BottomNavigationView bottomNavBar = findViewById(R.id.navbar);
        bottomNavBar.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.home:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.flights:
                    startActivity(new Intent(getApplicationContext(),
                            EnterTripTypeActivity.class));
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

}