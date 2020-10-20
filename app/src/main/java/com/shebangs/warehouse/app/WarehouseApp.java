package com.shebangs.warehouse.app;

import android.app.Application;
import android.os.Environment;

import com.nicolas.toollibrary.GlobalCrashHandler;
import com.shebangs.warehouse.FirstActivity;
import com.shebangs.warehouse.R;

public class WarehouseApp extends Application {

    private static WarehouseApp app;
    private String apkSavePath;

    public static WarehouseApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        this.apkSavePath = getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() + "prxd.apk";
        //初始化异常处理
        GlobalCrashHandler.getInstance().init(app, "",
                getString(R.string.emailForm), getString(R.string.formPassword),
                getString(R.string.emailTo), getString(R.string.emailContentHead), FirstActivity.class);
    }

    public String getApkSavePath() {
        return apkSavePath;
    }
}
