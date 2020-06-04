package com.shebangs.warehouse.ui.receipt;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;

public class WarehouseReceiptStatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_statistics);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.statistics_received, R.id.statistics_unreceived)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_receipt_statistics_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
