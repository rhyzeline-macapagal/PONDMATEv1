package com.example.pondmatev1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class login extends AppCompatActivity {

    EditText userName, passWord;
    Button loginButton;
    TextView signupdirect;
    CheckBox rememberMeCheckBox;
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
        rememberMeCheckBox = findViewById(R.id.checkBox);

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

        loadPreferences();

        loginButton.setOnClickListener(v -> {
            String username = userName.getText().toString().trim();
            String password = passWord.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Both fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL("https://pondmate.alwaysdata.net/login_user.php"); // Replace with your real URL
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String postData = "username=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8");

                    OutputStream os = conn.getOutputStream();
                    os.write(postData.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String response = in.readLine();
                    in.close();

                    runOnUiThread(() -> {
                        switch (response) {
                            case "success":
                                Toast.makeText(login.this, "✅ Login successful", Toast.LENGTH_SHORT).show();
                                SessionManager sessionManager = new SessionManager(login.this);
                                sessionManager.saveUsername(username);

                                if (rememberMeCheckBox.isChecked()) {
                                    savePreferences(username, password);
                                } else {
                                    clearPreferences();
                                }

                                Intent intent = new Intent(login.this, MainActivity.class);
                                intent.putExtra("loggedInUsername", username);
                                startActivity(intent);
                                finish();
                                break;

                            case "invalid":
                                Toast.makeText(login.this, "❌ Invalid username or password", Toast.LENGTH_SHORT).show();
                                break;

                            case "missing":
                                Toast.makeText(login.this, "⚠️ Missing username or password", Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                Toast.makeText(login.this, "❌ Server error", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(login.this, "⚠️ Network error", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Save username and password to SharedPreferences
    private void savePreferences(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void clearPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("username");
        editor.remove("password");
        editor.remove("rememberMe");
        editor.apply();
    }
    // Load username and password from SharedPreferences
    private void loadPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            String savedUsername = sharedPreferences.getString("username", "");
            String savedPassword = sharedPreferences.getString("password", "");
            userName.setText(savedUsername);
            passWord.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);  // Set checkbox to checked
        }
    }


    private void setPasswordEyeIcon(EditText editText, Drawable startDrawable, Drawable endDrawable) {
        editText.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, endDrawable, null);
    }
}
