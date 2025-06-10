package com.example.pondmatev1;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FishActivityScheduler {

    public static class FishActivity {
        public CalendarDay date;
        public String description;

        public FishActivity(CalendarDay date, String description) {
            this.date = date;
            this.description = description;
        }
    }

    public static List<FishActivity> generateSchedule(String breed, Calendar startDate) {
        List<FishActivity> schedule = new ArrayList<>();
        Calendar day = (Calendar) startDate.clone();

        switch (breed.toLowerCase()) {
            case "tilapia":
            case "bangus": {
                // Day 0: Preparation
                schedule.add(new FishActivity(toCalendarDay(day), "Preparation: Cleaning, drying, lason application, add water, stock fingerlings"));

                // Feeding phases every 30 days
                int[] feedOffsets = {30, 60, 90, 120};
                String[] feeds = {
                        "Pre-starter feeding",
                        "Starter feeding",
                        "Grower feeding",
                        "Finisher feeding"
                };
                for (int i = 0; i < feeds.length; i++) {
                    Calendar feedDay = (Calendar) startDate.clone();
                    feedDay.add(Calendar.DAY_OF_YEAR, feedOffsets[i]);
                    schedule.add(new FishActivity(toCalendarDay(feedDay), feeds[i]));
                }

                // Final day: Harvesting
                Calendar harvestDay = (Calendar) startDate.clone();
                harvestDay.add(Calendar.DAY_OF_YEAR, 120);
                schedule.add(new FishActivity(toCalendarDay(harvestDay), "Harvesting"));

                // Weekly maintenance for 4 months (~16 weeks)
                Calendar temp = (Calendar) startDate.clone();
                for (int i = 0; i < 16; i++) {
                    schedule.add(new FishActivity(toCalendarDay(temp), "Maintenance: Change water, vegetation control, katsa check"));
                    temp.add(Calendar.DAY_OF_YEAR, 7);
                }
                break;
            }
            case "alimango": {
                // Day 0: Preparation
                schedule.add(new FishActivity(toCalendarDay(day), "Preparation: Clean pond, dry bottom, lason application, add water, stock juvenile crabs"));

                // Feeding and growth stages over 9 months
                int[] feedOffsets = {60, 120, 180, 240, 270};
                String[] alimangoFeeds = {
                        "Feed: Crushed snails and soft trash fish (2x/day)",
                        "Feed: Trash fish + formulated grower feed",
                        "Feed: Whole fish, crushed shellfish",
                        "Feed: Reduced feeding, prepare for fattening",
                        "Feed: Finishing diet and harvest preparation"
                };
                for (int i = 0; i < alimangoFeeds.length; i++) {
                    Calendar feedDay = (Calendar) startDate.clone();
                    feedDay.add(Calendar.DAY_OF_YEAR, feedOffsets[i]);
                    schedule.add(new FishActivity(toCalendarDay(feedDay), alimangoFeeds[i]));
                }

                // Final day: Harvesting
                Calendar harvestDay = (Calendar) startDate.clone();
                harvestDay.add(Calendar.DAY_OF_YEAR, 270);
                schedule.add(new FishActivity(toCalendarDay(harvestDay), "Harvesting"));

                // Weekly maintenance for 9 months (~36 weeks)
                Calendar temp = (Calendar) startDate.clone();
                for (int i = 0; i < 36; i++) {
                    schedule.add(new FishActivity(toCalendarDay(temp), "Maintenance: Salinity check, net repair, water exchange"));
                    temp.add(Calendar.DAY_OF_YEAR, 7);
                }
                break;
            }
        }

        return schedule;
    }

    private static CalendarDay toCalendarDay(Calendar cal) {
        return CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }
}
