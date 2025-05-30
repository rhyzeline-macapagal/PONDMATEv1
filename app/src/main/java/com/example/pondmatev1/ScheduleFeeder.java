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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.itextpdf.kernel.geom.Line;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleFeeder extends Fragment {

    private LinearLayout containerd, containert;

    private ImageButton addDbtn, addTbtn;

    private Button selectDate, selectTime;

    private TextView dateFS, timeFS;

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
}






