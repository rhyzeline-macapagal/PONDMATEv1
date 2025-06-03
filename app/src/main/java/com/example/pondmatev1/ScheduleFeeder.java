package com.example.pondmatev1;

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

import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleFeeder extends Fragment {

    private LinearLayout containerd, containert;

    private ImageButton addDbtn, addTbtn;

    private Button selectDate, selectTime, setManual, Createbtn, Savebtn;

    private TextView dateFS, timeFS, feedqttycont;
    private TableLayout SummaryT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.schedulefeeder, container, false);

        containerd = view.findViewById(R.id.datecontainer);
        addDbtn = view.findViewById(R.id.adddatebtn);
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
        SummaryT= view.findViewById(R.id.summaryTable);

        //select date and time
        selectDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedMonth + 1, selectedDay, selectedYear);
                        dateFS.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        selectTime.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireContext(),
                    (view1, selectedHour, selectedMinute) -> {
                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d",
                                selectedHour, selectedMinute);
                        timeFS.setText(formattedTime);
                    },
                    hour, minute, true);
            timePickerDialog.show();
        });

        //dynamically added row
        addDbtn.setOnClickListener(v ->addDateRow(inflater));
        addTbtn.setOnClickListener(v ->addTimeRow(inflater));

        //editbutton
        Createbtn.setOnClickListener(v -> {
            selectDate.setEnabled(true);
            selectTime.setEnabled(true);
            setManual.setEnabled(true);
            addDbtn.setEnabled(true);
            addTbtn.setEnabled(true);
            feedqttycont.setEnabled(true);

            for (int i = 0; i < containerd.getChildCount(); i++) {
                View dateRow = containerd.getChildAt(i);
                Button selectDateBtn = dateRow.findViewById(R.id.btnselectdate);
                ImageButton removeDateBtn = dateRow.findViewById(R.id.removedate);
                if (selectDateBtn != null) selectDateBtn.setEnabled(true);
                if (removeDateBtn != null) removeDateBtn.setEnabled(true);
            }
            for (int i = 0; i < containert.getChildCount(); i++) {
                View timeRow = containert.getChildAt(i);
                Button selectTimeBtn = timeRow.findViewById(R.id.btnselecttime);
                ImageButton removeTimeBtn = timeRow.findViewById(R.id.removetime);
                if (selectTimeBtn != null) selectTimeBtn.setEnabled(true);
                if (removeTimeBtn != null) removeTimeBtn.setEnabled(true);
            }

            Savebtn.setVisibility(View.VISIBLE);
        });

        //savebutton
        Savebtn.setOnClickListener(v -> {
            SummaryT.removeViews(1, SummaryT.getChildCount() - 1);
            String staticDate = dateFS.getText().toString();
            String staticTime = timeFS.getText().toString();
            String feedQuantity = feedqttycont.getText().toString();
            String status = getStatus(staticDate, staticTime);

            addTableRow(staticDate, staticTime, feedQuantity, status);

            selectDate.setEnabled(false);
            selectTime.setEnabled(false);
            addDbtn.setEnabled(false);
            addTbtn.setEnabled(false);
            setManual.setEnabled(false);
            feedqttycont.setEnabled(false);
            Savebtn.setVisibility(View.GONE);

            for (int i = 0; i < containerd.getChildCount(); i++) {
                View dateRow = containerd.getChildAt(i);
                Button selectDateBtn = dateRow.findViewById(R.id.btnselectdate);
                ImageButton removeDateBtn = dateRow.findViewById(R.id.removedate);
                if (selectDateBtn != null) selectDateBtn.setEnabled(false);
                if (removeDateBtn != null) removeDateBtn.setEnabled(false);
            }

            for (int i = 0; i < containert.getChildCount(); i++) {
                View timeRow = containert.getChildAt(i);
                Button selectTimeBtn = timeRow.findViewById(R.id.btnselecttime);
                ImageButton removeTimeBtn = timeRow.findViewById(R.id.removetime);
                if (selectTimeBtn != null) selectTimeBtn.setEnabled(false);
                if (removeTimeBtn != null) removeTimeBtn.setEnabled(false);
            }

            int dateCount = containerd.getChildCount();
            int timeCount = containert.getChildCount();
            int maxCount = Math.max(dateCount, timeCount);

            for (int i = 1; i < maxCount; i++) {
                String dynDate = "";
                String dynTime = "";

                if (i < dateCount) {
                    View dateRow = containerd.getChildAt(i);
                    TextView dateText = dateRow.findViewById(R.id.dateoffeedingschedule);
                    dynDate = dateText.getText().toString();
                }

                if (i < timeCount) {
                    View timeRow = containert.getChildAt(i);
                    TextView timeText = timeRow.findViewById(R.id.timeoffeeding);
                    dynTime = timeText.getText().toString();
                }

                String dynamicStatus = getStatus(dynDate, dynTime);
                addTableRow(dynDate, dynTime, "", dynamicStatus); // No feed quantity for dynamic rows
            }

        });

        // Disable initially
        selectDate.setEnabled(false);
        selectTime.setEnabled(false);
        setManual.setEnabled(false);
        feedqttycont.setEnabled(false);
        addDbtn.setEnabled(false);
        addTbtn.setEnabled(false);
        Savebtn.setVisibility(View.GONE);

        return view;
    }

    private void addDateRow (LayoutInflater inflater){
        View row = inflater.inflate(R.layout.row_date, containerd, false);

        //ids inside row_date
        TextView datedar = row.findViewById(R.id.dateoffeedingschedule);
        Button selectdatedar = row.findViewById(R.id.btnselectdate);
        ImageButton removedatedar = row.findViewById(R.id.removedate);

        selectdatedar.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                selectedMonth + 1, selectedDay, selectedYear);
                        datedar.setText(formattedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        removedatedar.setOnClickListener(v -> containerd.removeView(row));
        containerd.addView (row);
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
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
            Calendar now = Calendar.getInstance();
            Calendar schedule = Calendar.getInstance();
            schedule.setTime(sdf.parse(dateStr + " " + timeStr));

            long diffMillis = schedule.getTimeInMillis() - now.getTimeInMillis();

            if (diffMillis < 0) return "Past due";

            long mins = diffMillis / (60 * 1000);
            return mins + " mins left";
        } catch (Exception e) {
            return "Invalid";
        }
    }


}






