package com.example.pondmatev1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class UserProfile extends Fragment {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TextView fullNameText, addressText, userTypeText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(v -> {
            sessionManager.clearSession();

            Intent intent = new Intent(requireContext(), login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });



        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        fullNameText = view.findViewById(R.id.txtFullNameValue);
        addressText = view.findViewById(R.id.txtAddressValue);
        userTypeText = view.findViewById(R.id.txtPositionValue);

        String username = sessionManager.getUsername();
        if (username != null) {
            Cursor cursor = dbHelper.getUserInfo(username);
            if (cursor != null && cursor.moveToFirst()) {
                fullNameText.setText(cursor.getString(cursor.getColumnIndexOrThrow("fullname")));
                addressText.setText(cursor.getString(cursor.getColumnIndexOrThrow("address")));
                userTypeText.setText(cursor.getString(cursor.getColumnIndexOrThrow("usertype")));
                cursor.close();
            }
        }

        return view;


    }
}
