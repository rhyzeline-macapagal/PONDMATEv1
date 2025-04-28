package com.example.pondmatev1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentTwo extends Fragment {

    String[] items = {"Tilapia", "Milkfish", "Shrimp", "Seaweed", "Carp", "Oyster", "Mussel"};
    AutoCompleteTextView autoCompleteText;
    ArrayAdapter<String> adapterItems;
    View view;
    EditText SOA;
    Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_two, container, false);

        SOA = view.findViewById(R.id.start_of_act);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
            private void updateCalendar(){
                String Format ="MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);

                SOA.setText(sdf.format(calendar.getTime()));
            }
        };
        SOA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(
                        requireContext(),
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        autoCompleteText = view.findViewById(R.id.auto_complete_txt1);
        adapterItems = new ArrayAdapter<>(requireContext(), R.layout.list_items, items);
        autoCompleteText.setAdapter(adapterItems);

        autoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Example action: show a Toast
                Toast.makeText(requireContext(), "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
