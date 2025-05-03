package com.example.pondmatev1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        ImageButton backButton = findViewById(R.id.backbtn_profile);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: closes the current activity
        });
    }
}