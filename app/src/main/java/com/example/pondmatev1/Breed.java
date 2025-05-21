package com.example.pondmatev1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
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
    EditText estDoh, intStockFish, NoDFish;
    TextView SOA, MortResult, harvestDateView;
    Calendar calendar;
    Button btnSelectDate, btnSelectBreed, btnEditFbreed, btnEditMort;
    RadioGroup radioGroup;

    // Track editing state
    boolean isEditingBreed = false;
    boolean isEditingMort = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        MortResult = view.findViewById(R.id.txt_mortality_rate);
        btnSelectDate = view.findViewById(R.id.btn_select_date);
        btnSelectBreed = view.findViewById(R.id.btn_select_breed);
        harvestDateView = view.findViewById(R.id.harvestdate);
        intStockFish = view.findViewById(R.id.txt_initial_stock);
        NoDFish = view.findViewById(R.id.txt_est_dead);
        SOA = view.findViewById(R.id.txt_soa);

        btnEditFbreed = view.findViewById(R.id.btn_edit_fbreed);
        btnEditMort = view.findViewById(R.id.btn_edit_mort);

        calendar = Calendar.getInstance();

        setFishBreedEditable(false);
        setMortalityEditable(false);

        btnEditFbreed.setOnClickListener(v -> {
            if (!isEditingBreed) {
                // Confirm before enabling editing
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Edit")
                        .setMessage("Are you sure you want to edit the Fish Breed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            isEditingBreed = true;
                            setFishBreedEditable(true);
                            btnEditFbreed.setText("Save");
                            Toast.makeText(getContext(), "Editing Fish Breed enabled", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Save")
                        .setMessage("Are you sure to save?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Disable editing and save
                            isEditingBreed = false;
                            setFishBreedEditable(false);
                            btnEditFbreed.setText("Edit");
                            Toast.makeText(getContext(), "Fish Breed saved", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });



        btnSelectBreed.setOnClickListener(v -> {
            if (isEditingBreed) {
                BreedDialogFragment dialog = new BreedDialogFragment();
                dialog.setOnBreedSelectedListener(breedName -> btnSelectBreed.setText(breedName));
                dialog.show(getParentFragmentManager(), "breedDialog");
            } else {
                Toast.makeText(getContext(), "Click Edit to enable breed selection", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelectDate.setOnClickListener(v -> {
            if (isEditingBreed) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
                    // Set selected date as the start of activity
                    calendar.set(year1, month1, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    SOA.setText(sdf.format(calendar.getTime()));

                    // Compute and display harvest date (7 months later)
                    Calendar harvestCalendar = (Calendar) calendar.clone();
                    harvestCalendar.add(Calendar.MONTH, 7);
                    harvestDateView.setText(sdf.format(harvestCalendar.getTime())); // Replace with your actual TextView ID
                }, year, month, day);

                datePickerDialog.show();
            } else {
                Toast.makeText(getContext(), "Click Edit to enable date selection", Toast.LENGTH_SHORT).show();
            }
        });


        // Calculate mortality rate (simple function example)
        btnEditMort.setOnClickListener(v -> {
            if (!isEditingMort) {
                // Ask for confirmation before enabling edit mode
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Edit")
                        .setMessage("Are you sure you want to edit the mortality fields?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Enable editing
                            isEditingMort = true;
                            setMortalityEditable(true);
                            btnEditMort.setText("Save");
                            Toast.makeText(getContext(), "Editing Mortality fields enabled", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                // Show confirmation before saving
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Save")
                        .setMessage("Are you sure to save?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            try {
                                int initialStock = Integer.parseInt(intStockFish.getText().toString());
                                int deadFish = Integer.parseInt(NoDFish.getText().toString());

                                if (initialStock == 0) {
                                    Toast.makeText(getContext(), "Initial stock cannot be zero", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                double mortalityRate = (double) deadFish / initialStock * 100;
                                MortResult.setText(String.format(Locale.getDefault(), "%.2f%%", mortalityRate));

                                isEditingMort = false;
                                setMortalityEditable(false);
                                btnEditMort.setText("Edit");
                                Toast.makeText(getContext(), "Mortality fields saved", Toast.LENGTH_SHORT).show();
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });



        return view;
    }

    // Enable/Disable fish breed fields
    private void setFishBreedEditable(boolean enabled) {
        btnSelectBreed.setEnabled(enabled);
        btnSelectDate.setEnabled(enabled);
        SOA.setEnabled(enabled);
    }

    // Enable/Disable mortality fields
    private void setMortalityEditable(boolean enabled) {
        intStockFish.setEnabled(enabled);
        NoDFish.setEnabled(enabled);
        MortResult.setEnabled(false);  // Always disabled, just display result

        if (enabled) {
            // Clear "—" if present
            if ("—".equals(intStockFish.getText().toString().trim())) {
                intStockFish.setText("");
            }
            if ("—".equals(NoDFish.getText().toString().trim())) {
                NoDFish.setText("");
            }
        }
    }



}
