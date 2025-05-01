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

import java.util.ArrayList;

public class pcostroi extends Fragment {

    ArrayList<String> breedList;
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    Button addOptionButton, editButton, saveButton;
    EditText amtFingerlings, amtFeeders, maintenance, otherExpenses, totalExpenses;

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
        maintenance = view.findViewById(R.id.maintenancecost);
        otherExpenses = view.findViewById(R.id.otherexpenses);
        totalExpenses = view.findViewById(R.id.totalexpenses);
        editButton = view.findViewById(R.id.editbtn);
        saveButton = view.findViewById(R.id.savebtn);

        autoCompleteText = view.findViewById(R.id.auto_complete_txt1);
        addOptionButton = view.findViewById(R.id.addOptionButton);

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

            // Optional: Logic to save changes to database or shared preferences
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

        return view;
    }

    private void setEditable(boolean editable) {
        EditText[] fields = {amtFingerlings, amtFeeders, maintenance, otherExpenses, totalExpenses};

        for (EditText field : fields) {
            field.setFocusable(editable);
            field.setFocusableInTouchMode(editable);
            field.setClickable(editable);
            field.setCursorVisible(editable);
        }

        autoCompleteText.setEnabled(editable);
        addOptionButton.setEnabled(editable);
    }
}
