package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActProd extends Fragment {

    private EditText typeEditText, dateTimeEditText, descEditText;
    private LinearLayout logTable;

    private final List<String[]> activityLogs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "ActivityPrefs";
    private static final String LOGS_KEY = "logs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actprod, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Bind views
        typeEditText = view.findViewById(R.id.ToA);
        dateTimeEditText = view.findViewById(R.id.dateTimeEditText);
        descEditText = view.findViewById(R.id.descEditText);
        Button createBtn = view.findViewById(R.id.createbtn);
        logTable = view.findViewById(R.id.logTable);

        // restrict to character input
        InputFilter letterOnlyFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().matches("[a-zA-Z ]+")) {
                    return source;
                }
                return "";
            }
        };
        descEditText.setFilters(new InputFilter[]{letterOnlyFilter});
        typeEditText.setFilters(new InputFilter[]{letterOnlyFilter});

        // Date-Time picker
        dateTimeEditText.setOnClickListener(v -> showDateTimePicker());

        // Add log on button click
        createBtn.setOnClickListener(v -> addActivityLog());

        // Load existing logs
        loadLogs();
        displayLogs();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view1, year, month, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(),
                            (view2, hourOfDay, minute) -> {
                                String dateTime = String.format("%04d-%02d-%02d %02d:%02d",
                                        year, month + 1, dayOfMonth, hourOfDay, minute);
                                dateTimeEditText.setText(dateTime);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void addActivityLog() {
        String type = typeEditText.getText().toString().trim();
        String dateTime = dateTimeEditText.getText().toString().trim();
        String desc = descEditText.getText().toString().trim();

        if (!type.isEmpty() && !dateTime.isEmpty() && !desc.isEmpty()) {
            String[] log = {type, dateTime, desc};
            activityLogs.add(log);
            saveLogs();
            displayLogs();

            // Clear input fields
            typeEditText.setText("");
            dateTimeEditText.setText("");
            descEditText.setText("");
        }
    }

    private void displayLogs() {
        logTable.removeAllViews(); // Clear previous content

        // Header row
        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(Color.parseColor("#f1f1f1"));

        TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        String[] headers = {"Type", "Date & Time", "Description"};
        for (String title : headers) {
            TextView header = new TextView(getContext());
            header.setText(title);
            header.setTextSize(18);
            header.setTextColor(getResources().getColor(android.R.color.black));
            header.setGravity(Gravity.CENTER);
            header.setPadding(16, 16, 16, 16);
            header.setLayoutParams(cellParams);
            headerRow.addView(header);
        }

        logTable.addView(headerRow);

        // Data rows
        for (String[] log : activityLogs) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.transparentbg);
            row.setPadding(8, 8, 8, 8);

            for (String value : log) {
                TextView cell = new TextView(getContext());
                cell.setText(value);
                cell.setTextSize(16);
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(8, 8, 8, 8);
                cell.setLayoutParams(cellParams);
                row.addView(cell);
            }

            logTable.addView(row);
        }
    }


    private void saveLogs() {
        JSONArray jsonArray = new JSONArray();
        for (String[] log : activityLogs) {
            try {
                jsonArray.put(new JSONArray(log));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sharedPreferences.edit().putString(LOGS_KEY, jsonArray.toString()).apply();
    }

    private void loadLogs() {
        activityLogs.clear();
        String savedLogs = sharedPreferences.getString(LOGS_KEY, null);
        if (savedLogs != null) {
            try {
                JSONArray jsonArray = new JSONArray(savedLogs);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray logArray = jsonArray.getJSONArray(i);
                    String[] log = new String[logArray.length()];
                    for (int j = 0; j < logArray.length(); j++) {
                        log[j] = logArray.getString(j);
                    }
                    activityLogs.add(log);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
