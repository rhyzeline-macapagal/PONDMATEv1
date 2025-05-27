package com.example.pondmatev1;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.icu.text.DisplayOptions;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class pcostroi extends Fragment {

    ArrayList<String> breedList, maintenanceOptions;
    Button editButton, saveButton, generateReportButton,TypeofFeeders, initialMaintenancetype, initialMaintenanceType, maintenanceType;
    EditText amtFeeders, maintenance, Capital, Labor, fperpiece, maintenanceCost;
    LinearLayout maintenanceList, feedersContainer, maintenanceContainer;
    TextView totalExpenses, fishbreeddisplay, numfingerlingsdisplay, amtFingerlings;
    ImageButton addMaintenanceButton, addBtn, removeBtn;
    EditText initialMaintenanceCost;


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

        maintenanceOptions = new ArrayList<>(Arrays.asList(
                "Water Change", "Water Monitoring", "Waste Removal", "Algae Control",
                "Cleaning Ponds & Filters", "Leak Repair", "Inspection",
                "Pump & Pipe Maintenance", "Parasite Treatment", "Net or Screen Repair", "Others"
        ));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pcostroi, container, false);
        // Initialize views
        amtFingerlings = view.findViewById(R.id.amtoffingerlings);
        amtFeeders = view.findViewById(R.id.amtoffeeders);
        initialMaintenancetype = view.findViewById(R.id.initialMaintenanceType);
        fperpiece = view.findViewById(R.id.amtperpiece);
        initialMaintenanceCost = view.findViewById(R.id.initialMaintenanceCost);
        totalExpenses = view.findViewById(R.id.totalexpenses);
        editButton = view.findViewById(R.id.editbtn);
        saveButton = view.findViewById(R.id.savebtn);
        maintenanceList = view.findViewById(R.id.maintenanceList);
        fishbreeddisplay = view.findViewById(R.id.fishbreedpcostdisplay);
        numfingerlingsdisplay = view.findViewById(R.id.numoffingerlings);
        TypeofFeeders = view.findViewById(R.id.typeoffeeders);
        Capital = view.findViewById(R.id.capital);
        Labor = view.findViewById(R.id.labor);

        addMaintenanceButton = view.findViewById(R.id.addMaintenanceButton);
        generateReportButton = view.findViewById(R.id.generatereport);
        feedersContainer = view.findViewById(R.id.feeders_container);
        addBtn = view.findViewById(R.id.addToFeedsbtn);

        addBtn.setOnClickListener(v -> {
            addFeederRow(feedersContainer);
        });

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedBreed().observe(getViewLifecycleOwner(), breed -> {
            fishbreeddisplay.setText(breed);
        });

        viewModel.getNumOfFingerlings().observe(getViewLifecycleOwner(), num -> {
            if (num != null) {
                numfingerlingsdisplay.setText(String.valueOf(num));
            } else {
                numfingerlingsdisplay.setText("");
            }
            calculateFingerlingsAmount();
        });

        fperpiece.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                calculateFingerlingsAmount();
            }
        });

        amtFeeders.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateTotalExpenses();
            }
        });

        TextWatcher expenseWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                updateTotalExpenses();
            }
        };
        amtFingerlings.addTextChangedListener(expenseWatcher);
        amtFeeders.addTextChangedListener(expenseWatcher);
        Capital.addTextChangedListener(expenseWatcher);
        Labor.addTextChangedListener(expenseWatcher);
        initialMaintenanceCost.addTextChangedListener(expenseWatcher);

        // restrict to character input
        InputFilter letterOnlyFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().matches("[a-zA-Z ]+")) {
                    return source;
                }
                return "";
            }
        };

        initialMaintenancetype.setFilters(new InputFilter[]{letterOnlyFilter});
        // Set fields to non-editable initially
        setEditable(false);

        // Handle Edit button
        editButton.setOnClickListener(v -> {
            setEditable(true);
            saveButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.GONE);
        });

        // Handle Save button
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                setEditable(false);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(requireContext(), "Please fill out all required fields", Toast.LENGTH_SHORT).show();
            }
        });

        List<String> feederOptions = new ArrayList<>(Arrays.asList("Starter", "Grower", "Finisher", "Others"));
        TypeofFeeders.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), TypeofFeeders);
            for (String option : feederOptions) popupMenu.getMenu().add(option);
            popupMenu.setOnMenuItemClickListener(item -> {
                String selected = item.getTitle().toString();
                if (selected.equals("Others")) {
                    showAddFeederDialog(TypeofFeeders, feederOptions);
                } else {
                    TypeofFeeders.setText(selected);
                }
                return true;
            });
            popupMenu.show();
        });

        maintenanceContainer = view.findViewById(R.id.maintenance_container);
        addMaintenanceButton.setOnClickListener(v -> addMaintenanceRow(maintenanceContainer));
        initialMaintenancetype.setOnClickListener(v -> showMaintenanceMenu(initialMaintenancetype));

        addFeederButton.setOnClickListener(v -> {
            addFeederRow(feedersContainer);
        });

        addMaintenanceButton.setOnClickListener(v -> {
            addMaintenanceRow(maintenanceList);
        });


        return view;
    }

    private void setEditable(boolean editable) {
        EditText[] fields = {amtFeeders,  initialMaintenanceCost, Capital, Labor};
        for (EditText field : fields) {
            field.setFocusable(editable);
            field.setFocusableInTouchMode(editable);
            field.setClickable(editable);
            field.setCursorVisible(editable);

        }
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            cost.setFocusable(editable);
            cost.setFocusableInTouchMode(editable);
            cost.setClickable(editable);
            cost.setCursorVisible(editable);
        }
    }
    private void updateTotalExpenses() {
        double total = 0;

        total += parseDoubleFromTextView(amtFingerlings);
        total += parseDoubleFromEditText(Capital);
        total += parseDoubleFromEditText(Labor);
        total += parseDoubleFromEditText(initialMaintenanceCost);

        for (int i = 0; i < feedersContainer.getChildCount(); i++) {
            View row = feedersContainer.getChildAt(i);
            EditText feedCost = row.findViewById(R.id.amtoffeeders);
            total += parseDoubleFromEditText(feedCost);
        }

        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            total += parseDoubleFromEditText(cost);
        }

        totalExpenses.setText(String.format("Total: %.2f", total));
    }


    private double parseDoubleFromEditText(EditText editText) {
        try {
            return Double.parseDouble(editText.getText().toString().replace("₱", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDoubleFromTextView(TextView tv) {
        try {
            return Double.parseDouble(tv.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Method to add new maintenance row dynamically
    private void addMaintenanceRow(LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View maintenanceRow = inflater.inflate(R.layout.row_maintenance, container, false);

        maintenanceType = maintenanceRow.findViewById(R.id.maintenanceType);
        maintenanceCost = maintenanceRow.findViewById(R.id.maintenanceCost);
        removeBtn = maintenanceRow.findViewById(R.id.removeMaintenanceButton);

        maintenanceCost.setFocusable(true);
        maintenanceCost.setFocusableInTouchMode(true);
        maintenanceCost.setClickable(true);
        maintenanceCost.setCursorVisible(true);

        maintenanceType.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), maintenanceType);
            for (String option : maintenanceOptions) {
                popupMenu.getMenu().add(option);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                String selected = item.getTitle().toString();
                if (selected.equals("Others")) {
                    showAddCustomMaintenanceDialog(maintenanceType, maintenanceOptions);
                } else {
                    maintenanceType.setText(selected);
                }
                return true;
            });

            popupMenu.show();
        });

        removeBtn.setOnClickListener(v -> container.removeView(maintenanceRow));
        container.addView(maintenanceRow);
    }
    private void showAddCustomMaintenanceDialog(Button targetButton, List<String> optionsList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Custom Maintenance");
        final EditText input = new EditText(getContext());
        input.setHint("Enter custom type");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String customOption = input.getText().toString().trim();
            if (!customOption.isEmpty()) {
                optionsList.add(customOption);
                targetButton.setText(customOption);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void calculateFingerlingsAmount() {
        double perPiece = 0;
        int numFingerlings = 0;

        try {
            perPiece = Double.parseDouble(fperpiece.getText().toString().trim());
        } catch (NumberFormatException e) {
            perPiece = 0;
        }

        try {
            numFingerlings = Integer.parseInt(numfingerlingsdisplay.getText().toString().trim());
        } catch (NumberFormatException e) {
            numFingerlings = 0;
        }

        double total = perPiece * numFingerlings;
        amtFingerlings.setText(String.format("%.2f", total));
    }
    private boolean validateInputs() {
        // Check static required fields
        if (amtFingerlings.getText().toString().trim().isEmpty()) return false;
        if (amtFeeders.getText().toString().trim().isEmpty()) return false;
        if (initialMaintenanceCost.getText().toString().trim().isEmpty()) return false;

        if (initialMaintenancetype.getText().toString().trim().isEmpty()) return false;
        //if (autoCompleteText.getText().toString().trim().isEmpty()) return false;

        // Check dynamic maintenance fields
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText type = row.findViewById(R.id.maintenanceType);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            if (type.getText().toString().trim().isEmpty() || cost.getText().toString().trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }
    private void showAddFeederDialog(Button feederButton, List<String> feederOptions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter custom feeder type");

        final EditText input = new EditText(requireContext());
        input.setHint("e.g. Booster");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newOption = input.getText().toString().trim();
            if (!newOption.isEmpty()) {
                feederOptions.add(feederOptions.size() - 1, newOption); // Add before "Others"
                feederButton.setText(newOption);
            } else {
                Toast.makeText(getContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void addFeederRow(LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View row = inflater.inflate(R.layout.row_feeders, container, false);

        Button feederType = row.findViewById(R.id.typeoffeeders);
        EditText amount = row.findViewById(R.id.amtoffeeders);
        ImageButton removeBtn = row.findViewById(R.id.removeMaintenanceButton);

        amount.setFocusable(true);
        amount.setFocusableInTouchMode(true);
        amount.setClickable(true);
        amount.setCursorVisible(true);

        // Dropdown logic (same as previous popup menu)
        List<String> feederOptions = new ArrayList<>(Arrays.asList("Starter", "Grower", "Finisher", "Others"));
        feederType.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), feederType);
            for (String option : feederOptions) {
                popupMenu.getMenu().add(option);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                String selected = item.getTitle().toString();
                if (selected.equals("Others")) {
                    showAddFeederDialog(feederType, feederOptions);
                } else {
                    feederType.setText(selected);
                }
                return true;
            });
            popupMenu.show();
        });
        removeBtn.setOnClickListener(v -> {
            container.removeView(row);
        });

        container.addView(row);
    }

    private void showMaintenanceMenu(Button button) {
        PopupMenu popupMenu = new PopupMenu(getContext(), button);
        // Dynamically populate the menu
        for (String option : maintenanceOptions) {
            popupMenu.getMenu().add(option);
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String selected = item.getTitle().toString();

            if (selected.equals("Others")) {
                showAddCustomMaintenanceDialog(button, maintenanceOptions);
            } else {
                button.setText(selected);
            }

            return true;
        });

        popupMenu.show();
    }

    private void showAddCustomMaintenanceDialog(Button button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Custom Maintenance Type");
        final EditText input = new EditText(getContext());
        input.setHint("Enter custom type");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String customOption = input.getText().toString().trim();
            if (!customOption.isEmpty()) {
                maintenanceOptions.add(maintenanceOptions.size() - 1, customOption);
                button.setText(customOption);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

}