package com.example.pondmatev1;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedViewModel extends ViewModel {
    // Map month index -> list of mortality rates for that month
    private final MutableLiveData<Map<Integer, List<Float>>> mortalityRates = new MutableLiveData<>(new HashMap<>());

    public LiveData<Map<Integer, List<Float>>> getMortalityRates() {
        return mortalityRates;
    }

    public void addMortalityEntry(int month, float value) {
        Map<Integer, List<Float>> currentMap = mortalityRates.getValue();
        if (currentMap == null) {
            currentMap = new HashMap<>();
        }

        // Important: Create a new map to trigger LiveData update
        currentMap = new HashMap<>(currentMap);

        List<Float> monthList = currentMap.get(month);
        if (monthList == null) {
            monthList = new ArrayList<>();
        } else {
            // Create a new list to avoid modifying old one directly
            monthList = new ArrayList<>(monthList);
        }
        monthList.add(value);

        currentMap.put(month, monthList);
        mortalityRates.setValue(currentMap); // Trigger observer

        Log.d("SharedViewModel", "Month: " + month + ", Rate: " + value + ", Updated Map: " + currentMap);
    }
    public void loadMortalityRates(Map<Integer, List<Float>> savedData) {
        if (savedData == null) savedData = new HashMap<>();
        mortalityRates.setValue(new HashMap<>(savedData));
    }

    public Map<Integer, List<Float>> getCurrentRates() {
        return mortalityRates.getValue();
    }
    private Context context;

    public void setContext(Context context) {
        this.context = context.getApplicationContext();
    }

    public void persistData() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("MortalityPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(getCurrentRates());
        editor.putString("mortality_data", json);
        editor.apply();

        Log.d("SharedViewModel", "Data persisted: " + json);
    }


}
