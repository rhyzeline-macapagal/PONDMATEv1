package com.example.pondmatev1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fraghome extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fraghome, container, false);


        // Load ActProd fragment into the container
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.calendarContainer, new ActProd())
                .commit();


        return view;
    }
}
