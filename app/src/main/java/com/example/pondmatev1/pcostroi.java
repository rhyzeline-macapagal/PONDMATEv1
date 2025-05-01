package com.example.pondmatev1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;


import java.util.ArrayList;

public class pcostroi extends Fragment {

    ArrayList<String> breedList;
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    Button addOptionButton, editButton, saveButton;
    EditText amtFingerlings, amtFeeders, maintenance, otherExpenses, totalExpenses;
    LinearLayout maintenanceList, oexpensesList;
    ImageButton addMaintenanceButton, addOtherExpensesButton;
    EditText initialMaintenanceCost, initialOtherExpenseCost;


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
        View view = inflater.inflate(R.layout.fragment_pcostroi, container, false);

        // Initialize views
        amtFingerlings = view.findViewById(R.id.amtoffingerlings);
        amtFeeders = view.findViewById(R.id.amtoffeeders);
        initialMaintenanceCost = view.findViewById(R.id.initialMaintenanceCost);
        initialOtherExpenseCost = view.findViewById(R.id.initialOtherExpenseCost);
        totalExpenses = view.findViewById(R.id.totalexpenses);
        editButton = view.findViewById(R.id.editbtn);
        saveButton = view.findViewById(R.id.savebtn);
        maintenanceList = view.findViewById(R.id.maintenanceList);
        oexpensesList = view.findViewById(R.id.otherExpensesList);

        addOptionButton = view.findViewById(R.id.addOptionButton);
        autoCompleteText = view.findViewById(R.id.auto_complete_txt1);
        addMaintenanceButton = view.findViewById(R.id.addMaintenanceButton);
        addOtherExpensesButton = view.findViewById(R.id.addOtherExpensesButton);

        // Set up dropdown adapter
        adapterItems = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, breedList);
        autoCompleteText.setAdapter(adapterItems);

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
            setEditable(false);
            saveButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
        });

        // Add new breed option
        addOptionButton.setOnClickListener(v -> {
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

        // Add new maintenance row
        addMaintenanceButton.setOnClickListener(v -> addNewMaintenanceRow());

        // Add new other expenses row
        addOtherExpensesButton.setOnClickListener(v -> addNewOtherExpensesRow());

        return view;
    }

    private void setEditable(boolean editable) {
        EditText[] fields = {amtFingerlings, amtFeeders,  initialMaintenanceCost, initialOtherExpenseCost, totalExpenses};

        for (EditText field : fields) {
            field.setFocusable(editable);
            field.setFocusableInTouchMode(editable);
            field.setClickable(editable);
            field.setCursorVisible(editable);

            if (editable) {
                field.requestFocus(); // Manually request focus when making editable
            }
        }
        initialMaintenanceCost.setFocusable(editable);
        initialMaintenanceCost.setFocusableInTouchMode(editable);
        initialMaintenanceCost.setClickable(editable);
        initialMaintenanceCost.setCursorVisible(editable);

        initialOtherExpenseCost.setFocusable(editable);
        initialOtherExpenseCost.setFocusableInTouchMode(editable);
        initialOtherExpenseCost.setClickable(editable);
        initialOtherExpenseCost.setCursorVisible(editable);

        autoCompleteText.setEnabled(editable);
        addOptionButton.setEnabled(editable);
    }

    // Method to add new maintenance row dynamically
    private void addNewMaintenanceRow() {
        View maintenanceView = LayoutInflater.from(requireContext()).inflate(R.layout.row_maintenance, null);

        EditText maintenanceType = maintenanceView.findViewById(R.id.maintenanceType);
        EditText maintenanceCost = maintenanceView.findViewById(R.id.maintenanceCost);

        maintenanceList.addView(maintenanceView);
    }

    // Method to add new other expenses row dynamically
    private void addNewOtherExpensesRow() {
        View expensesView = LayoutInflater.from(requireContext()).inflate(R.layout.row_other_expenses, null);

        EditText expenseType = expensesView.findViewById(R.id.otherexpenses);
        EditText expenseCost = expensesView.findViewById(R.id.otherexpensesCost);

        oexpensesList.addView(expensesView);
    }
}
