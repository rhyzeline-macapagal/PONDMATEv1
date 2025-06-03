package com.example.pondmatev1;

import android.content.Context;
import android.content.SharedPreferences;

public class BreedDataManager {
    private static final String PREF_NAME = "PondPrefs";
    private static final String KEY_BREED = "selectedBreed";
    private static final String KEY_DATE = "selectedDate";
    private static final String KEY_HARVEST_DATE = "harvestDate";
    private static final String KEY_FINGERLINGS = "numFingerlings";
    private static final String KEY_EST_DEAD = "estDead";
    private static final String KEY_MORTALITY = "mortalityRate";
    private final SharedPreferences prefs;

    public BreedDataManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveBreedData(String breed, String soaDate, String harvestDate, String fingerlings, String estDead, String mortalityRate) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BREED, breed);
        editor.putString(KEY_DATE, soaDate);
        editor.putString(KEY_HARVEST_DATE, harvestDate);
        editor.putString(KEY_FINGERLINGS, fingerlings);
        editor.putString(KEY_EST_DEAD, estDead);
        editor.putString(KEY_MORTALITY, mortalityRate);
        editor.apply();
    }

    public String getBreed() {
        return prefs.getString(KEY_BREED, null);
    }

    public String getSOADate() {
        return prefs.getString(KEY_DATE, null);
    }

    public String getHarvestDate() {
        return prefs.getString(KEY_HARVEST_DATE, null);
    }

    public String getFingerlings() {
        return prefs.getString(KEY_FINGERLINGS, null);
    }

    public String getEstDead() {
        return prefs.getString(KEY_EST_DEAD, null);
    }

    public String getMortalityRate() {
        return prefs.getString(KEY_MORTALITY, null);
    }
}
