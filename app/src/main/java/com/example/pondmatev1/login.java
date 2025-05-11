package com.example.pondmatev1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    EditText userName, passWord;
    Button loginButton;
    TextView signupdirect;
    private DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        userName = findViewById(R.id.username_input);
        passWord = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        signupdirect = findViewById(R.id.signuptext);
        signupdirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(v -> {
            String username = userName.getText().toString();
            String password = passWord.getText().toString();

            // Check if any field is empty
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Both fields are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = dbHelper.checkUserCredentials(username, password);
                if (isValid) {
                    // Redirect to home page after successful login
                    Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(login.this, MainActivity.class));
                } else {
                    Toast.makeText(login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}