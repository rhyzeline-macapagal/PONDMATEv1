package com.example.pondmatev1;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.icu.text.DisplayOptions;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class pcostroi extends Fragment {

    ArrayList<String> breedList;
    //ArrayAdapter<String> adapterItems;
        Button editButton, saveButton, generateReportButton;
    EditText amtFingerlings, amtFeeders, maintenance, initialMaintenancetype, TypeofFeeders, Capital, Labor;
    LinearLayout maintenanceList;
    TextView totalExpenses, fishbreeddisplay, numfingerlingsdisplay;
    ImageButton addMaintenanceButton;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pcostroi, container, false);

        // Initialize views
        amtFingerlings = view.findViewById(R.id.amtoffingerlings);
        amtFeeders = view.findViewById(R.id.amtoffeeders);
        initialMaintenancetype = view.findViewById(R.id.initialMaintenanceType);
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


        setupExpenseAutoSum(amtFingerlings, amtFeeders, initialMaintenanceCost);


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

        // Set up dropdown adapter
        //adapterItems = new ArrayAdapter<>(requireContext(),
        //        android.R.layout.simple_dropdown_item_1line, breedList);
        //autoCompleteText.setAdapter(adapterItems);

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


        //Handle Add opt
        //addOptionButton.setOnClickListener(v -> {
        //    String newItem = autoCompleteText.getText().toString().trim();

        //    if (!newItem.isEmpty() && !breedList.contains(newItem)) {
        //        breedList.add(newItem);
        //       adapterItems = new ArrayAdapter<>(requireContext(),
        //                android.R.layout.simple_dropdown_item_1line, breedList);
        //        autoCompleteText.setAdapter(adapterItems);
        //        autoCompleteText.setText("");
        //        autoCompleteText.showDropDown();
        //        Toast.makeText(requireContext(), "Added: " + newItem, Toast.LENGTH_SHORT).show();
        //    } else {
        //        Toast.makeText(requireContext(), "Item is empty or already exists", Toast.LENGTH_SHORT).show();
        //    }
        //});

        // Add new maintenance row
        addMaintenanceButton.setOnClickListener(v -> addNewMaintenanceRow());

        //generate report button
        generateReportButton.setOnClickListener(v -> generateReport());


        return view;
    }
    // generate report
    private void generateReport() {
        //String breed = autoCompleteText.getText().toString().trim();
        String fingerlings = amtFingerlings.getText().toString().trim();
        String feeders = amtFeeders.getText().toString().trim();
        String maintenancetype = initialMaintenancetype.getText().toString().trim();
        String maintenance = initialMaintenanceCost.getText().toString().trim();

        String total = totalExpenses.getText().toString().trim();

        StringBuilder maintenanceDetails = new StringBuilder();
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText type = row.findViewById(R.id.maintenanceType);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            maintenanceDetails.append("   • ").append(type.getText().toString())
                    .append(": ₱").append(cost.getText().toString()).append("\n");
        }

        try {
            PdfDocument pdfDocument = new PdfDocument();
            Paint paint = new Paint();
            paint.setTextSize(14);

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            int x = 40;
            int y = 60;

            // Title
            Paint titlePaint = new Paint();
            titlePaint.setTextSize(18);
            titlePaint.setFakeBoldText(true);
            String title = "*** Production Cost Summary ***";
            float titleWidth = titlePaint.measureText(title);
            canvas.drawText(title, (pageInfo.getPageWidth() - titleWidth) / 2, y, titlePaint);

            y += 40;

            //canvas.drawText("Breed: " + breed, x, y, paint); y += 30;
            canvas.drawText("➤ Fingerlings:                                           ₱" + fingerlings, x, y, paint); y += 25;
            canvas.drawText("➤ Feeders:                                               ₱" + feeders, x, y, paint); y += 25;
            canvas.drawText("➤ Initial Maintenance: " +"\n"
                    + maintenancetype + " -         ₱" + maintenance, x, y, paint); y += 25;

            canvas.drawText("— Maintenance Breakdown —", x, y, paint); y += 25;
            if (maintenanceDetails.length() > 0) {
                for (String line : maintenanceDetails.toString().split("\n")) {
                    canvas.drawText(line, x + 20, y, paint);
                    y += 22;
                }
            } else {
                canvas.drawText("   No additional maintenance expenses.", x + 20, y, paint);
                y += 22;
            }

            y += 30;
            Paint totalPaint = new Paint(paint);
            totalPaint.setFakeBoldText(true);
            canvas.drawText("============================", x, y, paint); y += 25;
            canvas.drawText("TOTAL EXPENSES: ₱" + total, x, y, totalPaint);

            pdfDocument.finishPage(page);

            File file = new File(requireContext().getExternalFilesDir(null), "ProductionCostReport.pdf");
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();

            Toast.makeText(requireContext(), "PDF saved to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setEditable(boolean editable) {
        EditText[] fields = {amtFingerlings, amtFeeders,  initialMaintenanceCost};

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

        //autoCompleteText.setEnabled(editable);
        //addOptionButton.setEnabled(editable);
        //autoCompleteText.setFocusable(editable);
        //autoCompleteText.setFocusableInTouchMode(editable);
        //autoCompleteText.setClickable(editable);
        //autoCompleteText.setCursorVisible(editable);
    }

    // Method to add new maintenance row dynamically
    private void addNewMaintenanceRow() {
        View maintenanceView = LayoutInflater.from(requireContext()).inflate(R.layout.row_maintenance, null);

        ImageButton removeButton = maintenanceView.findViewById(R.id.removeMaintenanceButton);
        EditText maintenanceType = maintenanceView.findViewById(R.id.maintenanceType);
        EditText maintenanceCost = maintenanceView.findViewById(R.id.maintenanceCost);

        maintenanceCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalExpenses(amtFingerlings, amtFeeders, initialMaintenanceCost);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        removeButton.setOnClickListener(v -> {
            maintenanceList.removeView(maintenanceView);
            updateTotalExpenses(amtFingerlings, amtFeeders, initialMaintenanceCost);
        });

        maintenanceList.addView(maintenanceView);
    }


    private void setupExpenseAutoSum(EditText... staticFields) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalExpenses(staticFields);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        for (EditText field : staticFields) {
            field.addTextChangedListener(watcher);
        }
    }

    private void updateTotalExpenses(EditText... staticFields) {
        double total = 0;

        // Sum static fields
        for (EditText field : staticFields) {
            String value = field.getText().toString().trim();
            if (!value.isEmpty()) {
                try {
                    total += Double.parseDouble(value);
                } catch (NumberFormatException ignored) {}
            }
        }

        // Sum dynamic maintenance costs
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            if (cost != null && !cost.getText().toString().trim().isEmpty()) {
                try {
                    total += Double.parseDouble(cost.getText().toString().trim());
                } catch (NumberFormatException ignored) {}
            }
        }

        totalExpenses.setText(String.format(Locale.US, "%.2f", total));
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



}
