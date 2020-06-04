package com.shebangs.warehouse.notification.notice;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据本地管理
 */
public class DataLocalManagement {

    private static final String TAG = "DataLocalManagement";
    private List<NoticeData> noticeData;
    private List<AdvertData> advertData;

    private static DataLocalManagement management = new DataLocalManagement();
    private OnManagementGetLocalDataListener listener;
    private OnManagementGetLocalDataInitListener initListener;
    private boolean initializationFinish = false;     //是否已经初始化加载了本地数据

    private DataLocalManagement() {
        this.noticeData = new ArrayList<>();
        this.advertData = new ArrayList<>();
    }

    /**
     * 初始化数据
     */
    public void initLocalData() {
        //初始化数据
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!initializationFinish) {
                    for (int i = 0; i < 3; i++) {
                        NoticeData notice = new NoticeData(((i + 1) + ".该项目是在的核心部分就是重写的Context.LayoutInflater"), System.currentTimeMillis());
                        noticeData.add(notice);
                    }
                    AdvertData advert = new AdvertData(BitmapFactory.decodeResource(WarehouseApp.getInstance().getResources(), R.mipmap.advert1), System.currentTimeMillis());
                    advertData.add(advert);
                    AdvertData advert1 = new AdvertData(BitmapFactory.decodeResource(WarehouseApp.getInstance().getResources(), R.mipmap.advert2), System.currentTimeMillis());
                    advertData.add(advert1);
                    AdvertData advert2 = new AdvertData(BitmapFactory.decodeResource(WarehouseApp.getInstance().getResources(), R.mipmap.advert3), System.currentTimeMillis());
                    advertData.add(advert2);
                    initializationFinish = true;
                }
                handler.sendEmptyMessage(1);
            }
        };
        new Thread(runnable).start();
    }

    private Handler handler = new Handler(WarehouseApp.getInstance().getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    if (initListener != null) {
                        initListener.onGetLocalDataInitFinish();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static DataLocalManagement getInstance() {
        return management;
    }

    /**
     * 获取广告数据
     *
     * @return 广告
     */
    public List<AdvertData> getAdvertData() {
        return advertData;
    }

    /**
     * 获取通知数据
     *
     * @return 通知
     */
    public List<NoticeData> getNoticeData() {
        return noticeData;
    }

    /**
     * 设置监听
     *
     * @param listener listener
     */
    public void setOnManagementGetLocalDataListener(OnManagementGetLocalDataListener listener) {
        this.listener = listener;
    }

    /**
     * 接口
     */
    public interface OnManagementGetLocalDataListener {
        void onGetNoticeDataFinish(NoticeData data);

        void OnGetAdvertDataFinish(AdvertData data);
    }

    /**
     * 设置监听
     *
     * @param initListener listener
     */
    public void setOnManagementGetLocalDataInitListener(OnManagementGetLocalDataInitListener initListener) {
        this.initListener = initListener;
    }

    /**
     * 初始化接口
     */
    public interface OnManagementGetLocalDataInitListener {
        void onGetLocalDataInitFinish();
    }
}
