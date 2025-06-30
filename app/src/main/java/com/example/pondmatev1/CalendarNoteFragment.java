package com.example.pondmatev1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CalendarNoteFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView noteText;
    private final HashMap<CalendarDay, List<String>> scheduledActivityMap = new HashMap<>();
    private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        calendarView = view.findViewById(R.id.calendarView);
        noteText = view.findViewById(R.id.noteText);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            List<String> activities = scheduledActivityMap.get(date);
            if (activities != null && !activities.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Activities for ").append(displayFormat.format(date.getDate())).append(":\n\n");
                for (String activity : activities) {
                    builder.append(activity).append("\n");
                }
                noteText.setText(builder.toString().trim());
            } else {
                noteText.setText("No scheduled activities.");
            }
        });

        Date soa = sharedViewModel.getSOADate().getValue();
        String breed = sharedViewModel.getSelectedBreed().getValue();
        if (soa != null && breed != null) {
            generateSchedule(breed, soa);
        }
    }

    private void generateSchedule(String breed, Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        List<FishActivityScheduler.FishActivity> schedule = FishActivityScheduler.generateSchedule(breed, calendar);

        scheduledActivityMap.clear();
        for (FishActivityScheduler.FishActivity act : schedule) {
            CalendarDay day = act.date;
            scheduledActivityMap.computeIfAbsent(day, k -> new java.util.ArrayList<>()).add(act.description);
        }

        calendarView.addDecorator(new ActProd.EventDecorator(
                getResources().getColor(android.R.color.holo_blue_dark),
                new java.util.ArrayList<>(scheduledActivityMap.keySet())
        ));
    }
}
