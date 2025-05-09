package com.example.pondmatev1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.text.Editable;
import android.text.TextWatcher;


import androidx.appcompat.app.AppCompatActivity;

public class profile extends AppCompatActivity {
    private EditText pondName, location, name;
    private Button editBtn, saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Button logoutBtn = findViewById(R.id.logout_button);
        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, login.class);
            startActivity(intent);
            finish();
        });

        // Back Button
        ImageButton backButton = findViewById(R.id.backbtn_profile);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // EditText fields
        pondName = findViewById(R.id.textView_pondname2);
        location = findViewById(R.id.textView_location2);
        name = findViewById(R.id.textView_name2);

        // Buttons
        editBtn = findViewById(R.id.userprofile_edit);
        saveBtn = findViewById(R.id.userprofile_save);

        // Initially disable EditTexts and Save button
        setEditable(false);
        saveBtn.setVisibility(View.GONE);
        saveBtn.setEnabled(false); // disable until all fields are filled

        // Add text watchers to monitor changes
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        pondName.addTextChangedListener(watcher);
        location.addTextChangedListener(watcher);
        name.addTextChangedListener(watcher);


        // Edit button logic
        editBtn.setOnClickListener(v -> {
            setEditable(true);
            saveBtn.setVisibility(View.VISIBLE);
            checkFields();
        });

        // Save button logic
        saveBtn.setOnClickListener(v -> {
            // Optionally handle saving logic here
            setEditable(false);
            saveBtn.setVisibility(View.GONE);
        });
    }

    private void setEditable(boolean enabled) {
        pondName.setEnabled(enabled);
        location.setEnabled(enabled);
        name.setEnabled(enabled);
    }

    private void checkFields() {
        boolean allFilled = !pondName.getText().toString().trim().isEmpty()
                && !location.getText().toString().trim().isEmpty()
                && !name.getText().toString().trim().isEmpty();
        saveBtn.setEnabled(allFilled);
    }
}
