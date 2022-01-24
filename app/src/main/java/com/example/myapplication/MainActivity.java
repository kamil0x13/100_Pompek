package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bootomNav);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new TreningFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.trening);

        bottomNavigationView.setOnItemSelectedListener(bottomNavMethod);


    }

    private BottomNavigationView.OnItemSelectedListener bottomNavMethod = new NavigationBarView.OnItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.zasady:
                    fragment = new ZasadyFragment();
                    break;

                case R.id.trening:
                    fragment = new TreningFragment();
                    break;

                case R.id.historia:
                    fragment = new HistoriaFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            return true;
        }
    };
}