package com.example.iotv1app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FilterFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        Button applyFilterBtn = (Button) view.findViewById(R.id.applyFilterBtn);
        applyFilterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the applyFilterBtn");
            }
        });

        Button makeFilterBtn = (Button) view.findViewById(R.id.makeFilterBtn);
        makeFilterBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the makeFilterBtn");
            }
        });

        return view;
    }
}