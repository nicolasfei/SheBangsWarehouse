package com.shebangs.warehouse.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.ModuleNavigation;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.ui.goodsback.GoodsBackActivity;
import com.shebangs.warehouse.ui.receipt.WarehouseReceiptActivity;
import com.shebangs.warehouse.ui.receipt.WarehouseReceiptStatisticsActivity;
import com.shebangs.warehouse.ui.shipment.WarehouseShipmentActivity;
import com.shebangs.warehouse.ui.shipment.WarehouseShipmentStatisticsActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<OperateResult> updateNavNumResult;
    private List<ModuleNavigation> content = new ArrayList<>();

    public HomeViewModel() {
        this.updateNavNumResult = new MutableLiveData<>();
        content.add(new ModuleNavigation(true, WarehouseApp.getInstance().getString(R.string.nav_warehouse_manager), 0, null));
        content.add(new ModuleNavigation(false, WarehouseApp.getInstance().getString(R.string.nav_warehousing), R.drawable.ic_warehousing, WarehouseReceiptActivity.class));
        content.add(new ModuleNavigation(false, WarehouseApp.getInstance().getString(R.string.nav_exwarehouse), R.drawable.ic_exwarehous, WarehouseShipmentActivity.class));
        content.add(new ModuleNavigation(false, WarehouseApp.getInstance().getString(R.string.nav_goodsback), R.drawable.ic_goods_back, GoodsBackActivity.class));
//        content.add(new ModuleNavigation(false, WarehouseApp.getInstance().getString(R.string.nav_warehousing_statistics), R.drawable.ic_warehousing_statistics, WarehouseReceiptStatisticsActivity.class));
//        content.add(new ModuleNavigation(false, WarehouseApp.getInstance().getString(R.string.nav_exwarehouse_statistics), R.drawable.ic_exwarehous_statistics, WarehouseShipmentStatisticsActivity.class));
    }

    public LiveData<OperateResult> getUpdateNavNumResult() {
        return updateNavNumResult;
    }

    /**
     * 更新ModuleNavigation通知数字
     *
     * @param position position
     * @param num      num
     */
    public void updateModuleNoticeNum(int position, int num) {
        ModuleNavigation item = content.get(position);
        if (item.getNavigationNum() != num) {
            item.setNavigationNum(num);
            updateNavNumResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    public ModuleNavigation getModuleNavigation(int position) {
        return content.get(position);
    }

    public List<ModuleNavigation> getModuleNavigationList() {
        return content;
    }
}