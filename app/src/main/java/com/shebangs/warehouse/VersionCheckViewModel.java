package com.shebangs.warehouse;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.apkdown.ApkDownClass;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.OperateError;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.manager.ManagerInterface;

import java.util.HashMap;
import java.util.Map;

public class VersionCheckViewModel extends ViewModel {
    private String appCurrentVersion;       //app当前版本
    private String appLastVersion;          //app最新版本

    private MutableLiveData<OperateResult> matchVersionResult = new MutableLiveData<>();        //版本比较结果
    private MutableLiveData<OperateResult> newAppDownResult = new MutableLiveData<>();          //新版本app下载结果

    public VersionCheckViewModel() {
        //获取app当前版本
        PackageManager manager = WarehouseApp.getInstance().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(WarehouseApp.getInstance().getPackageName(), 0);
            this.appCurrentVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            this.appCurrentVersion = "";
        }
    }

    public LiveData<OperateResult> getMatchVersionResult() {
        return matchVersionResult;
    }

    public LiveData<OperateResult> getNewAppDownResult() {
        return newAppDownResult;
    }

    /**
     * app版本检测
     */
    public void checkAppLastVersion() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_MANAGER;
        vo.url = ManagerInterface.VersionCheck;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 下载新版本App
     */
    public void downNewVersionApp(String url) {
        new ApkDownClass(WarehouseApp.getInstance(), url, WarehouseApp.getInstance().getApkSavePath(), new ApkDownClass.OnApkDownListener() {
            @Override
            public void downFinish(String apkPath) {
                if (TextUtils.isEmpty(apkPath)) {
                    newAppDownResult.setValue(new OperateResult(
                            new OperateError(-1, WarehouseApp.getInstance().getString(R.string.downApkFailed), null)));
                } else {
                    Message msg = new Message();
                    msg.obj = apkPath;
                    newAppDownResult.setValue(new OperateResult(new OperateInUserView(msg)));
                }
            }
        }).downApk();
    }

    /**
     * 响应
     */
    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {
        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ManagerInterface.VersionCheck:
                    if (result.success) {
                        appLastVersion = result.data;
                    } else {
                        appLastVersion = "";
                    }
                    matchAppVersion();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 版本比较
     */
    private void matchAppVersion() {
        if (TextUtils.isEmpty(appCurrentVersion)) {
            matchVersionResult.setValue(new OperateResult(new OperateError(-1,
                    WarehouseApp.getInstance().getString(R.string.localCheckAppVFailed), null)));
        } else if (TextUtils.isEmpty(appLastVersion)) {
            matchVersionResult.setValue(new OperateResult(new OperateError(-1,
                    WarehouseApp.getInstance().getString(R.string.remoteCheckAppVFailed), null)));
        } else {
            Message msg = new Message();
            if (appCurrentVersion.compareTo(appLastVersion) < 0) {     //有新版本app发布
                msg.what = 1;
            } else {
                msg.what = 0;
            }
            matchVersionResult.setValue(new OperateResult(new OperateInUserView(msg)));
        }
    }
}
