package com.example.pondmatev1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    EditText estDoh, numfingerlings;
    TextView SOA, MortResult, harvestDateView, intStockFish, NoDFish;
    Calendar calendar;
    Button btnSelectDate, btnSelectBreed, btnEditFbreed;
    RadioGroup radioGroup;

    boolean isEditingBreed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        MortResult = view.findViewById(R.id.txt_mortality_rate);
        btnSelectDate = view.findViewById(R.id.btn_select_date);
        btnSelectBreed = view.findViewById(R.id.btn_select_breed);
        harvestDateView = view.findViewById(R.id.harvestdate);
        intStockFish = view.findViewById(R.id.initialstockfingerlings);
        NoDFish = view.findViewById(R.id.txt_est_dead);
        SOA = view.findViewById(R.id.txt_soa);
        numfingerlings = view.findViewById(R.id.numoffingerlings);

        btnEditFbreed = view.findViewById(R.id.btn_edit_fbreed);

        calendar = Calendar.getInstance();

        setFishBreedEditable(false);

        btnEditFbreed.setOnClickListener(v -> {
            if (!isEditingBreed) {
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
                String selectedDate = SOA.getText().toString().trim();
                String breed = btnSelectBreed.getText().toString().trim();
                String fingerlings = numfingerlings.getText().toString().trim();
                String harvestDate = harvestDateView.getText().toString().trim();

                if (selectedDate.isEmpty() || breed.isEmpty() || fingerlings.isEmpty() || harvestDate.isEmpty()
                        || selectedDate.equals("—") || breed.equals("Select Breed") || harvestDate.equals("—")) {
                    Toast.makeText(getContext(), "Please fill out all required fields before saving.", Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Save")
                        .setMessage("Are you sure to save?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            isEditingBreed = false;
                            setFishBreedEditable(false);
                            btnEditFbreed.setText("Edit");
                            Toast.makeText(getContext(), "Fish Breed saved", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set initial EditText value if any saved in ViewModel
        Integer currentValue = viewModel.getNumOfFingerlings().getValue();
        if (currentValue != null) {
            numfingerlings.setText(String.valueOf(currentValue));
        }

        // Add TextWatcher to update ViewModel on input changes
        numfingerlings.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int value = Integer.parseInt(s.toString());
                    viewModel.setNumofFingerlings(value);
                } catch (NumberFormatException e) {
                    viewModel.setNumofFingerlings(null);
                }
                intStockFish.setText(s);
                try {
                    int stock = Integer.parseInt(s.toString());
                    int estDead = (int) Math.floor(stock * 0.10); // 10% rounded down
                    NoDFish.setText(String.valueOf(estDead));
                } catch (NumberFormatException e) {
                    NoDFish.setText("—");
                }
                updateMortalityRate();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnSelectBreed.setOnClickListener(v -> {
            if (isEditingBreed) {
                BreedDialogFragment dialog = new BreedDialogFragment();
                dialog.setOnBreedSelectedListener(breedName -> {
                    btnSelectBreed.setText(breedName);
                    viewModel.setSelectedBreed(breedName);
                });
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
                    calendar.set(year1, month1, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    SOA.setText(sdf.format(calendar.getTime()));

                    Calendar harvestCalendar = (Calendar) calendar.clone();
                    harvestCalendar.add(Calendar.MONTH, 7);
                    harvestDateView.setText(sdf.format(harvestCalendar.getTime()));
                }, year, month, day);

                datePickerDialog.show();
            } else {
                Toast.makeText(getContext(), "Click Edit to enable date selection", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setFishBreedEditable(boolean enabled) {
        btnSelectBreed.setEnabled(enabled);
        btnSelectDate.setEnabled(enabled);
        SOA.setEnabled(enabled);
        numfingerlings.setEnabled(enabled);

        if (enabled) {
            if ("—".equals(numfingerlings.getText().toString().trim())) {
                numfingerlings.setText("");
            }
        }
    }
    private void updateMortalityRate() {
        String intStockStr = intStockFish.getText().toString().trim();
        String estDeadStr = NoDFish.getText().toString().trim();

        if (!intStockStr.isEmpty() && !estDeadStr.isEmpty()) {
            try {
                double stock = Double.parseDouble(intStockStr);
                double dead = Double.parseDouble(estDeadStr);

                if (stock != 0) {
                    double result = (dead / stock) * 100;
                    MortResult.setText(String.format(Locale.US, "%.2f%%", result));
                } else {
                    MortResult.setText("0.00%");
                }
            } catch (NumberFormatException e) {
                MortResult.setText("—");
            }
        } else {
            MortResult.setText("—");
        }
    }

}
