package com.example.pondmatev1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fraghome extends Fragment {

    private LineChart lineChart;
    private List<String> xval;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fraghome, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.mortalityratechart);

        // Chart configuration
        Description desc = new Description();
        desc.setText("Monthly Mortality Rate in Aquaculture");
        desc.setPosition(450f, 15f);
        lineChart.setDescription(desc);
        lineChart.getAxisRight().setDrawLabels(false);

        List<String> xval = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xval));
        xAxis.setLabelCount(12, true);
        xAxis.setGranularity(1f);

        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(12f);
        xAxis.setDrawLabels(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setSpaceMin(0.5f);
        xAxis.setSpaceMax(0.5f);


        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setAxisLineColor(Color.BLACK);
        yAxis.setLabelCount(10);

        lineChart.invalidate();

        List<Entry> testEntries = new ArrayList<>();
        testEntries.add(new Entry(0, 10));
        testEntries.add(new Entry(1, 20));
        testEntries.add(new Entry(2, 30));

        LineDataSet testDataSet = new LineDataSet(testEntries, "Test Data");
        testDataSet.setColor(Color.RED);
        testDataSet.setCircleColor(Color.RED);
        testDataSet.setValueTextColor(Color.BLACK);

        LineData testLineData = new LineData(testDataSet);
        lineChart.setData(testLineData);
        lineChart.invalidate();

        // ViewModel + data loading
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        Map<Integer, List<Float>> savedData = loadData();
        viewModel.loadMortalityRates(savedData);

        // Observe data changes to update chart
        viewModel.getMortalityRates().observe(getViewLifecycleOwner(), mortalityMap -> {
            Log.d("FragHomeDebug", "Mortality data received: " + mortalityMap);
            if (mortalityMap != null) {
                Log.d("MortalityDebug", "Observed mortality data change: " + mortalityMap.toString());

                List<Entry> entries = new ArrayList<>();

                for (int month = 0; month < 12; month++) {
                    List<Float> rates = mortalityMap.get(month);
                    if (rates != null && !rates.isEmpty()) {
                        float sum = 0f;
                        for (Float r : rates) sum += r;
                        float avg = sum / rates.size();
                        entries.add(new Entry(month, avg));

                    }
                }

                LineDataSet dataSet = new LineDataSet(entries, "Mortality Rate (%)");
                dataSet.setColor(Color.RED);
                dataSet.setCircleColor(Color.RED);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setLineWidth(2f);

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // Refresh chart

                saveData(mortalityMap);
            }
        });
    }

    private static final String PREFS_NAME = "MortalityPrefs";
    private static final String KEY_DATA = "mortality_data";

    private void saveData(Map<Integer, List<Float>> data) {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Log.d("MortalityDebug", "Saved to SharedPreferences: " + new Gson().toJson(data));


        // Serialize data to JSON string (use Gson)
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(KEY_DATA, json);
        editor.apply();
    }

    private Map<Integer, List<Float>> loadData() {
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DATA, null);
        if (json == null) return new HashMap<>();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, List<Float>>>(){}.getType();
        return gson.fromJson(json, type);
    }

}
