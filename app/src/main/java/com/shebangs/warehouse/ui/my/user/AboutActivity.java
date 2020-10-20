package com.shebangs.warehouse.ui.my.user;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView about = findViewById(R.id.about);
        //获取app当前版本
        String appCurrentVersion = "";
        PackageManager manager = WarehouseApp.getInstance().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(WarehouseApp.getInstance().getPackageName(), 0);
            appCurrentVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String value = "App名称：" +
                getString(R.string.app_name) + " \n" +
                "版本号：" +
                appCurrentVersion + " \n" +
                "\n" +
                "系统名称：\n" +
                "DAWN BUSINESS IT SYSTEM \n" +
                "\n" +
                "版权信息：\n" +
                "Copyright  2009-2020 By Si Chuan Province Dawn Business CO.,Ltd.All rights Reserved.";
        about.setText(value);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView d = null;
                d.setText("1");
            }
        });
    }
}
