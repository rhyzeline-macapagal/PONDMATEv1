package com.example.pondmatev1;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class signup extends AppCompatActivity {

    EditText fullname, username1, password1, Cpassword, address;

    private RadioGroup rgUserType;
    private DatabaseHelper dbHelper;

    Button signupButton;
    TextView logindirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // Forces DB creation
        db.close();

        // Initialize EditText fields
        fullname = findViewById(R.id.fullname_input);
        username1 = findViewById(R.id.username_input1);
        password1 = findViewById(R.id.password_input1);
        Cpassword = findViewById(R.id.password_input2);
        address = findViewById(R.id.address_input);
        rgUserType = findViewById(R.id.rgUserType);
        signupButton = findViewById(R.id.signup_button);
        logindirect = findViewById(R.id.loginalready);


        password1.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Cpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        setPasswordEyeIcon(password1, R.drawable.hidden);
        setPasswordEyeIcon(Cpassword, R.drawable.hidden);

        signupButton.setOnClickListener(v -> {
            String username = username1.getText().toString().trim();
            String password = password1.getText().toString().trim();
            String confirmPassword = Cpassword.getText().toString().trim(); // 🔁 GET CONFIRM PASSWORD
            String fname = fullname.getText().toString().trim();
            String address1 = address.getText().toString().trim();

            // Check if any field is empty
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    fname.isEmpty() || address1.isEmpty()) {
                Toast.makeText(signup.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Confirm passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get selected user type
            int selectedId = rgUserType.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(signup.this, "Please select a user type", Toast.LENGTH_SHORT).show();
                return;
            }

            String userType = (selectedId == R.id.rbOwner) ? "Owner" : "Caretaker";

            // Insert data into database
            boolean isInserted = dbHelper.addUser(username, password, fname, address1, userType);
            if (isInserted) {
                Toast.makeText(signup.this, "Registration successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        });

        // pag set ng pass visib sa et click 1
        password1.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password1.getRight() - password1.getCompoundPaddingRight())) {
                    togglePasswordVisibility(password1);
                    return true;
                }
            }
            return false;
        });

        // pag set ng confirm pass visib sa et click
        Cpassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (Cpassword.getRight() - Cpassword.getCompoundPaddingRight())) {
                    togglePasswordVisibility(Cpassword);
                    return true;
                }
            }
            return false;
        });


        logindirect.setOnClickListener(v -> {
            Intent intent = new Intent(signup.this, login.class);
            startActivity(intent);
        });
    }

    // Toggle password visibility and update eye icon
    public void togglePasswordVisibility(EditText editText) {
        if (editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            // Change to plain text
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            setPasswordEyeIcon(editText, R.drawable.eye_open);  // Set open eye icon
        } else {
            // Change to password text
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            setPasswordEyeIcon(editText, R.drawable.hidden);  // Set closed eye icon
        }
    }



    // Helper method to set drawable icons for EditText
    private void setPasswordEyeIcon(EditText editText, int drawableRes) {
        editText.setCompoundDrawablesWithIntrinsicBounds(
                null, null, getResources().getDrawable(drawableRes), null
        );
    }

}
