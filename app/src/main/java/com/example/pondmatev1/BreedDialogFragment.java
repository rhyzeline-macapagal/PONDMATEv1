package com.example.pondmatev1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class BreedDialogFragment extends DialogFragment {

    public interface OnBreedSelectedListener {
        void onBreedSelected(String breedName);
    }

    private OnBreedSelectedListener listener;

    public void setOnBreedSelectedListener(OnBreedSelectedListener listener) {
        this.listener = listener;
    }

    private final String[] breedNames = {
            "Tilapia", "Milkfish", "Shrimp", "Seaweed", "Carp", "Oyster", "Mussel", "Other"
    };

    private final int[] breedImages = {
            R.drawable.tilapia, R.drawable.tilapia, R.drawable.tilapia,
            R.drawable.tilapia, R.drawable.tilapia, R.drawable.tilapia,
            R.drawable.tilapia, 0
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Fish Breed");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.select_dialog_item, breedNames) {
            @Nullable
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(breedNames[position]);
                textView.setCompoundDrawablePadding(32);

                if (position < breedImages.length && breedImages[position] != 0) {
                    Drawable img = ContextCompat.getDrawable(requireContext(), breedImages[position]);
                    if (img != null) {
                        img.setBounds(0, 0, 80, 80);
                        textView.setCompoundDrawables(img, null, null, null);
                    }
                } else {
                    textView.setCompoundDrawables(null, null, null, null);
                }
                return view;
            }
        };

        builder.setAdapter(adapter, (dialog, which) -> {
            if (breedNames[which].equals("Other")) {
                showCustomBreedInput();
            } else {
                if (listener != null) listener.onBreedSelected(breedNames[which]);
            }
        });

        return builder.create();
    }

    private void showCustomBreedInput() {
        AlertDialog.Builder inputDialog = new AlertDialog.Builder(requireContext());
        inputDialog.setTitle("Enter Custom Breed");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter breed name");
        inputDialog.setView(input);

        inputDialog.setPositiveButton("OK", (dialogInterface, i) -> {
            String customBreed = input.getText().toString().trim();
            if (!customBreed.isEmpty() && listener != null) {
                listener.onBreedSelected(customBreed);
            }
        });

        inputDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        inputDialog.show();
    }
}
