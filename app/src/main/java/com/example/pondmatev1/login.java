package com.example.pondmatev1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class login extends AppCompatActivity {

    EditText userName, passWord;
    Button loginButton;
    TextView signupdirect;
    private DatabaseHelper dbHelper;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        userName = findViewById(R.id.username_input);
        passWord = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        final Drawable eyeOpen = ContextCompat.getDrawable(this, R.drawable.eye_open);
        final Drawable eyeClosed = ContextCompat.getDrawable(this, R.drawable.hidden);
        final Drawable lockIcon = ContextCompat.getDrawable(this, R.drawable.lock_icon);
        final boolean[] isVisible = {false};

        // Set initial state: hidden password
        setPasswordEyeIcon(passWord, lockIcon, eyeClosed);

        passWord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = passWord.getCompoundDrawables()[2]; // right drawable
                if (drawableEnd != null) {
                    int drawableWidth = drawableEnd.getBounds().width();
                    int clickAreaStart = passWord.getWidth() - passWord.getPaddingEnd() - drawableWidth;
                    if (event.getX() >= clickAreaStart) {
                        isVisible[0] = !isVisible[0];
                        int cursorPosition = passWord.getSelectionStart();

                        if (isVisible[0]) {
                            passWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            setPasswordEyeIcon(passWord, lockIcon, eyeOpen);
                        } else {
                            passWord.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            setPasswordEyeIcon(passWord, lockIcon, eyeClosed);
                        }

                        passWord.setSelection(cursorPosition);
                        return true;
                    }
                }
            }
            return false;
        });

        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Drawable currentIcon = isVisible[0] ? eyeOpen : eyeClosed;
                setPasswordEyeIcon(passWord, lockIcon, currentIcon);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        signupdirect = findViewById(R.id.signuptext);
        signupdirect.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, signup.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String username = userName.getText().toString();
            String password = passWord.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Both fields are required", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = dbHelper.checkUserCredentials(username, password);
                if (isValid) {
                    Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login.this, MainActivity.class);
                    intent.putExtra("loggedInUsername", username);
                    startActivity(intent);
                    SessionManager sessionManager = new SessionManager(login.this);
                    sessionManager.saveUsername(username);
                    finish();
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

    
    private void setPasswordEyeIcon(EditText editText, Drawable startDrawable, Drawable endDrawable) {
        editText.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);
    }
}
