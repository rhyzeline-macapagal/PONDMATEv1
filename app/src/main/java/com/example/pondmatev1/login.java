package com.example.pondmatev1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
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

        userName = findViewById(R.id.username_input);
        passWord = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        rememberMeCheckBox = findViewById(R.id.checkBox);
        signupdirect = findViewById(R.id.signuptext);

        // Password eye toggle
        final Drawable eyeOpen = ContextCompat.getDrawable(this, R.drawable.eye_open);
        final Drawable eyeClosed = ContextCompat.getDrawable(this, R.drawable.hidden);
        final Drawable lockIcon = ContextCompat.getDrawable(this, R.drawable.lock_icon);
        final boolean[] isVisible = {false};
        setPasswordEyeIcon(passWord, lockIcon, eyeClosed);

        passWord.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable drawableEnd = passWord.getCompoundDrawables()[2];
                if (drawableEnd != null) {
                    int drawableWidth = drawableEnd.getBounds().width();
                    int clickAreaStart = passWord.getWidth() - passWord.getPaddingEnd() - drawableWidth;
                    if (event.getX() >= clickAreaStart) {
                        isVisible[0] = !isVisible[0];
                        int cursorPosition = passWord.getSelectionStart();
                        passWord.setInputType(isVisible[0] ?
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        setPasswordEyeIcon(passWord, lockIcon, isVisible[0] ? eyeOpen : eyeClosed);
                        passWord.setSelection(cursorPosition);
                        return true;
                    }
                }
            }
            return false;
        });

        passWord.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPasswordEyeIcon(passWord, lockIcon, isVisible[0] ? eyeOpen : eyeClosed);
            }
            public void afterTextChanged(Editable s) {}
        });

        // Sync all users from server to local SQLite (if online)
        if (isInternetAvailable()) syncUsersFromServer();

        loadPreferences();

        loginButton.setOnClickListener(v -> {
            String username = userName.getText().toString().trim();
            String password = passWord.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isInternetAvailable()) {
                loginOnline(username, password);
            } else {
                loginOffline(username, password);
            }
        });

        signupdirect.setOnClickListener(v -> startActivity(new Intent(this, signup.class)));
    }

    private void loginOnline(String username, String password) {
        new Thread(() -> {
            try {
                URL url = new URL("https://pondmate.alwaysdata.net/login_user.php");
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
                    if ("success".equals(response)) {
                        SessionManager session = new SessionManager(this);
                        session.saveUsername(username);
                        if (rememberMeCheckBox.isChecked()) savePreferences(username, password);
                        else clearPreferences();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else if ("invalid".equals(response)) {
                        Toast.makeText(this, "❌ Invalid credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "⚠️ Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "⚠️ Network error", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void loginOffline(String username, String password) {
        boolean valid = dbHelper.checkUserCredentials(username, password);
        if (valid) {
            SessionManager session = new SessionManager(this);
            session.saveUsername(username);
            Toast.makeText(this, "✅ Offline login successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "❌ Invalid credentials (offline)", Toast.LENGTH_SHORT).show();
        }
    }

    private void syncUsersFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL("https://pondmate.alwaysdata.net/get_users.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                reader.close();

                JSONArray usersArray = new JSONArray(jsonBuilder.toString());

                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);
                    String uname = user.getString("username");
                    String pass = user.getString("password");
                    String fname = user.getString("fullname");
                    String address = user.getString("address");
                    String type = user.getString("usertype");

                    if (!dbHelper.checkUserCredentials(uname, pass)) {
                        dbHelper.addUser(uname, pass, fname, address, type);
                    }
                }

                runOnUiThread(() -> Toast.makeText(this, "🔄 Users synced", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "❌ Sync failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNet = cm.getActiveNetworkInfo();
            return activeNet != null && activeNet.isConnected();
        }
        return false;
    }

    private void savePreferences(String username, String password) {
        SharedPreferences sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putBoolean("rememberMe", true);
        editor.apply();
    }

    private void clearPreferences() {
        SharedPreferences sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    private void loadPreferences() {
        SharedPreferences sp = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        if (sp.getBoolean("rememberMe", false)) {
            userName.setText(sp.getString("username", ""));
            passWord.setText(sp.getString("password", ""));
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void setPasswordEyeIcon(EditText editText, Drawable start, Drawable end) {
        editText.setCompoundDrawablesWithIntrinsicBounds(start, null, end, null);
    }
}
