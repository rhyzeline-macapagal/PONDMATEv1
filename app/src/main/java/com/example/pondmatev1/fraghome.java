package com.example.pondmatev1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.Arrays;
import java.util.List;

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

        Description desc = new Description();
        desc.setText("Monthly Mortality Rate of ---- in Aquaculture");
        desc.setPosition(150f, 15f);
        lineChart.setDescription(desc);
        lineChart.getAxisRight().setDrawLabels(false);

        xval = Arrays.asList("Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec");
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xval));
        xAxis.setLabelCount(12, true); // true to force exactly 12 labels
        xAxis.setGranularity(1f);


    }
}
