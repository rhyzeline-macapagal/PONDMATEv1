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

        for (int i = 0; i < scheduleList.size(); i++) {
            Schedule s = scheduleList.get(i);

            CardView cardView = new CardView(getContext());
            cardView.setCardElevation(8f);  // Elevation for shadow effect
            cardView.setPadding(16, 12, 16, 12);
            cardView.setBackgroundResource(R.drawable.rounded_container);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 5, 0, 5); // Adds space between the cards (top and bottom)
            cardView.setLayoutParams(cardParams);

            // Create the layout for content inside the card
            LinearLayout itemLayout = new LinearLayout(getContext());
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            itemLayout.setGravity(Gravity.CENTER_VERTICAL);
            itemLayout.setWeightSum(1);

            // TextView to display schedule details
            TextView scheduleView = new TextView(getContext());
            scheduleView.setText("📅 " + s.date + "   🕒 " + s.time + "   🐟 " + s.amount + " kg");
            scheduleView.setTextSize(16);
            scheduleView.setPadding(10,5,0,5);
            scheduleView.setTextColor(Color.parseColor("#10282E"));
            scheduleView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.85f));

            // Delete button
            Button deleteButton = new Button(getContext());
            deleteButton.setText("❌");
            deleteButton.setTextSize(14);
            deleteButton.setPadding(0,0,10,0);
            deleteButton.setBackgroundColor(Color.TRANSPARENT);
            deleteButton.setTextColor(Color.RED);
            deleteButton.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.15f));

            int finalI = i;
            deleteButton.setOnClickListener(v -> {
                scheduleList.remove(finalI);
                saveToPreferences();
                displaySchedules();
            });

            // Add views to the item layout
            itemLayout.addView(scheduleView);
            itemLayout.addView(deleteButton);

            // Add item layout to card view
            cardView.addView(itemLayout);

            // Add the card to the container
            scheduleListContainer.addView(cardView);
        }
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
