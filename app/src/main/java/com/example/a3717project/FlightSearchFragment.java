package com.example.a3717project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class FlightSearchFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_flight_search, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        EditText etSearchCode = (EditText) view.findViewById(R.id.flightSearchCodeEditText);



        Button flightSearchButton = (Button) view.findViewById(R.id.flightSearchButton);
        flightSearchButton.setOnClickListener( View -> {
            String codeToSearch = etSearchCode.getText().toString();
            Intent flightTrackIntent = new Intent(getActivity(), FlightTrackActivity.class);
            flightTrackIntent.putExtra("codeToSearch", codeToSearch);

            startActivity(flightTrackIntent);
        });
    }
}
