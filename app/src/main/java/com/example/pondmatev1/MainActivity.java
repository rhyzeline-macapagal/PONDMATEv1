package com.example.pondmatev1;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;


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

        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.getUsername() == null) {
            // No user logged in, redirect to login
            Intent intent = new Intent(this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView profileIcon = findViewById(R.id.profileIcon);

        profileIcon.setOnClickListener(v -> {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.con, new UserProfile())
                    .commit();
        });

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.home1));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.select2));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.productioncost3));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.feeder4));
        bottomNavigation.add(new NafisBottomNavigation.Model(5, R.drawable.activity5));


        bottomNavigation.show(1, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.con, new fraghome()).commit();

        bottomNavigation.setOnClickMenuListener(new Function1<NafisBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(NafisBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.con, new fraghome())
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.con, new Breed())
                                .commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.con, new pcostroi())
                                .commit();
                        break;
                    case 4:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.con, new feeders())
                                .commit();
                        break;
                    case 5:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.con, new ActProd())
                                .commit();
                        break;
                }
                return null;
            }
        });


    }

}