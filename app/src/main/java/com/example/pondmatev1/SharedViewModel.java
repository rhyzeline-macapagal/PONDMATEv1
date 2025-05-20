package com.example.pondmatev1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    }
    public void loadMortalityRates(Map<Integer, List<Float>> savedData) {
        if (savedData == null) savedData = new HashMap<>();
        mortalityRates.setValue(new HashMap<>(savedData));
    }


    public Map<Integer, List<Float>> getCurrentRates() {
        return mortalityRates.getValue();
    }

}
