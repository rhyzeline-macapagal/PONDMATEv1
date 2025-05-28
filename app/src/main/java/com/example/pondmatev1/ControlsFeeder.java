package com.example.pondmatev1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ControlsFeeder extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.controlsfeeder, container, false);

        // Set current date and time in PH timezone
        //Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
        //SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy - hh:mm a", Locale.ENGLISH);
        //dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        //String currentDateTime = dateTimeFormat.format(calendar.getTime());
        //TextView textCurrentDate = view.findViewById(R.id.currentdate);
        //textCurrentDate.setText(currentDateTime);

        // Access fields if needed (already non-editable in XML)


        return view;
    }
}
