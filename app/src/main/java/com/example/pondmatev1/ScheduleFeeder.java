package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleFeeder extends Fragment {

    EditText startOfAct, chooseTime, amtOfFeeders;
    Button saveSchedule;
    Calendar calendar;
    LinearLayout scheduleListContainer;
    List<Schedule> scheduleList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedulefeeder, container, false);

        // UI elements
        startOfAct = view.findViewById(R.id.start_of_act);
        chooseTime = view.findViewById(R.id.choose_time);
        amtOfFeeders = view.findViewById(R.id.amtoffeeders);
        saveSchedule = view.findViewById(R.id.save_schedule);
        scheduleListContainer = view.findViewById(R.id.schedule_list_container);

        calendar = Calendar.getInstance();

        // Date Picker
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

        // Time Picker
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

        // Save Button
        saveSchedule.setOnClickListener(v -> {
            String date = startOfAct.getText().toString().trim();
            String time = chooseTime.getText().toString().trim();
            String amount = amtOfFeeders.getText().toString().trim();

            if (!date.isEmpty() && !time.isEmpty() && !amount.isEmpty()) {
                Schedule newSchedule = new Schedule(date, time, amount);
                scheduleList.add(newSchedule);
                displaySchedules();
                saveToPreferences();

                startOfAct.setText("");
                chooseTime.setText("");
                amtOfFeeders.setText("");
            }
        });

        loadFromPreferences();
        displaySchedules();

        return view;
    }

    private void displaySchedules() {
        scheduleListContainer.removeAllViews();

        // Create TableLayout to hold the table rows
        TableLayout tableLayout = new TableLayout(getContext());
        tableLayout.setStretchAllColumns(true); // Make all columns stretch to fill the screen

        // Create the header row (optional)
        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(Color.parseColor("#f1f1f1")); // Set background color for header row

        TextView dateHeader = new TextView(getContext());
        dateHeader.setText("Date");
        dateHeader.setTextSize(18);
        dateHeader.setPadding(10, 10, 10, 10);
        dateHeader.setGravity(Gravity.CENTER);
        dateHeader.setTextColor(getResources().getColor(android.R.color.black));

        TextView timeHeader = new TextView(getContext());
        timeHeader.setText("Time");
        timeHeader.setTextSize(18);
        timeHeader.setPadding(10, 10, 10, 10);
        timeHeader.setGravity(Gravity.CENTER);
        timeHeader.setTextColor(getResources().getColor(android.R.color.black));

        TextView amountHeader = new TextView(getContext());
        amountHeader.setText("Amount");
        amountHeader.setTextSize(18);
        amountHeader.setPadding(10, 10, 10, 10);
        amountHeader.setGravity(Gravity.CENTER);
        amountHeader.setTextColor(getResources().getColor(android.R.color.black));

        TextView deleteHeader = new TextView(getContext());
        deleteHeader.setText("Pending");
        deleteHeader.setTextSize(18);
        deleteHeader.setPadding(10, 10, 10, 10);
        deleteHeader.setGravity(Gravity.CENTER);
        deleteHeader.setTextColor(getResources().getColor(android.R.color.black));

        // Add the headers to the header row
        headerRow.addView(dateHeader);
        headerRow.addView(timeHeader);
        headerRow.addView(amountHeader);
        headerRow.addView(deleteHeader);
        tableLayout.addView(headerRow);  // Add the header row to the table

        // Loop through schedule list and create a row for each schedule
        for (int i = 0; i < scheduleList.size(); i++) {
            final int index = i;

            Schedule s = scheduleList.get(index);

            // Create a row for each schedule item
            TableRow scheduleRow = new TableRow(getContext());
            scheduleRow.setBackgroundResource(R.drawable.transparentbg);
            scheduleRow.setPadding(5, 5, 5, 5);

            // Date column
            TextView dateColumn = new TextView(getContext());
            dateColumn.setText(s.date);
            dateColumn.setTextSize(16);
            dateColumn.setPadding(10, 10, 10, 10);
            dateColumn.setGravity(Gravity.CENTER);

            // Time column
            TextView timeColumn = new TextView(getContext());
            timeColumn.setText(s.time);
            timeColumn.setTextSize(16);
            timeColumn.setPadding(10, 10, 10, 10);
            timeColumn.setGravity(Gravity.CENTER);

            // Amount column
            TextView amountColumn = new TextView(getContext());
            amountColumn.setText(s.amount + " kg");
            amountColumn.setTextSize(16);
            amountColumn.setPadding(10, 10, 10, 10);
            amountColumn.setGravity(Gravity.CENTER);

            // Delete column
            Button deleteButton = new Button(getContext());
            deleteButton.setText("❌");
            deleteButton.setTextSize(16);
            deleteButton.setPadding(0, 0, 10, 0);
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setTextColor(Color.RED);
            deleteButton.setOnClickListener(v -> {
                scheduleList.remove(index);
                saveToPreferences();
                displaySchedules();
            });

            // Add columns to the schedule row
            scheduleRow.addView(dateColumn);
            scheduleRow.addView(timeColumn);
            scheduleRow.addView(amountColumn);
            scheduleRow.addView(deleteButton);

            // Add the row to the table
            tableLayout.addView(scheduleRow);
        }

        // Add the TableLayout to the container
        scheduleListContainer.addView(tableLayout);
    }


    private void saveToPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(scheduleList);
        editor.putString("schedules", json);
        editor.apply();
    }

    private void loadFromPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("SchedulePrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("schedules", null);

        if (json != null) {
            Type type = new TypeToken<List<Schedule>>() {}.getType();
            scheduleList = new Gson().fromJson(json, type);
        }
    }

    // Simple POJO for storing schedule data
    public static class Schedule {
        public String date;
        public String time;
        public String amount;

        public Schedule(String date, String time, String amount) {
            this.date = date;
            this.time = time;
            this.amount = amount;
        }
    }
}
