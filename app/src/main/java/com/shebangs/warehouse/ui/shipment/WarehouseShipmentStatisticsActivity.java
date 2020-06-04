package com.shebangs.warehouse.ui.shipment;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;

public class WarehouseShipmentStatisticsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_statistics);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.statistics_send, R.id.statistics_unsend)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_shipment_statistics_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
