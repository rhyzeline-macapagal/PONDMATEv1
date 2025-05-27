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
    //ArrayAdapter<String> adapterItems;
    Button editButton, saveButton, generateReportButton,TypeofFeeders, initialMaintenancetype;
    EditText amtFeeders, maintenance, Capital, Labor, fperpiece;
    LinearLayout maintenanceList;
    TextView totalExpenses, fishbreeddisplay, numfingerlingsdisplay, amtFingerlings;
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
        fperpiece = view.findViewById(R.id.amtperpiece);
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

        LinearLayout feedersContainer = view.findViewById(R.id.feeders_container);
        ImageButton addBtn = view.findViewById(R.id.addToFeedsbtn);

        addBtn.setOnClickListener(v -> {
            addFeederRow(feedersContainer);
        });

        setupExpenseAutoSum(amtFeeders, initialMaintenanceCost);

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
            calculateAmtFingerlings();
        });

        fperpiece.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateAmtFingerlings();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

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

        Button TypeofFeeders = view.findViewById(R.id.typeoffeeders);
        List<String> feederOptions = new ArrayList<>(Arrays.asList("Starter", "Grower", "Finisher", "Others"));

        TypeofFeeders.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), TypeofFeeders);

            for (String option : feederOptions) {
                popupMenu.getMenu().add(option);
            }

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

        LinearLayout maintenanceContainer = view.findViewById(R.id.maintenance_container);
        ImageButton addMaintenanceButton = view.findViewById(R.id.addMaintenanceButton);

        addMaintenanceButton.setOnClickListener(v -> addMaintenanceRow(maintenanceContainer));

        //generate report button
        generateReportButton.setOnClickListener(v -> generateReport());

        Button initialMaintenanceType = view.findViewById(R.id.initialMaintenanceType);

        initialMaintenanceType.setOnClickListener(v -> showMaintenanceMenu(initialMaintenanceType));


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
        EditText[] fields = {amtFeeders,  initialMaintenanceCost};

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
    }

    // Method to add new maintenance row dynamically
    private void addMaintenanceRow(LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View maintenanceRow = inflater.inflate(R.layout.row_maintenance, container, false);

        Button maintenanceType = maintenanceRow.findViewById(R.id.maintenanceType);
        EditText maintenanceCost = maintenanceRow.findViewById(R.id.maintenanceCost);
        ImageButton removeBtn = maintenanceRow.findViewById(R.id.removeMaintenanceButton);

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

    private void calculateAmtFingerlings() {
        String perPieceStr = fperpiece.getText().toString();
        String numFingerlingsStr = numfingerlingsdisplay.getText().toString();

        try {
            double perPiece = perPieceStr.isEmpty() ? 0 : Double.parseDouble(perPieceStr);
            double numFingerlings = numFingerlingsStr.isEmpty() ? 0 : Double.parseDouble(numFingerlingsStr);
            double total = perPiece * numFingerlings;
            amtFingerlings.setText(String.format(Locale.US, "₱%.2f", total));
        } catch (NumberFormatException e) {
            amtFingerlings.setText("₱0.00");
        }
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
                // Add new item above "Others"
                maintenanceOptions.add(maintenanceOptions.size() - 1, customOption);
                button.setText(customOption);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}