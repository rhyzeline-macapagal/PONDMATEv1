package com.example.pondmatev1;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class pcostroi extends Fragment {

    ArrayList<String> breedList, maintenanceOptions;
    Button editButton, saveButton, generateReportButton, TypeofFeeders, initialMaintenancetype, maintenanceType;
    EditText amtFeeders, maintenance, Capital, Labor, fperpiece, maintenanceCost;
    LinearLayout maintenanceList, feedersContainer, maintenanceContainer;
    TextView totalExpenses, fishbreeddisplay, numfingerlingsdisplay, amtFingerlings;
    ImageButton addMaintenanceButton, addFeederBtn, removeBtn;
    EditText initialMaintenanceCost;

    public abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }


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
        addFeederBtn = view.findViewById(R.id.addToFeedsbtn);

        addFeederBtn.setOnClickListener(v -> {
            addFeederRow(feedersContainer);
            updateTotalExpenses();
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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateFingerlingsAmount();
            }
        });

        amtFeeders.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateTotalExpenses();
            }
        });

        TextWatcher expenseWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
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
            isDataSaved = false;
        });


        // Handle Save button
        saveButton.setOnClickListener(v -> {
            if (validateInputs()) {
                setEditable(false);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                isDataSaved = true; // ✅ Data is now saved
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
        addMaintenanceButton.setOnClickListener(v -> {
            addMaintenanceRow(maintenanceList);
            updateTotalExpenses();
        });


        initialMaintenancetype.setOnClickListener(v -> showMaintenanceMenu(initialMaintenancetype));

        generateReportButton.setOnClickListener(v -> {
            if (!isDataSaved) {
                Toast.makeText(requireContext(), "Please save your changes before generating the report.", Toast.LENGTH_SHORT).show();
                return;
            }
            generatePdfReport();
        });


        return view;
    }

    private void setEditable(boolean editable) {
        // Static EditText fields
        EditText[] editTextFields = {
                amtFeeders, initialMaintenanceCost, Capital, Labor, fperpiece
        };

        for (EditText field : editTextFields) {
            field.setFocusable(editable);
            field.setFocusableInTouchMode(editable);
            field.setClickable(editable);
            field.setCursorVisible(editable);
        }

        // Static Button dropdowns
        Button[] buttonFields = {
                initialMaintenancetype, TypeofFeeders
        };

        for (Button button : buttonFields) {
            button.setEnabled(editable);
            button.setClickable(editable);
        }

        // Dynamically added maintenance rows
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            Button type = row.findViewById(R.id.maintenanceType);

            cost.setFocusable(editable);
            cost.setFocusableInTouchMode(editable);
            cost.setClickable(editable);
            cost.setCursorVisible(editable);

            type.setEnabled(editable);
            type.setClickable(editable);
        }

        // Dynamically added feeder rows
        for (int i = 0; i < feedersContainer.getChildCount(); i++) {
            View row = feedersContainer.getChildAt(i);
            EditText amount = row.findViewById(R.id.amtoffeeders);
            Button type = row.findViewById(R.id.typeoffeeders);

            amount.setFocusable(editable);
            amount.setFocusableInTouchMode(editable);
            amount.setClickable(editable);
            amount.setCursorVisible(editable);

            type.setEnabled(editable);
            type.setClickable(editable);
        }

        // Enable/disable Add buttons if needed
        addFeederBtn.setEnabled(editable);
        addMaintenanceButton.setEnabled(editable);
    }


    private void updateTotalExpenses() {
        double total = 0;

        // Static fields
        total += parseDoubleFromTextView(amtFingerlings);
        total += parseDoubleFromEditText(Capital);
        total += parseDoubleFromEditText(Labor);
        total += parseDoubleFromEditText(initialMaintenanceCost);

        // Dynamic feeder rows
        for (int i = 0; i < feedersContainer.getChildCount(); i++) {
            View row = feedersContainer.getChildAt(i);
            EditText amount = row.findViewById(R.id.amtoffeeders);
            total += parseDoubleSafely(amount);
        }

        // Dynamic maintenance rows
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            total += parseDoubleSafely(cost);
        }

        totalExpenses.setText(String.format("%.2f", total));
    }

    private double parseDoubleSafely(EditText editText) {
        if (editText != null) {
            String text = editText.getText().toString().trim();
            if (!text.isEmpty()) {
                try {
                    return Double.parseDouble(text);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0;
    }

    private double parseDoubleFromEditText(EditText editText) {
        String input = editText.getText().toString().replace("₱", "").trim();
        if (input.isEmpty()) return 0;
        try {
            return Double.parseDouble(input);
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
        if (fishbreeddisplay.getText().toString().trim().isEmpty()) return false;
        if (numfingerlingsdisplay.getText().toString().trim().isEmpty()) return false;
        if (fperpiece.getText().toString().trim().isEmpty()) return false;
        if (amtFingerlings.getText().toString().trim().isEmpty()) return false;
        if (amtFeeders.getText().toString().trim().isEmpty()) return false;
        if (TypeofFeeders.getText().toString().trim().isEmpty()) return false;
        if (initialMaintenancetype.getText().toString().trim().isEmpty()) return false;
        if (initialMaintenanceCost.getText().toString().trim().isEmpty()) return false;
        if (Capital.getText().toString().trim().isEmpty()) return false;
        if (Labor.getText().toString().trim().isEmpty()) return false;

        // Validate all feeder rows
        for (int i = 0; i < feedersContainer.getChildCount(); i++) {
            View row = feedersContainer.getChildAt(i);
            Button type = row.findViewById(R.id.typeoffeeders);
            EditText amount = row.findViewById(R.id.amtoffeeders);
            if (type.getText().toString().trim().isEmpty() || amount.getText().toString().trim().isEmpty()) {
                return false;
            }
        }

        // Validate all maintenance rows
        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            Button type = row.findViewById(R.id.maintenanceType);
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

    private void generatePdfReport() {
        PdfDocument document = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int xLeft = 40;
        int xRight = 500;
        int y = 60;
        int lineSpacing = 25;

        // Title
        paint.setTypeface(Typeface.MONOSPACE);
        paint.setTextSize(14);
        paint.setFakeBoldText(true);
        String title = "PondMate Production Cost Report";
        float titleWidth = paint.measureText(title);
        canvas.drawText(title, (pageInfo.getPageWidth() - titleWidth) / 2, y, paint);

        // Timestamp below title
        paint.setFakeBoldText(false);
        paint.setTextSize(10);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        float timestampWidth = paint.measureText(timestamp);
        y += lineSpacing;
        canvas.drawText(timestamp, (pageInfo.getPageWidth() - timestampWidth) / 2, y, paint);

        paint.setTextSize(12);
        y += lineSpacing;

        // Basic Info
        canvas.drawText(String.format("%-30s", "Breed: ") + fishbreeddisplay.getText(), xLeft, y, paint);
        y += lineSpacing;
        canvas.drawText(String.format("%-30s", "Number of Fingerlings: ") + numfingerlingsdisplay.getText(), xLeft, y, paint);
        y += lineSpacing;
        canvas.drawText("Total Cost of Fingerlings:", xLeft, y, paint);
        canvas.drawText("₱" + amtFingerlings.getText(), xRight, y, paint);
        y += lineSpacing * 2;

        // Feeders
        canvas.drawText("Feeders:", xLeft, y, paint);
        y += lineSpacing;

        for (int i = 0; i < feedersContainer.getChildCount(); i++) {
            View row = feedersContainer.getChildAt(i);
            Button type = row.findViewById(R.id.typeoffeeders);
            EditText amount = row.findViewById(R.id.amtoffeeders);
            canvas.drawText(" - " + type.getText(), xLeft + 20, y, paint);
            canvas.drawText("₱" + amount.getText(), xRight, y, paint);
            y += lineSpacing;
        }

        y += lineSpacing / 2;
        canvas.drawText("------------------------------------------", xLeft, y, paint);
        y += lineSpacing;

        // Maintenance
        canvas.drawText("Maintenance:", xLeft, y, paint);
        y += lineSpacing;
        canvas.drawText(" - " + initialMaintenancetype.getText(), xLeft + 20, y, paint);
        canvas.drawText("₱" + initialMaintenanceCost.getText(), xRight, y, paint);
        y += lineSpacing;

        for (int i = 0; i < maintenanceList.getChildCount(); i++) {
            View row = maintenanceList.getChildAt(i);
            Button type = row.findViewById(R.id.maintenanceType);
            EditText cost = row.findViewById(R.id.maintenanceCost);
            canvas.drawText(" - " + type.getText(), xLeft + 20, y, paint);
            canvas.drawText("₱" + cost.getText(), xRight, y, paint);
            y += lineSpacing;
        }

        y += lineSpacing / 2;
        canvas.drawText("------------------------------------------", xLeft, y, paint);
        y += lineSpacing;

        // Capital & Labor
        canvas.drawText("Capital:", xLeft, y, paint);
        canvas.drawText("₱" + Capital.getText(), xRight, y, paint);
        y += lineSpacing;

        canvas.drawText("Labor:", xLeft, y, paint);
        canvas.drawText("₱" + Labor.getText(), xRight, y, paint);
        y += lineSpacing;

        y += lineSpacing / 2;
        canvas.drawText("------------------------------------------", xLeft, y, paint);
        y += lineSpacing;

        // Total
        paint.setFakeBoldText(true);
        canvas.drawText("TOTAL EXPENSES:", xLeft, y, paint);
        canvas.drawText("₱" + totalExpenses.getText(), xRight, y, paint);
        paint.setFakeBoldText(false);

        document.finishPage(page);

        try {
            String filename = "PondMate_Report.pdf";
            File file = new File(requireContext().getExternalFilesDir(null), filename);
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();

            Toast.makeText(getContext(), "PDF saved to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to generate PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDataSaved = true;

}