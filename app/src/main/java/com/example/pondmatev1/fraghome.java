package com.example.pondmatev1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.*;

public class fraghome extends Fragment {

    private MaterialCalendarView calendarView;
    private final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private TextView noteText;
    private SharedPreferences sharedPreferences;
    private SharedViewModel sharedViewModel;

    private final HashMap<CalendarDay, List<String>> scheduledActivityMap = new HashMap<>();
    private static final String PREF_NAME = "ActivityPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fraghome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        calendarView = view.findViewById(R.id.calendarView);
        noteText = view.findViewById(R.id.noteText);

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

        observeBreedAndStartDate();
    }

    private void showActivityDialog(CalendarDay date) {
        List<String> activities = scheduledActivityMap.get(date);
        if (activities == null || activities.isEmpty()) {
            noteText.setText("No scheduled activities.");
            return;
        }

        SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
        String formattedDate = displayFormat.format(date.getDate());

        StringBuilder noteContent = new StringBuilder();
        noteContent.append("Activities for ").append(formattedDate).append(":\n\n");

        for (String activity : activities) {
            noteContent.append(activity).append("\n");
        }

        noteText.setText(noteContent.toString().trim());
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

        highlightActivityDates();
    }

    private void highlightActivityDates() {
        List<CalendarDay> scheduleDates = new ArrayList<>(scheduledActivityMap.keySet());
        if (!scheduleDates.isEmpty()) {
            calendarView.addDecorator(new EventDecorator(Color.BLUE, scheduleDates));
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
        private final Set<CalendarDay> dates;

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
