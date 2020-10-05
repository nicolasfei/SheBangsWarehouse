package com.shebangs.warehouse.app;

import android.app.Application;

import com.nicolas.toollibrary.GlobalCrashHandler;
import com.shebangs.warehouse.FirstActivity;

public class WarehouseApp extends Application {

    private static WarehouseApp app;

    public static WarehouseApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        //初始化异常处理
        GlobalCrashHandler.getInstance().init(app, "",
                "fshq_test@163.com", "ZIPGXOJDAEZVOJBY", "fshq_debug@163.com",
                "She bangs warehouse ", FirstActivity.class);
    }


}
