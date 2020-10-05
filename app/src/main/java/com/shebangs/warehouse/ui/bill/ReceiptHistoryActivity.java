package com.shebangs.warehouse.ui.bill;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.toollibrary.BruceDialog;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

public class ReceiptHistoryActivity extends BaseActivity implements View.OnClickListener {

    private TextView warehouseName;
    private ReceiptHistoryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_history);
        //viewModel
        this.viewModel = new ViewModelProvider(this).get(ReceiptHistoryViewModel.class);
        //库房名
        this.warehouseName = findClickView(R.id.warehouseName);

        //查询收货小票
        this.viewModel.receiptHistoryQuery();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.warehouseName:
                warehouseChoice();
                break;
            default:
                break;
        }
    }

    /**
     * 库房选择
     */
    private void warehouseChoice() {
        BruceDialog.showSingleChoiceDialog(R.string.warehouse_choice, this, WarehouseKeeper.getInstance().getWarehouseInformationList(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (TextUtils.isEmpty(itemName)) {
                    return;
                } else {
                    WarehouseKeeper.getInstance().setOnDutyWarehouse(itemName);
                    updateWarehouseName();
                }
            }
        });
    }

    /**
     * 更新库房名
     */
    private void updateWarehouseName() {
        String value = getString(R.string.warehouse_name) + "\u3000\u3000" + WarehouseKeeper.getInstance().getOnDutyWarehouse().name + "/" +
                WarehouseKeeper.getInstance().getOnDutyStaffName();
        this.warehouseName.setText(value);
    }
}
