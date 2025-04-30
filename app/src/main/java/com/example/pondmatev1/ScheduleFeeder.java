package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFeeder extends Fragment {

    EditText startOfAct, chooseTime, amtOfFeeders;
    Button saveSchedule;

    Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedulefeeder, container, false);

        // Initialize views
        startOfAct = view.findViewById(R.id.start_of_act);
        chooseTime = view.findViewById(R.id.choose_time);
        amtOfFeeders = view.findViewById(R.id.amtoffeeders);
        saveSchedule = view.findViewById(R.id.save_schedule);

        calendar = Calendar.getInstance();

        // Date Picker logic
        startOfAct.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", new Locale("en", "PH"));
                        startOfAct.setText(dateFormat.format(calendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Time Picker logic
        chooseTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (TimePicker timePicker, int hourOfDay, int minute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                                (hourOfDay % 12 == 0) ? 12 : hourOfDay % 12,
                                minute,
                                (hourOfDay < 12) ? "AM" : "PM");
                        chooseTime.setText(formattedTime);
                    },
                    12, 0, false
            );
            timePickerDialog.show();
        });

        // Save Button logic - clear fields
        saveSchedule.setOnClickListener(v -> {
            startOfAct.setText("");
            chooseTime.setText("");
            amtOfFeeders.setText("");
        });

        return view;
    }
}
