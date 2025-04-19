package com.example.pondmatev1;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nafis.bottomnavigation.NafisBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    NafisBottomNavigation bottomNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.home1));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.select2));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.productioncost3));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.feeder4));
        bottomNavigation.add(new NafisBottomNavigation.Model(5, R.drawable.activity5));

        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {

                if (model.getId() == 1) {
                    fraghome fraghome = new fraghome();
                    getSupportFragmentManager().beginTransaction().replace(R.id.con, fraghome).commit();

                } else if (model.getId() == 2) {
                    FragmentTwo fragmentTwo = new FragmentTwo();
                    getSupportFragmentManager().beginTransaction().replace(R.id.con, fragmentTwo).commit();


                } else if (model.getId() == 3) {
                    pcostroi pcostroi = new pcostroi();
                    getSupportFragmentManager().beginTransaction().replace(R.id.con, pcostroi).commit();

                } else if (model.getId() == 4) {


                } else if (model.getId() == 5) {


                }
                return null;
            }
        });

    }
}