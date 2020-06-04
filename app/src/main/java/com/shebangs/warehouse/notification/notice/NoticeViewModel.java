package com.shebangs.warehouse.notification.notice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;

import java.util.List;

public class NoticeViewModel extends ViewModel {
    private static final String TAG = "NoticeViewModel";
    private MutableLiveData<OperateResult> noticeGetResult;
    private MutableLiveData<OperateResult> advertGetResult;
    private MutableLiveData<OperateResult> initResult;

    private List<AdvertData> advertData;
    private List<NoticeData> noticeData;

    public NoticeViewModel() {
        this.noticeGetResult = new MutableLiveData<>();
        this.advertGetResult = new MutableLiveData<>();
        this.initResult = new MutableLiveData<>();

        DataLocalManagement.getInstance().setOnManagementGetLocalDataListener(new DataLocalManagement.OnManagementGetLocalDataListener() {
            @Override
            public void onGetNoticeDataFinish(NoticeData data) {
                noticeData.add(data);
                noticeGetResult.setValue(new OperateResult(new OperateInUserView(null)));
            }

            @Override
            public void OnGetAdvertDataFinish(AdvertData data) {
                advertData.add(data);
                advertGetResult.setValue(new OperateResult(new OperateInUserView(null)));
            }
        });

        DataLocalManagement.getInstance().setOnManagementGetLocalDataInitListener(new DataLocalManagement.OnManagementGetLocalDataInitListener() {
            @Override
            public void onGetLocalDataInitFinish() {
                advertData = DataLocalManagement.getInstance().getAdvertData();
                noticeData = DataLocalManagement.getInstance().getNoticeData();
                initResult.setValue(new OperateResult(new OperateInUserView(null)));
            }
        });
    }

    public MutableLiveData<OperateResult> getAdvertGetResult() {
        return advertGetResult;
    }

    public MutableLiveData<OperateResult> getNoticeGetResult() {
        return noticeGetResult;
    }

    public MutableLiveData<OperateResult> getInitResult() {
        return initResult;
    }

    /**
     * 获取广告数据
     *
     * @return 广告数据
     */
    public List<AdvertData> getAdvertData() {
        return advertData;
    }

    /**
     * 获取通知数据
     */
    public List<NoticeData> getNoticeData() {
        return noticeData;
    }

    /**
     * 广告，通知本地初始化
     */
    public void advertNoticeLocalInit() {
        DataLocalManagement.getInstance().initLocalData();
    }

    /**
     * 查询广告
     */
    public void queryAdvert() {

    }

    /**
     * 查询通知
     */
    public void queryNotice() {

    }
}
