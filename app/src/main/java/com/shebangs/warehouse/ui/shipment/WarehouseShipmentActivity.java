package com.shebangs.warehouse.ui.shipment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.common.WarehouseDialog;
import com.shebangs.warehouse.hardware.scanner.PPdaScanner;
import com.shebangs.warehouse.hardware.scanner.Scanner;
import com.shebangs.warehouse.tool.Utils;
import com.shebangs.warehouse.ui.receipt.ReceiptGoodsInformationAdapter1;

import static com.shebangs.warehouse.tool.Tool.dp2px;

public class WarehouseShipmentActivity extends BaseActivity {

    private TextView warehouseName;
    private TextView onDutyStaff;
    private TextView businessTime;
    private TextView branch;
    private EditText codeEdit;
    private Scanner scanner;                //扫描头
    private EditText remarkEdit;
    private TextView total;
    private Button submit;

    private WarehouseShipmentViewModel viewModel;
    private ReceiptGoodsInformationAdapter1 adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_shipment);
        this.viewModel = new ViewModelProvider(this).get(WarehouseShipmentViewModel.class);
        //库房
        this.warehouseName = findViewById(R.id.warehouseName);
        this.warehouseName.setOnClickListener(this.listener);
        //在岗人员
        this.onDutyStaff = findViewById(R.id.warehouseStaffName);
        this.onDutyStaff.setOnClickListener(this.listener);
        //出库时间
//        this.businessTime = findViewById(R.id.businessTime);
//        this.businessTime.setOnClickListener(this.listener);
        //分店
        this.branch = findViewById(R.id.branch);
        this.branch.setOnClickListener(this.listener);

        //条码输入框
        this.codeEdit = findViewById(R.id.codeEdit);
        /**
         * 初始化扫描头
         */
        scanner = new PPdaScanner(this);
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                codeEdit.setText(scan);
                queryCodeInformation(scan);
            }
        });
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.codeDataChanged(codeEdit.getText().toString());
            }
        };
        this.codeEdit.addTextChangedListener(afterTextChangedListener);
        this.codeEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    queryCodeInformation(codeEdit.getText().toString());
                }
                return false;
            }
        });
        //合计
        this.total = findViewById(R.id.total);
        //备注
        this.remarkEdit = findViewById(R.id.remarkEdit);
        //提交
        this.submit = findViewById(R.id.submit);
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.submitBusinessData();
            }
        });
        //swipeMenuListView
        SwipeMenuListView swipeMenuListView = findViewById(R.id.swipeMenuListView);
        //左滑菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set item title
                deleteItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                deleteItem.setTitleSize(18);
                // set item title font color
                deleteItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        //添加左滑菜单
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteGoods(position);
                        break;
                }
                return true;
            }
        });
        this.adapter = new ReceiptGoodsInformationAdapter1(this, viewModel.getGoodsInformationList());
        swipeMenuListView.setAdapter(this.adapter);
        /**
         * 监听库房
         */
        this.viewModel.getSetWarehouseNameResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateWarehouseName();
                }
            }
        });
        /**
         * 监听在岗人员切换
         */
        this.viewModel.getSetNewOnDutyStaffSwitchResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    onDutyStaffChangeLogin();
                }
            }
        });
        /**
         * 监听新的人员登陆结果
         */
        this.viewModel.getSetNewOnDutyStaffLoginResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    updateOnDutyStaff();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(WarehouseShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });
        /**
         * 监听业务时间
         */
        this.viewModel.getSetBusinessTimeResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateBusinessTime();
                }
            }
        });
        /**
         * 监听条码输入变化
         */
        viewModel.getInputFormState().observe(this, new Observer<InputFormState>() {
            @Override
            public void onChanged(InputFormState inputFormState) {
                if (inputFormState == null) {
                    return;
                }
                if (inputFormState.getInputError() != null) {
                    codeEdit.setError(inputFormState.getInputError());
                }
            }
        });
        /**
         * 监听条码信息查询
         */
        this.viewModel.getQueryCodeInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    updateBusinessInformation();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(WarehouseShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });
        /**
         * 监听数据提交结果
         */
        this.viewModel.getSubmitBusinessDataResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getError() != null) {
                    Utils.toast(WarehouseShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
                if (operateResult.getSuccess() != null) {
                    //提交后处理
                    Utils.toast(WarehouseShipmentActivity.this, WarehouseShipmentActivity.this.getString(R.string.submit_success));
                    viewModel.updateSubmitSuccess();
                }
            }
        });
        /**
         * 监听查询须发货分店结果
         */
        this.viewModel.getQueryDeliveryRequiredBranchResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    showAndChoiceDeliveryRequiredBranch();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(WarehouseShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });
        /**
         * 监听设置当前需要发货的分店结果
         */
        this.viewModel.getSetCurrentDeliveryRequiredBranchResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    updateCurrentDeliveryRequiredBranch();
                    showProgressDialog();
                    viewModel.queryDeliveryRequiredBranchGoodsList();       //查询需发货物清单
                }
            }
        });
        /**
         * 监听查询分店发货清单结果
         */
        this.viewModel.getQueryDeliveryRequiredBranchGoodsListResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    updateBusinessInformation();
                }
                if (operateResult.getError() != null) {
                    Utils.toast(WarehouseShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        updateWarehouseName();
        updateBusinessTime();
        updateOnDutyStaff();
        updateBusinessInformation();
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.warehouseName:        //选择库房
                    warehouseChoice();
                    break;
                case R.id.warehouseStaffName:   //在岗工作人员
                    onDutyStaffChoice();
                    break;
                case R.id.businessTime:         //入库时间
                    businessTimeChoice();
                    break;
                case R.id.branch:
                    queryDeliveryRequiredBranch();  //查询须发货的分店
                    break;
                default:
                    break;
            }
        }
    };

    private void queryDeliveryRequiredBranch() {
        showProgressDialog();
        viewModel.queryDeliveryRequiredBranch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanner.scannerOpen();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanner.scannerSuspend();
    }

    /**
     * 业务时间
     */
    private void businessTimeChoice() {
        WarehouseDialog.showDateTimePickerDialog(this, new WarehouseDialog.OnDateTimePickListener() {
            @Override
            public void OnDateTimePick(String dateTime) {
                viewModel.setBusinessTime(dateTime);
            }
        });
    }

    /**
     * 在岗人员选择
     */
    private void onDutyStaffChoice() {
        WarehouseDialog.showOnDutyStaffChoiceDialog(this, new WarehouseDialog.OnSingleChoiceItemListener() {
            @Override
            public void onChoiceItem(String name) {
                viewModel.setNewOnDutyStaffSwitch(name);
            }
        });
    }

    /**
     * 在岗工作人员切换登陆
     */
    private void onDutyStaffChangeLogin() {
        WarehouseDialog.showOnDutyStaffChangeLoginDialog(this, new WarehouseDialog.OnSingleInputItemListener() {
            @Override
            public void onInputItem(String value) {
                if (TextUtils.isEmpty(value) || value.length() < 6) {
                    Utils.toast(WarehouseShipmentActivity.this, getString(R.string.invalid_password));
                } else {
                    showProgressDialog();
                    viewModel.newOnDutyStaffLogin(value);
                }
            }

            @Override
            public void onNothing() {
                Utils.toast(WarehouseShipmentActivity.this, getString(R.string.invalid_password));
            }
        });
    }

    /**
     * 库房选择
     */
    private void warehouseChoice() {
        WarehouseDialog.showWarehouseChoiceDialog(this, new WarehouseDialog.OnSingleChoiceItemListener() {
            @Override
            public void onChoiceItem(String name) {
                viewModel.setWarehouseName(name);
            }
        });
    }

    /**
     * 分店选择
     */
    private void showAndChoiceDeliveryRequiredBranch() {
        WarehouseDialog.showAndChoiceDeliveryRequiredBranchDialog(this, viewModel.getDeliveryRequiredBranches(), new WarehouseDialog.OnSingleChoiceItemListener() {
            @Override
            public void onChoiceItem(String value) {
                viewModel.setCurrentDeliveryRequiredBranch(value);
            }
        });
    }

    /**
     * 查条码
     *
     * @param code 条码
     */
    private void queryCodeInformation(String code) {
        showProgressDialog();
        viewModel.queryCodeInformation(code);
    }

    /**
     * 更新库房名
     */
    private void updateWarehouseName() {
        String value = getString(R.string.warehouse_name) + "\u3000\u3000\u3000\u3000\u3000" + this.viewModel.getWarehouseName();
        this.warehouseName.setText(value);
    }

    /**
     * 更新在岗人员
     */
    private void updateOnDutyStaff() {
        String value = getString(R.string.warehouse_staff_name) + "\u3000\u3000\u3000\u3000\u3000" + this.viewModel.getOnDutyStaff();
        this.onDutyStaff.setText(value);
    }

    /**
     * 更新业务时间
     */
    private void updateBusinessTime() {
        String value = getString(R.string.exwarehouse_business_time) + "\u3000\u3000\u3000\u3000" + this.viewModel.getBusinessTime();
//        this.businessTime.setText(value);
    }

    /**
     * 更新当前需发货店铺名
     */
    private void updateCurrentDeliveryRequiredBranch() {
        String value = getString(R.string.branch_code1) + "\u3000\u3000\u3000\u3000" + this.viewModel.getCurrentDeliveryRequiredBranch();
        this.branch.setText(value);
    }

    /**
     * 删除商品
     *
     * @param position pos
     */
    private void deleteGoods(int position) {
        this.viewModel.deleteGoods(position);
    }

    /**
     * 更新商品信息
     */
    private void updateBusinessInformation() {
        //更新商品信息
        this.adapter.notifyDataSetChanged();
        //合计更新
        String value = getString(R.string.total) + ":" + this.viewModel.getTotal() + "\u3000" +
                "<font color=\"blue\">" + getString(R.string.scaned) + getString(R.string.colon_zh) + this.viewModel.getScannedGoodsNum() + "\u3000" + "</font>" +
                "<font color=\"red\">" + getString(R.string.unScan) + getString(R.string.colon_zh) + this.viewModel.getUnScanGoodsNum() + "</font>";
        this.total.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
        if (this.viewModel.getTotal() > 0 && !this.viewModel.isHaveInvalidGoods() && this.viewModel.getScannedGoodsNum() > 0) {
            if (!this.submit.isClickable()) {
                this.submit.setClickable(true);
                this.submit.setBackground(getDrawable(R.drawable.shape_rectangle_red));
            }
        } else {
            if (this.submit.isClickable()) {
                this.submit.setClickable(false);
                this.submit.setBackground(getDrawable(R.drawable.shape_rectangle_grey));
            }
        }
    }

    @Override
    protected void onDestroy() {
        scanner.scannerClose();
        super.onDestroy();
    }
}
