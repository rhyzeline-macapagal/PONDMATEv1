package com.example.pondmatev1;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import java.net.HttpURLConnection;
import java.net.URL;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ControlsFeeder extends Fragment {

    private TextView feedleveltv;
    private Button monitorbtn, dispenseon, dispenseoff;

    private ImageView feedlvlicon;

    //for feed level status
    private final String[] levels = {"LOW", "MEDIUM", "HIGH"};
    private int currentIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.controlsfeeder, container, false);

        // Set current date and time in PH timezone
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Manila"));
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy - hh:mm a", Locale.ENGLISH);
        dateTimeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        String currentDateTime = dateTimeFormat.format(calendar.getTime());
        TextView textCurrentDate = view.findViewById(R.id.currentdate);
        textCurrentDate.setText(currentDateTime);

        feedleveltv = view.findViewById(R.id.feedlevel); //text view ng feed lvl
        monitorbtn = view.findViewById(R.id.monitorbttn); // to monitor feed lvl
        feedlvlicon = view.findViewById(R.id.feedLevelIcon); //icon ng feed lvl

        Button btnToggleFeeder = view.findViewById(R.id.btnToggleFeeder);
        TextView feederStatusText = view.findViewById(R.id.feederStatusText);

        final String baseUrl = "http://192.168.254.100"; // replace with your actual ESP IP
        final boolean[] isConnected = {false}; // using array to mutate inside inner class

        btnToggleFeeder.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    String endpoint = "/on";
                    URL url = new URL(baseUrl + endpoint);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(3000);
                    connection.setReadTimeout(3000);
                    int responseCode = connection.getResponseCode();
                    connection.disconnect();

                    requireActivity().runOnUiThread(() -> {
                        if (responseCode == 200) {
                            isConnected[0] = true;
                            btnToggleFeeder.setText("Connected");
                            btnToggleFeeder.setEnabled(false);
                            feederStatusText.setText("Status: Connected");
                        } else {
                            feederStatusText.setText("Error: HTTP " + responseCode);
                            btnToggleFeeder.setEnabled(true);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        feederStatusText.setText("Connection failed: " + e.getMessage());
                        btnToggleFeeder.setEnabled(true);
                    });
                }
            }).start();
        });




        //monitoring feed level
        monitorbtn.setOnClickListener(v -> {
            String level = levels[currentIndex];
            feedleveltv.setText(level);
            //to set color based on level
            switch (level) {
                case "LOW":
                    feedleveltv.setTextColor(Color.RED);
                    feedlvlicon.setImageResource(R.drawable.red);
                    break;
                case "MEDIUM":
                    feedleveltv.setTextColor(Color.rgb(255, 165, 0));
                    feedlvlicon.setImageResource(R.drawable.orange);
                    break;
                case "HIGH":
                    feedleveltv.setTextColor(Color.GREEN);
                    feedlvlicon.setImageResource(R.drawable.green);
                    break;
            }
            currentIndex = (currentIndex + 1) % levels.length;
        });
        return view;
    }
}
