package com.shebangs.warehouse.app;

import android.app.Application;

public class WarehouseApp extends Application {

    private static WarehouseApp app;

    public static WarehouseApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
