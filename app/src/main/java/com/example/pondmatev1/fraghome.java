package com.example.pondmatev1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class fraghome extends Fragment {

    private LineChart lineChart;
    private static final String PREFS_NAME = "MortalityPrefs";
    private static final String KEY_DATA = "mortality_data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fraghome, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.mortalityratechart);
        Button generateReportBtn = view.findViewById(R.id.genreport);

        // Chart setup
        Description desc = new Description();
        desc.setText("Monthly Mortality Rate in Aquaculture");
        desc.setTextSize(16f);
        desc.setPosition(550f, 30f);
        lineChart.setDescription(desc);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.setExtraTopOffset(40f);

        List<String> xval = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

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
        yAxis.setLabelCount(20);

        lineChart.invalidate();

        // ViewModel setup
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        Map<Integer, List<Float>> savedData = loadData();
        viewModel.loadMortalityRates(savedData);

        // Observer for updating chart
        viewModel.getMortalityRates().observe(getViewLifecycleOwner(), mortalityMap -> {
            if (mortalityMap != null) {
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
                dataSet.setValueTextSize(10f);
                dataSet.setCircleColor(Color.RED);
                dataSet.setValueTextColor(Color.BLACK);
                dataSet.setLineWidth(2f);

                LineData lineData = new LineData(dataSet);
                lineChart.setData(lineData);
                lineChart.invalidate(); // Refresh chart

                saveData(mortalityMap);
            }
        });
        // Report generation

        generateReportBtn.setOnClickListener(v -> {
            Map<Integer, List<Float>> currentData = viewModel.getCurrentRates();

            if (currentData == null || currentData.isEmpty()) {
                Toast.makeText(requireContext(), "No data to generate report.", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> months = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

            StringBuilder reportBuilder = new StringBuilder();
            reportBuilder.append("📊 Mortality Rate Report\n\n");

            List<String> entryLabels = new ArrayList<>();
            List<int[]> indexMap = new ArrayList<>();

            for (int month = 0; month < 12; month++) {
                List<Float> rates = currentData.get(month);
                if (rates != null && !rates.isEmpty()) {
                    reportBuilder.append(xval.get(month)).append(":\n");

                    float sum = 0f;
                    for (int i = 0; i < rates.size(); i++) {
                        float entry = rates.get(i);
                        sum += entry;
                        reportBuilder.append("  • Entry ").append(i + 1)
                                .append(": ").append(String.format(Locale.US, "%.2f%%", entry)).append("\n");

                        entryLabels.add("Month: " + months.get(month) + ", Entry " + (i + 1) + ": " + String.format(Locale.US, "%.2f%%", entry));
                        indexMap.add(new int[]{month, i});
                    }

                    float avg = sum / rates.size();
                    reportBuilder.append("  ➤ Average: ")
                            .append(String.format(Locale.US, "%.2f%%", avg))
                            .append("\n\n");
                }
            }

            Log.d("ReportGen", reportBuilder.toString());

            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Mortality Report")
                    .setMessage(reportBuilder.toString())
                    .setPositiveButton("OK", null)
                    .setNeutralButton("Delete Entry", (dialog, which) -> {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Select Entry to Delete")
                                .setItems(entryLabels.toArray(new String[0]), (dialog2, which2) -> {
                                    int[] selected = indexMap.get(which2);
                                    String label = entryLabels.get(which2);

                                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                            .setTitle("Confirm Deletion")
                                            .setMessage("Are you sure you want to delete:\n\n" + label + "?")
                                            .setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {

                                                deleteEntry(selected[0], selected[1]);
                                            })
                                            .setNegativeButton("No", null)
                                            .show();
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    })
                    .show();
        });
    }

    private void saveData(Map<Integer, List<Float>> data) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString(KEY_DATA, json);
        editor.apply();

        Log.d("MortalityDebug", "Saved to SharedPreferences: " + json);
    }

    private Map<Integer, List<Float>> loadData() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_DATA, null);
        if (json == null) return new HashMap<>();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, List<Float>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void generatePdf(String content) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);

        int x = 40, y = 50;
        int lineHeight = 20;

        Paint paint = new Paint();
        paint.setTextSize(12);

        String[] lines = content.split("\n");
        Canvas canvas = page.getCanvas();

        for (String line : lines) {
            if (y + lineHeight > pageInfo.getPageHeight()) {
                document.finishPage(page);
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 50;
            }
            canvas.drawText(line, x, y, paint);
            y += lineHeight;
        }

        document.finishPage(page);

        String fileName = "Mortality_Report_" + System.currentTimeMillis() + ".pdf";

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PondMateReports");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, fileName);
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(getContext(), "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

    private void deleteEntry(int monthIndex, int entryIndexToRemove) {
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        Map<Integer, List<Float>> currentData = viewModel.getCurrentRates();

        if (currentData != null && currentData.containsKey(monthIndex)) {
            List<Float> monthRates = currentData.get(monthIndex);
            if (entryIndexToRemove >= 0 && entryIndexToRemove < monthRates.size()) {
                monthRates.remove(entryIndexToRemove);

                // Update ViewModel and SharedPreferences
                viewModel.loadMortalityRates(currentData);
                saveData(currentData);

                Toast.makeText(requireContext(), "Entry deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Invalid entry index.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
