package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

public class fraghome extends Fragment {
    private SharedPreferences sharedPreferences;
    private SharedViewModel sharedViewModel;
    private View SelectedPond = null;
    private final boolean[] labelUsed = new boolean[5]; // for pond A to E

    private static final String PREF_NAME = "ActivityPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fraghome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        //pond card view
        LinearLayout pondContainer = view.findViewById(R.id.pondContainer);
        int[] pondImages = {
                R.drawable.pond, R.drawable.pond, R.drawable.pond, R.drawable.pond, R.drawable.pond
        };
        char[] labels = {'A', 'B', 'C', 'D', 'E'};

        View plusCard = LayoutInflater.from(requireContext()).inflate(R.layout.pond_card, null);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(1, 0, 1, 0);
        plusCard.setLayoutParams(cardParams);


        ImageView plusImage = plusCard.findViewById(R.id.pondImage);
        TextView plusLabel = plusCard.findViewById(R.id.pondLabel);

        plusImage.setImageResource(R.drawable.pond);
        plusLabel.setText("+");
        pondContainer.addView(plusCard);

        plusCard.setOnClickListener(v -> {
            int nextLabelIndex = IntStream.range(0, labelUsed.length).filter(i -> !labelUsed[i]).findFirst().orElse(-1);

            if (nextLabelIndex != -1) {
                char label = labels[nextLabelIndex];

                new AlertDialog.Builder(requireContext())
                        .setTitle("Create New Pond")
                        .setMessage("Do you want to create Pond " + label + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            View newCard = LayoutInflater.from(requireContext()).inflate(R.layout.pond_card, null);
                            LinearLayout.LayoutParams newParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            newParams.setMargins(16, 0, 16, 0);
                            newCard.setLayoutParams(newParams);

                            ImageView pondImage = newCard.findViewById(R.id.pondImage);
                            TextView pondLabel = newCard.findViewById(R.id.pondLabel);
                            View selectorBorder = newCard.findViewById(R.id.selectorBorder);

                            pondImage.setImageResource(pondImages[nextLabelIndex]);
                            pondLabel.setText(String.valueOf(label));
                            pondContainer.addView(newCard, pondContainer.getChildCount() - 1);

                            labelUsed[nextLabelIndex] = true;

                            if (SelectedPond == null) {
                                selectorBorder.setVisibility(View.VISIBLE);
                                SelectedPond = newCard;
                            }

                            ((MainActivity) requireActivity()).enableNavigationItems();
                            showBreedingSection();

                            // Selection logic
                            newCard.setOnClickListener(clickedView -> {
                                if (SelectedPond != null) {
                                    View lastBorder = SelectedPond.findViewById(R.id.selectorBorder);
                                    if (lastBorder != null) lastBorder.setVisibility(View.GONE);
                                }

                                View currentBorder = clickedView.findViewById(R.id.selectorBorder);
                                if (currentBorder != null) currentBorder.setVisibility(View.VISIBLE);
                                SelectedPond = clickedView;
                            });

                            // Deletion logic
                            pondImage.setOnLongClickListener(v1 -> {
                                View cardToDelete = (View) v1.getParent().getParent();
                                TextView labelView = cardToDelete.findViewById(R.id.pondLabel);
                                String labelText = labelView != null ? labelView.getText().toString() : "?";

                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Delete Pond")
                                        .setMessage("Are you sure you want to delete Pond " + labelText + "?")
                                        .setPositiveButton("Yes", (dialog1, which1) -> {
                                            pondContainer.removeView(cardToDelete);

                                            // Mark label as unused
                                            int indexToFree = labelText.charAt(0) - 'A';
                                            if (indexToFree >= 0 && indexToFree < labelUsed.length) {
                                                labelUsed[indexToFree] = false;
                                            }

                                            // Deselect if deleted pond was selected
                                            if (SelectedPond == cardToDelete) {
                                                SelectedPond = null;

                                                for (int i = 0; i < pondContainer.getChildCount(); i++) {
                                                    View child = pondContainer.getChildAt(i);
                                                    TextView lbl = child.findViewById(R.id.pondLabel);
                                                    if (lbl != null && !lbl.getText().toString().equals("+")) {
                                                        View border = child.findViewById(R.id.selectorBorder);
                                                        if (border != null) border.setVisibility(View.VISIBLE);
                                                        SelectedPond = child;
                                                        break;
                                                    }
                                                }
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();

                                return true;
                            });

                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                Toast.makeText(requireContext(), "Maximum ponds added", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void showBreedingSection() {
        View root = getView();
        if (root == null) return;

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        BreedDataManager dataManager = new BreedDataManager(requireContext());

        Calendar calendar = Calendar.getInstance();
        boolean[] isEditing = {false}; // simulate boolean within inner methods

        LinearLayout breedingSection = root.findViewById(R.id.breedingSection);
        breedingSection.setVisibility(View.VISIBLE);

        EditText numFingerlings = root.findViewById(R.id.numoffingerlings);
        TextView SOA = root.findViewById(R.id.txt_soa);
        TextView MortResult = root.findViewById(R.id.txt_mortality_rate);
        TextView harvestDateView = root.findViewById(R.id.harvestdate);
        TextView NoDFish = root.findViewById(R.id.txt_est_dead);
        Button btnSelectDate = root.findViewById(R.id.btn_select_date);
        Button btnSelectBreed = root.findViewById(R.id.btn_select_breed);
        Button btnEditFbreed = root.findViewById(R.id.btn_edit_fbreed);

        // Setup ViewModel initial data
        Integer currentValue = viewModel.getNumOfFingerlings().getValue();
        if (currentValue != null) numFingerlings.setText(String.valueOf(currentValue));

        numFingerlings.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int value = Integer.parseInt(s.toString());
                    viewModel.setNumofFingerlings(value);
                    int estDead = (int) Math.floor(value * 0.10);
                    NoDFish.setText(String.valueOf(estDead));
                } catch (NumberFormatException e) {
                    viewModel.setNumofFingerlings(null);
                    NoDFish.setText("—");
                }
                updateMortalityRate(numFingerlings, NoDFish, MortResult);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnSelectBreed.setOnClickListener(v -> {
            if (isEditing[0]) {
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
            if (isEditing[0]) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, y, m, d) -> {
                    calendar.set(y, m, d);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                    SOA.setText(sdf.format(calendar.getTime()));
                    viewModel.setSOADate(calendar.getTime());

                    Calendar harvestCalendar = (Calendar) calendar.clone();
                    harvestCalendar.add(Calendar.MONTH, 4);
                    harvestDateView.setText(sdf.format(harvestCalendar.getTime()));
                }, year, month, day);

                datePickerDialog.show();
            } else {
                Toast.makeText(getContext(), "Click Edit to enable date selection", Toast.LENGTH_SHORT).show();
            }
        });

        btnEditFbreed.setOnClickListener(v -> {
            if (!isEditing[0]) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Confirm Edit")
                        .setMessage("Are you sure you want to edit the Fish Breed?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            isEditing[0] = true;
                            setFishBreedEditable(true, btnSelectBreed, btnSelectDate, SOA, numFingerlings);
                            btnEditFbreed.setText("Save");
                            Toast.makeText(getContext(), "Editing Fish Breed enabled", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                String selectedDate = SOA.getText().toString().trim();
                String breed = btnSelectBreed.getText().toString().trim();
                String fingerlings = numFingerlings.getText().toString().trim();
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
                            isEditing[0] = false;
                            setFishBreedEditable(false, btnSelectBreed, btnSelectDate, SOA, numFingerlings);
                            btnEditFbreed.setText("Edit");

                            SharedPreferences prefs = requireContext().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
                            prefs.edit().putFloat("numFingerlings", Float.parseFloat(fingerlings)).apply();

                            Toast.makeText(getContext(), "Fish Breed saved", Toast.LENGTH_SHORT).show();
                            dataManager.saveBreedData(breed, selectedDate, harvestDate, fingerlings,
                                    NoDFish.getText().toString().trim(), MortResult.getText().toString().trim());
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        // Restore saved data
        btnSelectBreed.setText(dataManager.getBreed());
        SOA.setText(dataManager.getSOADate());
        harvestDateView.setText(dataManager.getHarvestDate());
        numFingerlings.setText(dataManager.getFingerlings());
        NoDFish.setText(dataManager.getEstDead());
        MortResult.setText(dataManager.getMortalityRate());

        try {
            viewModel.setNumofFingerlings(Integer.parseInt(dataManager.getFingerlings()));
        } catch (NumberFormatException e) {
            viewModel.setNumofFingerlings(null);
        }
    }

    private void setFishBreedEditable(boolean enabled, Button breedBtn, Button dateBtn, TextView soa, EditText num) {
        breedBtn.setEnabled(enabled);
        dateBtn.setEnabled(enabled);
        soa.setEnabled(enabled);
        num.setEnabled(enabled);

        if (enabled && "—".equals(num.getText().toString().trim())) {
            num.setText("");
        }
    }

    private void updateMortalityRate(EditText stockField, TextView estDeadView, TextView mortView) {
        String stockStr = stockField.getText().toString().trim();
        String deadStr = estDeadView.getText().toString().trim();

        if (!stockStr.isEmpty() && !deadStr.isEmpty()) {
            try {
                double stock = Double.parseDouble(stockStr);
                double dead = Double.parseDouble(deadStr);
                double result = (dead / stock) * 100;
                mortView.setText(String.format(Locale.US, "%.2f%%", result));
            } catch (NumberFormatException e) {
                mortView.setText("—");
            }
        } else {
            mortView.setText("—");
        }
    }


}
