package com.example.pondmatev1;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleFeeder extends Fragment {

    private LinearLayout containert;

    private ImageButton addTbtn;

    private Button selectDate, selectTime, setManual, Createbtn, Savebtn, Resetbtn;

    private TextView dateFS, timeFS, feedqttycont;
    private TableLayout SummaryT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedulefeeder, container, false);

        containert = view.findViewById(R.id.timecontainer);
        addTbtn = view.findViewById(R.id.addtimebtn);
        dateFS = view.findViewById(R.id.dateoffeedingschedule);
        timeFS = view.findViewById(R.id.timeoffeeding);
        selectDate = view.findViewById(R.id.btnselectdate);
        selectTime = view.findViewById(R.id.btnselecttime);
        setManual = view.findViewById(R.id.btnsetmanually);
        feedqttycont = view.findViewById(R.id.feedquantity);
        Createbtn = view.findViewById(R.id.createbtn);
        Savebtn = view.findViewById(R.id.savebtn);
        Resetbtn = view.findViewById(R.id.resetbtn);
        SummaryT= view.findViewById(R.id.summaryTable);

        //select date and time
        selectDate.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, month, dayOfMonth) -> {
                        Calendar selected = Calendar.getInstance();
                        selected.set(year, month, dayOfMonth);

                        if (selected.before(now)) {
                            Toast.makeText(getContext(), "Cannot select a past date.", Toast.LENGTH_SHORT).show();
                        } else {
                            @SuppressLint("DefaultLocale")
                            String selectedDate = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year);
                            dateFS.setText(selectedDate);
                        }
                    },
                    now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis()); // prevent past dates
            datePickerDialog.show();
        });

        selectTime.setOnClickListener(v -> {
            String selectedDateStr = dateFS.getText().toString();
            if (selectedDateStr.isEmpty()) {
                Toast.makeText(getContext(), "Please select a date first.", Toast.LENGTH_SHORT).show();
                return;
            }

            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view12, selectedHour, selectedMinute) -> {
                        try {
                            SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
                            Calendar selectedTime = Calendar.getInstance();
                            selectedTime.setTime(parser.parse(selectedDateStr + " " + selectedHour + ":" + selectedMinute));

                            if (selectedTime.before(now)) {
                                Toast.makeText(getContext(), "Cannot select a past time.", Toast.LENGTH_SHORT).show();
                            } else {
                                // Format to 12-hour with AM/PM
                                Calendar displayCal = Calendar.getInstance();
                                displayCal.set(Calendar.HOUR_OF_DAY, selectedHour);
                                displayCal.set(Calendar.MINUTE, selectedMinute);
                                SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                                String formattedTime = displayFormat.format(displayCal.getTime());

                                timeFS.setText(formattedTime);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Invalid date or time.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    hour, minute, false // 'false' for 12-hour format in TimePickerDialog
            );

            timePickerDialog.show();
        });

        //dynamically added row
        addTbtn.setOnClickListener(v ->addTimeRow(inflater));

        //editbutton
        Createbtn.setOnClickListener(v -> {
            selectDate.setEnabled(true);
            selectTime.setEnabled(true);
            setManual.setEnabled(true);
            addTbtn.setEnabled(true);
            feedqttycont.setEnabled(true);

            for (int i = 0; i < containert.getChildCount(); i++) {
                View timeRow = containert.getChildAt(i);
                Button selectTimeBtn = timeRow.findViewById(R.id.btnselecttime);
                ImageButton removeTimeBtn = timeRow.findViewById(R.id.removetime);
                if (selectTimeBtn != null) selectTimeBtn.setEnabled(true);
                if (removeTimeBtn != null) removeTimeBtn.setEnabled(true);
            }

            Savebtn.setVisibility(View.VISIBLE);
            Resetbtn.setVisibility(View.VISIBLE);
            Createbtn.setVisibility(View.GONE);
        });

        //resetbutton
        Resetbtn.setOnClickListener(v -> {
            dateFS.setText("");
            timeFS.setText("");
            feedqttycont.setText("");

            // Remove dynamically added date and time rows (if any), keeping the first one if needed
            containert.removeViews(1, containert.getChildCount() - 1);

            // Reset first row text if necessary
            View firstTimeRow = containert.getChildAt(0);
            if (firstTimeRow != null) {
                TextView timeText = firstTimeRow.findViewById(R.id.timeoffeeding);
                if (timeText != null) timeText.setText("");
            }

        });

        //savebutton
        Savebtn.setOnClickListener(v -> {
            String staticDate = dateFS.getText().toString();
            String staticTime = timeFS.getText().toString();
            String feedQuantity = feedqttycont.getText().toString();

            int dynamicTimeCount = containert.getChildCount();

            if (staticDate.isEmpty()) {
                Toast.makeText(getContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Add static time entry
            if (!staticTime.isEmpty()) {
                String status = getStatus(staticDate, staticTime);
                addTableRow(staticDate, staticTime, feedQuantity, status);
            }

            // Add dynamic time entries
            for (int i = 0; i < dynamicTimeCount; i++) {
                View timeRow = containert.getChildAt(i);
                TextView timeText = timeRow.findViewById(R.id.timeoffeeding);
                String time = timeText.getText().toString();

                if (!time.isEmpty() && !time.equals(staticTime)) {
                    String dynamicStatus = getStatus(staticDate, time);
                    addTableRow(staticDate, time, "", dynamicStatus);
                }
            }

            selectDate.setEnabled(false);
            selectTime.setEnabled(false);
            addTbtn.setEnabled(false);
            setManual.setEnabled(false);
            feedqttycont.setEnabled(false);
            Savebtn.setVisibility(View.GONE);
            Resetbtn.setVisibility(View.GONE);
            Createbtn.setVisibility(View.VISIBLE);

            for (int i = 0; i < containert.getChildCount(); i++) {
                View timeRow = containert.getChildAt(i);
                Button selectTimeBtn = timeRow.findViewById(R.id.btnselecttime);
                ImageButton removeTimeBtn = timeRow.findViewById(R.id.removetime);
                if (selectTimeBtn != null) selectTimeBtn.setEnabled(false);
                if (removeTimeBtn != null) removeTimeBtn.setEnabled(false);
            }

            // Clear static inputs
            dateFS.setText("");
            timeFS.setText("");
            feedqttycont.setText("");

            // Remove dynamically added rows
            containert.removeViews(1, containert.getChildCount() - 1);

            // Reset text in first row (if needed)
            View firstTimeRow = containert.getChildAt(0);
            if (firstTimeRow != null) {
                TextView timeText = firstTimeRow.findViewById(R.id.timeoffeeding);
                if (timeText != null) timeText.setText("");
            }

        });

        // Disable initially
        selectDate.setEnabled(false);
        selectTime.setEnabled(false);
        setManual.setEnabled(false);
        feedqttycont.setEnabled(false);
        addTbtn.setEnabled(false);
        Savebtn.setVisibility(View.GONE);

        return view;
    }

    private void addTimeRow (LayoutInflater inflater){
        View row = inflater.inflate(R.layout.row_time, containert, false);
        //IDs in row_time
        TextView timedar = row.findViewById(R.id.timeoffeeding);
        Button selecttimedar = row.findViewById(R.id.btnselecttime);
        ImageButton removetimedar = row.findViewById(R.id.removetime);

        selecttimedar.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (TimePicker view, int selectedHour, int selectedMinute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        timedar.setText(formattedTime);
                    },
                    hour, minute, true // true = 24 hour format
            );
            timePickerDialog.show();
        });

        removetimedar.setOnClickListener(v -> containert.removeView(row));
        containert.addView(row);
    }

    private void addTableRow(String date, String time, String feedQty, String status) {
        TableRow row = new TableRow(getContext());

        row.addView(createCell(date));
        row.addView(createCell(time));
        row.addView(createCell(feedQty));
        row.addView(createCell(status));

        SummaryT.addView(row);
    }

    private TextView createCell(String text) {
        TextView tv = new TextView(getContext());
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);
        tv.setTextColor(Color.BLACK);
        return tv;
    }

    private String getStatus(String dateStr, String timeStr) {
        if (dateStr == null || dateStr.isEmpty() || timeStr == null || timeStr.isEmpty()) {
            return "Incomplete";
        }

        try {
            // Expecting format like "06/09/2025" and "01:45 PM"
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.getDefault());
            sdf.setLenient(false); // for stricter parsing

            Date scheduledDate = sdf.parse(dateStr + " " + timeStr);
            if (scheduledDate == null) return "Invalid";

            long currentMillis = System.currentTimeMillis();
            long diffMillis = scheduledDate.getTime() - currentMillis;

            if (diffMillis < 0) return "Past due";

            long totalMinutes = diffMillis / (60 * 1000);
            long hours = totalMinutes / 60;
            long minutes = totalMinutes % 60;

            if (hours > 0) {
                return hours + " hr" + (hours > 1 ? "s" : "") + " and " + minutes + " mn" + (minutes != 1 ? "s" : "") + " left";
            } else {
                return minutes + " mn" + (minutes != 1 ? "s" : "") + " left";
            }

        } catch (ParseException e) {
            return "Invalid";
        }
    }


}