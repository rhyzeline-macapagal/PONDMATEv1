package com.example.pondmatev1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.CheckBox;

public class signup extends AppCompatActivity {

    EditText fullname;
    EditText username1;
    EditText password1;
    EditText Cpassword;
    Button signupButton;

    CheckBox adminCheckBox;
    CheckBox caretakerCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);


        fullname = findViewById(R.id.fullname_input);
        username1 = findViewById(R.id.username_input1);
        password1 = findViewById(R.id.password_input1);
        Cpassword = findViewById(R.id.password_input2);
        signupButton = findViewById(R.id.signup_button);
        adminCheckBox = findViewById(R.id.admincheckbox);
        caretakerCheckBox = findViewById(R.id.caretakercheckBox);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}