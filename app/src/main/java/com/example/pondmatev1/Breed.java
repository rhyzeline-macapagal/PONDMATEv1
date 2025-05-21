package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Breed extends Fragment {

    ArrayList<String> breedList;
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    View view;
    EditText SOA, estDoh, intStockFish, NoDFish;
    Calendar calendar;
    TextView MortResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        breedList = new ArrayList<>();
        breedList.add("Tilapia");
        breedList.add("Milkfish");
        breedList.add("Shrimp");
        breedList.add("Seaweed");
        breedList.add("Carp");
        breedList.add("Oyster");
        breedList.add("Mussel");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        // Initialize views
        SOA = view.findViewById(R.id.start_of_act);
        estDoh = view.findViewById(R.id.est_doh);
        intStockFish = view.findViewById(R.id.initialstock_input);
        NoDFish = view.findViewById(R.id.deadfish_input);
        autoCompleteText = view.findViewById(R.id.auto_complete_txt1);
        Button editBtn = view.findViewById(R.id.edit_btn);
        Button saveBtn = view.findViewById(R.id.save_btn);
        Button mreditBtn = view.findViewById(R.id.mreditbtn);
        Button resetBtn = view.findViewById(R.id.resetbtn);
        Button calcBtn = view.findViewById(R.id.calculatebtn);
        Button addOptionBtn = view.findViewById(R.id.addOptionButton);
        MortResult = view.findViewById(R.id.mortrate_display);



        // Disable inputs initially
        SOA.setEnabled(false);
        SOA.setFocusable(false);
        SOA.setFocusableInTouchMode(false);
        SOA.setClickable(false);

        autoCompleteText.setEnabled(false);
        autoCompleteText.setFocusable(false);
        autoCompleteText.setFocusableInTouchMode(false);
        autoCompleteText.setClickable(false);

        estDoh.setEnabled(false);
        estDoh.setFocusable(false);
        estDoh.setFocusableInTouchMode(false);
        estDoh.setClickable(false);

        intStockFish.setEnabled(false);
        NoDFish.setEnabled(false);

        // Setup dropdown adapter
        adapterItems = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, breedList);
        autoCompleteText.setAdapter(adapterItems);


        addOptionBtn.setOnClickListener(v -> {
            String newItem = autoCompleteText.getText().toString().trim();

            if (!newItem.isEmpty() && !breedList.contains(newItem)) {
                breedList.add(newItem);
                adapterItems = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_dropdown_item_1line, breedList);
                autoCompleteText.setAdapter(adapterItems);
                autoCompleteText.setText("");
                autoCompleteText.showDropDown();
            }
        });

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
            SOA.setFocusable(true);
            SOA.setFocusableInTouchMode(true);
            SOA.setClickable(true);

            autoCompleteText.setEnabled(true);
            autoCompleteText.setFocusable(true);
            autoCompleteText.setFocusableInTouchMode(true);
            autoCompleteText.setClickable(true);

            estDoh.setEnabled(true);
            estDoh.setFocusable(true);
            estDoh.setFocusableInTouchMode(true);
            estDoh.setClickable(true);

            // Set listeners only after enabling
            SOA.setOnClickListener(v1 -> new DatePickerDialog(
                    requireContext(), date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show());

            autoCompleteText.setOnItemClickListener((parent, view1, position, id) -> {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(requireContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
            });

            saveBtn.setVisibility(View.VISIBLE);
        });

        // Save button logic
        saveBtn.setOnClickListener(v -> {
            // Get text from the fields
            String soaText = SOA.getText().toString().trim();
            String breedText = autoCompleteText.getText().toString().trim();
            String estDohText = estDoh.getText().toString().trim();

            // Check if any field is empty
            if (soaText.isEmpty() || breedText.isEmpty() || estDohText.isEmpty()) {
                Toast.makeText(requireContext(), "Please complete all required fields.", Toast.LENGTH_SHORT).show();
                return; // Stop execution here
            }

            // If valid, disable inputs and hide Save button
            SOA.setEnabled(false);
            SOA.setFocusable(false);
            SOA.setFocusableInTouchMode(false);
            SOA.setClickable(false);

            autoCompleteText.setEnabled(false);
            autoCompleteText.setFocusable(false);
            autoCompleteText.setFocusableInTouchMode(false);
            autoCompleteText.setClickable(false);

            estDoh.setEnabled(false);
            estDoh.setFocusable(false);
            estDoh.setFocusableInTouchMode(false);
            estDoh.setClickable(false);

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

                    // Get current month automatically
                    Calendar calendar = Calendar.getInstance();
                    int currentMonthIndex = calendar.get(Calendar.MONTH);

                    // Share the mortality rate with current month index
                    SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                    viewModel.addMortalityEntry(currentMonthIndex, (float) result);
                    viewModel.setContext(requireContext());
                    viewModel.persistData();
                    Log.d("BreedFragment", "Added mortality rate: " + result + " for month: " + currentMonthIndex);
                    Toast.makeText(requireContext(), "Mortality rate saved for month: " + currentMonthIndex, Toast.LENGTH_SHORT).show();

                    // Disable fields after calculation
                    intStockFish.setEnabled(false);
                    NoDFish.setEnabled(false);
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
