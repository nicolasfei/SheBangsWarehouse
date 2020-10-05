package com.shebangs.warehouse;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Utils;
import com.nicolas.toollibrary.imageload.ImageLoadClass;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.component.ReceiveBillView;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.login.LoginInterface;
import com.shebangs.warehouse.ui.receipt.ReceiptVoucher;
import com.shebangs.warehouse.ui.receipt.WarehouseReceiptActivity;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppActivityManager.getInstance().addActivity(this);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_set)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //--------------初始化全局类-------------------//
        //开启打印机连接任务
        PrinterManager.getInstance().setLinkDeviceModel("Printer_F6E4");
        PrinterManager.getInstance().init(WarehouseApp.getInstance());
        //开启SupplierKeeper定时查询任务
        WarehouseKeeper.getInstance().startTimerTask();
        //初始化url图片缓存
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        ImageLoadClass.getInstance().init(BitmapFactory.decodeResource(getResources(), R.mipmap.ico_big_decolor, options));

        //界面
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.receive_print_title)
                        .setMessage(R.string.receive_print)
                        .setView(new ReceiveBillView(MainActivity.this, new ReceiptVoucher()))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                printReceiveBill();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                submitDataAndUpdate();
                            }
                        })
                        .create().show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle(R.string.logout)
                .setMessage(R.string.user_logout)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userLogout();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create().show();
    }

    /**
     * 用户登出
     */
    private void userLogout() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_LOGIN;
        vo.url = LoginInterface.WarehouseLogout;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case LoginInterface.WarehouseLogout:        //登出
                    if (!result.success) {
                        Utils.toast(MainActivity.this, MainActivity.this.getString(R.string.logout_failed) + "," + result.msg);
                    } else {
                        Utils.toast(MainActivity.this, MainActivity.this.getString(R.string.logout_success));
                    }
                    MainActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        AppActivityManager.getInstance().removeActivity(this);
        //关闭定时查询任务
        WarehouseKeeper.getInstance().cancelTimerTask();
        //打印机模块注销
        PrinterManager.getInstance().unManager();
        //释放
        ImageLoadClass.getInstance().release();
        super.onDestroy();
    }

}
