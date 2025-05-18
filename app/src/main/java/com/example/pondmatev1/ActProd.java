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
    private TableLayout logTable;

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
        logTable.removeAllViews();  // Clear previous content

        // Header row
        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(Color.parseColor("#f1f1f1"));

        int totalWidthInDp = 360;
        float scale = getResources().getDisplayMetrics().density;
        int columnWidthPx = (int) ((totalWidthInDp / 3f) * scale + 0.5f);

        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(columnWidthPx, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        TextView headerType = new TextView(getContext());
        headerType.setText("Type");
        headerType.setTextSize(18);
        headerType.setTextColor(getResources().getColor(android.R.color.black));
        headerType.setPadding(10, 10, 10, 10);
        headerType.setGravity(Gravity.CENTER);
        headerType.setLayoutParams(layoutParams);
        headerRow.addView(headerType);

        TextView headerDateTime = new TextView(getContext());
        headerDateTime.setText("Date & Time");
        headerDateTime.setTextSize(18);
        headerDateTime.setTextColor(getResources().getColor(android.R.color.black));
        headerDateTime.setPadding(10, 10, 10, 10);
        headerDateTime.setGravity(Gravity.CENTER);
        headerDateTime.setLayoutParams(layoutParams);
        headerRow.addView(headerDateTime);

        TextView headerDesc = new TextView(getContext());
        headerDesc.setText("Description");
        headerDesc.setTextSize(18);
        headerDesc.setTextColor(getResources().getColor(android.R.color.black));
        headerDesc.setPadding(10, 10, 10, 10);
        headerDesc.setGravity(Gravity.CENTER);
        headerDesc.setLayoutParams(layoutParams);
        headerRow.addView(headerDesc);

        logTable.addView(headerRow);

    // Display logs in table rows
        for (String[] log : activityLogs) {
            TableRow row = new TableRow(getContext());
            row.setBackgroundResource(R.drawable.transparentbg);
            row.setPadding(5, 5, 5, 5);

            TextView typeText = new TextView(getContext());
            typeText.setText(log[0]);
            typeText.setTextSize(16);
            typeText.setGravity(Gravity.CENTER);
            typeText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(typeText);

            TextView dateTimeText = new TextView(getContext());
            dateTimeText.setText(log[1]);
            dateTimeText.setTextSize(16);
            dateTimeText.setGravity(Gravity.CENTER);
            row.addView(dateTimeText);

            TextView descText = new TextView(getContext());
            descText.setText(log[2]);
            descText.setTextSize(16);
            descText.setGravity(Gravity.CENTER);
            descText.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            row.addView(descText);

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
