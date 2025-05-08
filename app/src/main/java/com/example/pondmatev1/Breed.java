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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Breed extends Fragment {

    String[] items = {"Tilapia", "Milkfish", "Shrimp", "Seaweed", "Carp", "Oyster", "Mussel"};
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    View view;
    EditText SOA, estDoh, amtFishStocked;
    Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        // Initialize views
        SOA = view.findViewById(R.id.start_of_act);
        estDoh = view.findViewById(R.id.est_doh);
        amtFishStocked = view.findViewById(R.id.amt_fish_stocked_input);
        AutoCompleteTextView breedDropdown = view.findViewById(R.id.auto_complete_txt1);
        Button editBtn = view.findViewById(R.id.edit_btn);
        Button saveBtn = view.findViewById(R.id.save_btn);

        // Disable inputs initially
        SOA.setEnabled(false);
        breedDropdown.setEnabled(false);
        amtFishStocked.setEnabled(false);
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
            amtFishStocked.setEnabled(true);
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
            amtFishStocked.setEnabled(false);
            estDoh.setEnabled(false);

            breedDropdown.setFocusable(false);
            breedDropdown.setFocusableInTouchMode(false);
            breedDropdown.setClickable(false);

            saveBtn.setVisibility(View.GONE);
        });

        Button editButton = view.findViewById(R.id.mreditbtn);
        Button resetButton = view.findViewById(R.id.resetbtn);
        Button calculateButton = view.findViewById(R.id.calculatebtn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.GONE);
                resetButton.setVisibility(View.VISIBLE);
                calculateButton.setVisibility(View.VISIBLE);
            }
        });

        View.OnClickListener reverseAction = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editButton.setVisibility(View.VISIBLE);
                resetButton.setVisibility(View.GONE);
                calculateButton.setVisibility(View.GONE);
            }
        };

        resetButton.setOnClickListener(reverseAction);
        calculateButton.setOnClickListener(reverseAction);


        return view;
    }

    private void updateCalendar() {
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        SOA.setText(sdf.format(calendar.getTime()));
    }
}
