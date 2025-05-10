package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Breed extends Fragment {

    String[] items = {"Tilapia", "Milkfish", "Shrimp", "Seaweed", "Carp", "Oyster", "Mussel"};
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    View view;
    EditText SOA, estDoh, intStockFish, NoDFish;
    Calendar calendar;
    TextView MortResult;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        // Initialize views
        SOA = view.findViewById(R.id.start_of_act);
        estDoh = view.findViewById(R.id.est_doh);
        intStockFish = view.findViewById(R.id.initialstock_input);
        NoDFish = view.findViewById(R.id.deadfish_input);
        AutoCompleteTextView breedDropdown = view.findViewById(R.id.auto_complete_txt1);
        Button editBtn = view.findViewById(R.id.edit_btn);
        Button saveBtn = view.findViewById(R.id.save_btn);
        Button mreditBtn = view.findViewById(R.id.mreditbtn);
        Button resetBtn = view.findViewById(R.id.resetbtn);
        Button calcBtn = view.findViewById(R.id.calculatebtn);
        MortResult = view.findViewById(R.id.mortrate_display);



        // Disable inputs initially
        SOA.setEnabled(false);
        breedDropdown.setEnabled(false);
        intStockFish.setEnabled(false);
        NoDFish.setEnabled(false);
        estDoh.setEnabled(false);

        // Setup dropdown adapter
        adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_items, items);
        breedDropdown.setAdapter(adapterItems);

        // Calendar setup
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view1, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateCalendar();
        };

        // Edit button logic
        editBtn.setOnClickListener(v -> {
            // Enable inputs when Edit is clicked
            SOA.setEnabled(true);
            breedDropdown.setEnabled(true);
            estDoh.setEnabled(true);

            // Set listeners only after enabling
            SOA.setOnClickListener(v1 -> new DatePickerDialog(
                    requireContext(), date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show());

            breedDropdown.setOnItemClickListener((parent, view1, position, id) -> {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(requireContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
            });

            saveBtn.setVisibility(View.VISIBLE);
        });

        // Save button logic
        saveBtn.setOnClickListener(v -> {
            // Disable inputs after Save is clicked
            SOA.setEnabled(false);
            breedDropdown.setEnabled(false);
            estDoh.setEnabled(false);

            breedDropdown.setFocusable(false);
            breedDropdown.setFocusableInTouchMode(false);
            breedDropdown.setClickable(false);

            saveBtn.setVisibility(View.GONE);
        });

        mreditBtn.setOnClickListener(v -> {
            intStockFish.setEnabled(true);
            NoDFish.setEnabled(true);

            mreditBtn.setEnabled(false);
            mreditBtn.setVisibility(View.GONE);
            resetBtn.setVisibility(View.VISIBLE);
            calcBtn.setVisibility(View.VISIBLE);
        });


        resetBtn.setOnClickListener(v -> {
            intStockFish.setText("");
            NoDFish.setText("");
            MortResult.setText("");

            // Disable fields again and hide buttons
            intStockFish.setEnabled(false);
            NoDFish.setEnabled(false);

            // Restore mreditBtn
            mreditBtn.setEnabled(true);
            mreditBtn.setVisibility(View.VISIBLE);

            resetBtn.setVisibility(View.GONE);
            calcBtn.setVisibility(View.GONE);
        });



        mreditBtn.setOnClickListener(v -> {
            intStockFish.setEnabled(true);
            NoDFish.setEnabled(true);
            intStockFish.setText("");
            NoDFish.setText("");

            mreditBtn.setVisibility(View.GONE);
            resetBtn.setVisibility(View.VISIBLE);
            calcBtn.setVisibility(View.VISIBLE);
        });

        resetBtn.setOnClickListener(v -> {
            intStockFish.setText("");
            NoDFish.setText("");
            MortResult.setText("");

            mreditBtn.setVisibility(View.VISIBLE);
            resetBtn.setVisibility(View.GONE);
            calcBtn.setVisibility(View.GONE);
        });

        calcBtn.setOnClickListener(v -> {
            String intStockFishStr = intStockFish.getText().toString();
            String noDFishStr = NoDFish.getText().toString();

            if (intStockFishStr.isEmpty() || noDFishStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter both values.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double intStockFishValue = Double.parseDouble(intStockFishStr);
                double noDFishValue = Double.parseDouble(noDFishStr);

                if (intStockFishValue == 0) {
                    Toast.makeText(requireContext(), "Initial stock of fish cannot be zero.", Toast.LENGTH_SHORT).show();
                } else {
                    double result = (noDFishValue / intStockFishValue) * 100;
                    MortResult.setText(String.format(Locale.US, "%.2f%%", result));
                    // Disable the fields after calculation
                    intStockFish.setEnabled(false);
                    NoDFish.setEnabled(false);

                    // Disable the edit button
                    mreditBtn.setEnabled(false);
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Invalid input, please enter valid numbers.", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void updateCalendar() {
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        SOA.setText(sdf.format(calendar.getTime()));
    }
}
