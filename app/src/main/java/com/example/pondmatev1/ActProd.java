package com.example.pondmatev1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ActProd extends Fragment {

    private MaterialCalendarView calendarView;
    private final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private LinearLayout logTable;
    private final List<String[]> activityLogs = new ArrayList<>();
    private final HashMap<CalendarDay, List<String>> scheduledActivityMap = new HashMap<>();

    private TextView noteText;

    private SharedPreferences sharedPreferences;
    private SharedViewModel sharedViewModel;

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

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        calendarView = view.findViewById(R.id.calendarView);
        logTable = view.findViewById(R.id.logTable);

        loadLogs();
        displayLogs();
        observeBreedAndStartDate();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (scheduledActivityMap.containsKey(date)) {
                showActivityDialog(date);
            }
        });

        Date soa = sharedViewModel.getSOADate().getValue();
        String breed = sharedViewModel.getSelectedBreed().getValue();
        if (soa != null && breed != null) {
            generateFishScheduleAndDecorateCalendar(breed, soa);
        }
        noteText = view.findViewById(R.id.noteText);

    }

    private void showActivityDialog(CalendarDay date) {
        List<String> activities = scheduledActivityMap.get(date);
        if (activities == null || activities.isEmpty()) {
            noteText.setText("No scheduled activities.");
            return;
        }

        StringBuilder noteContent = new StringBuilder();
        noteContent.append("📅 ").append(dateOnlyFormat.format(date.getDate())).append("\n\n");
        for (String activity : activities) {
            noteContent.append("• ").append(activity).append("\n");
        }

        noteText.setText(noteContent.toString().trim());
    }


    private void displayLogs() {
        logTable.removeAllViews();

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

    private void highlightActivityDates() {
        calendarView.removeDecorators();

        // RED: User-logged dates
        List<CalendarDay> logDates = new ArrayList<>();
        for (String[] log : activityLogs) {
            if (log.length > 1) {
                try {
                    String[] parts = log[1].split(" ")[0].split("-"); // yyyy-MM-dd
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1;
                    int day = Integer.parseInt(parts[2]);
                    logDates.add(CalendarDay.from(year, month, day));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!logDates.isEmpty()) {
            calendarView.addDecorator(new EventDecorator(Color.RED, logDates));
        }

        // BLUE: Fish schedule
        List<CalendarDay> scheduleDates = new ArrayList<>(scheduledActivityMap.keySet());
        if (!scheduleDates.isEmpty()) {
            calendarView.addDecorator(new EventDecorator(Color.BLUE, scheduleDates));
        }
    }

    private void generateFishScheduleAndDecorateCalendar(String breed, Date soaDate) {
        calendarView.removeDecorators();

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(soaDate);

        List<FishActivityScheduler.FishActivity> schedule =
                FishActivityScheduler.generateSchedule(breed, startDate);

        scheduledActivityMap.clear();

        for (FishActivityScheduler.FishActivity act : schedule) {
            CalendarDay day = act.date;
            scheduledActivityMap.computeIfAbsent(day, k -> new ArrayList<>()).add(act.description);
        }

        displayLogs();
        highlightActivityDates();
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

    private void observeBreedAndStartDate() {
        sharedViewModel.getSelectedBreed().observe(getViewLifecycleOwner(), breed -> {
            Date soa = sharedViewModel.getSOADate().getValue();
            if (breed != null && soa != null) {
                generateFishScheduleAndDecorateCalendar(breed, soa);
            }
        });

        sharedViewModel.getSOADate().observe(getViewLifecycleOwner(), date -> {
            String breed = sharedViewModel.getSelectedBreed().getValue();
            if (breed != null && date != null) {
                generateFishScheduleAndDecorateCalendar(breed, date);
            }
        });
    }

    public static class EventDecorator implements DayViewDecorator {
        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, List<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10, color));
        }
    }
}
