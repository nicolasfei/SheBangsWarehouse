package com.shebangs.warehouse.ui.shipment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.Utils;
import com.nicolas.toollibrary.VibratorUtil;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.data.ShipmentGoodsStatistics;
import com.shebangs.warehouse.data.ShipmentGoodsStatisticsAdapter;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

public class ShipmentActivity extends BaseActivity implements View.OnClickListener {
    private TextView warehouseName;
    private TextView branch;
    private EditText codeEdit;
    private Scanner scanner;                //扫描头
    private TextView total, scan, scanned;
    private Button submit;

    private ShipmentViewModel viewModel;

    private PullRefreshListView pullRefreshListView1;
    private PullRefreshListView pullRefreshListView2;
    private ShipmentGoodsStatisticsAdapter adapter;
    private ShipmentGoodsStatisticsAdapter adapter2;

    private String choiceBranchCode = "";      //当前选择的要发货的分店编码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehouse_shipment_content);
        this.viewModel = new ViewModelProvider(this).get(ShipmentViewModel.class);
        //库房
        this.warehouseName = findClickView(R.id.warehouseName);

        //分店
        this.branch = findClickView(R.id.branchName);
        this.branch.setHintTextColor(Color.RED);
        this.branch.setHint(R.string.branch_choice);
        this.branch.setClickable(true);

        //条码输入框
        this.codeEdit = findViewById(R.id.codeEdit);
        this.codeEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_UP) {
                    handlerScanResultInKeyListen(codeEdit.getText().toString());
                    return true;
                }
                return false;
            }
        });
        this.codeEdit.requestFocus();

        //初始化扫描头
        this.scanner = new PPdaScanner(this);
        this.scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerScanResultInBroadcast(scan);
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
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    handlerManualCodeInput(codeEdit.getText().toString());
                }
                return false;
            }
        });
        //合计
        this.total = findViewById(R.id.total);
        this.scanned = findViewById(R.id.scanned);
        this.scan = findViewById(R.id.scan);

        //提交
        this.submit = findClickView(R.id.submit);
        this.submit.setClickable(false);

        //待发货清单
        this.pullRefreshListView1 = findViewById(R.id.swipeMenuListView1);
        //适配器
        this.adapter = new ShipmentGoodsStatisticsAdapter(this, viewModel.getScanStatistics());
        this.adapter.setOnShipmentGoodsStatisticsClickListener(new ShipmentGoodsStatisticsAdapter.OnShipmentGoodsStatisticsClickListener() {
            @Override
            public void onNotScannedClick(int position) {       //某个供应商的所有订单被手动扫描
                ShipmentGoodsStatistics statistics = viewModel.getScanStatistics().get(position);
                String hint = getString(R.string.setThisStatisticsToScannedAll) + "（" + statistics.supplier + "）" + getString(R.string.sureToSet);
                BruceDialog.showAlertDialog(ShipmentActivity.this, getString(R.string.hint),
                        hint, new BruceDialog.OnAlertDialogListener() {
                            @Override
                            public void onSelect(boolean confirm) {
                                if (confirm) {
                                    viewModel.setShipmentStatisticsAllScan(position);
                                }
                            }
                        });
            }

            @Override
            public void onScannedClick(int position) {

            }
        });
        this.pullRefreshListView1.setAdapter(this.adapter);
        this.pullRefreshListView1.disablePullRefresh();
        this.pullRefreshListView1.disablePushLoadMore();

        //已扫描清单
        this.pullRefreshListView2 = findViewById(R.id.swipeMenuListView2);
        //适配器
        this.adapter2 = new ShipmentGoodsStatisticsAdapter(this, viewModel.getScannedStatistics());
        this.adapter2.setOnShipmentGoodsStatisticsClickListener(new ShipmentGoodsStatisticsAdapter.OnShipmentGoodsStatisticsClickListener() {
            @Override
            public void onNotScannedClick(int position) {       //某个供应商的所有订单被手动设置为待扫描
            }

            @Override
            public void onScannedClick(int position) {
                BruceDialog.showAlertDialog(ShipmentActivity.this, getString(R.string.hint),
                        getString(R.string.sureToSetThisStatisticsToScanAll), new BruceDialog.OnAlertDialogListener() {
                            @Override
                            public void onSelect(boolean confirm) {
                                if (confirm) {
                                    viewModel.setShipmentStatisticsAllNotScan(position);
                                }
                            }
                        });
            }
        });
        this.pullRefreshListView2.setAdapter(this.adapter2);
        this.pullRefreshListView2.disablePullRefresh();
        this.pullRefreshListView2.disablePushLoadMore();

        //监听条码输入变化
        viewModel.getInputFormState().observe(this, new Observer<InputFormState>() {
            @Override
            public void onChanged(InputFormState inputFormState) {
                if (inputFormState == null) {
                    return;
                }
//                if (inputFormState.getInputError() != null) {
//                    codeEdit.setError(inputFormState.getInputError());
//                }
            }
        });

        //监听条码信息查询
        this.viewModel.getQueryCodeInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        VibratorUtil.getInstance().showWarning();
                        BruceDialog.showPromptDialog(ShipmentActivity.this, (String) msg.obj);
                    } else {
                        updateBusinessInformation();
                    }
                }
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(ShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
                codeEdit.setText("");
                codeEdit.requestFocus();        //codeEdit获取焦点
                codeEdit.setSelection(0);       //codeEdit光标移到起始位置
            }
        });

        //监听数据提交结果
        this.viewModel.getSubmitBusinessDataResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(ShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
                if (operateResult.getSuccess() != null) {
                    updateBranchID("");     //提交成功，分店设置为空
                    updateBusinessInformation();
                    Utils.toast(ShipmentActivity.this, WarehouseApp.getInstance().getString(R.string.submit_success));
                }
            }
        });

        //监听拉取分店订单列表
        this.viewModel.getQueryDeliveryRequiredBranchGoodsListResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    if (operateResult.getSuccess().getMessage() != null) {
                        BruceDialog.showPromptDialog(ShipmentActivity.this, (String) operateResult.getSuccess().getMessage().obj);
                    }
                    //更新分店名字
                    updateBranchID(viewModel.getCurrentDeliveryRequiredBranchCode());
                    updateBusinessInformation();
                }
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(ShipmentActivity.this, operateResult.getError().getErrorMsg());
                }
//                if (pullRefreshListView1.isPullToRefreshing()) {
//                    pullRefreshListView1.refreshFinish();
//                }
                codeEdit.setText("");
                BruceDialog.dismissProgressDialog();
            }
        });

        updateWarehouseName();
        updateBusinessInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shipment_menu, menu);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.app_bar_search));
//        searchView.setQueryHint(getString(R.string.search_hint));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                BruceDialog.showAlertDialog(this, getString(R.string.warning), getString(R.string.isClearData),
                        new BruceDialog.OnAlertDialogListener() {
                            @Override
                            public void onSelect(boolean confirm) {
                                if (confirm) {
                                    clearData();
                                    clearBranchID();
                                }
                            }
                        });
                break;
            case android.R.id.home:
                if (this.viewModel.isHavDataNotProcessed()) {
                    BruceDialog.showAlertDialog(this, getString(R.string.warning), getString(R.string.isHavDataNotProcessed),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {
                                    if (confirm) {
                                        ShipmentActivity.this.finish();
                                    }
                                }
                            });
                } else {
                    finish();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 清空所有数据：已扫描，未扫描
     */
    private void clearData() {
        viewModel.clearData();
        updateBusinessInformation();
    }

    /**
     * 清空选择的分店
     */
    private void clearBranchID() {
        choiceBranchCode = "";        //清空分店编号
        updateBranchID(choiceBranchCode);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warehouseName:        //选择库房
                warehouseChoice();
                break;
            case R.id.branchName:
                queryDeliveryRequiredBranch();  //拉取须发货的分店
                break;
            case R.id.submit:
                BruceDialog.showAlertDialog(ShipmentActivity.this, getString(R.string.shipment), getString(R.string.submit_sure), new BruceDialog.OnAlertDialogListener() {
                    @Override
                    public void onSelect(boolean confirm) {
                        if (confirm) {
                            BruceDialog.showProgressDialog(ShipmentActivity.this, getString(R.string.submitting));
                            viewModel.submitBusinessData();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 以分店来查询需要发货的清单
     */
    private void queryDeliveryRequiredBranch() {
        //选择分店
        BruceDialog.showAutoCompleteTextViewDialog(R.string.branch_choice, R.string.branch_input_please, InputType.TYPE_CLASS_TEXT,
                ShipmentActivity.this, WarehouseKeeper.getInstance().getBranchList(), new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (!TextUtils.isEmpty(itemName) && !choiceBranchCode.equals(itemName)) {
                            updateBranchID(itemName);
                            queryBranchOrderList();
                        }
                    }
                });
    }

    /**
     * 查询分店订单列表
     */
    private void queryBranchOrderList() {
        BruceDialog.showProgressDialog(this, getString(R.string.data_query));
        viewModel.queryDeliveryRequiredBranch(choiceBranchCode);
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

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(codeEdit.getWindowToken(), 0); //强制隐藏键盘
        }
    }

    /**
     * 库房选择
     */
    private void warehouseChoice() {
        BruceDialog.showSingleChoiceDialog(R.string.warehouse_choice, this, WarehouseKeeper.getInstance().getWarehouseInformationList(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (!TextUtils.isEmpty(itemName)) {
                    if (!itemName.equals(WarehouseKeeper.getInstance().getOnDutyWarehouse().name)) {
                        switchWarehouse(itemName);
                    }
                }
            }
        });
    }

    /**
     * 库房切换
     *
     * @param itemName 库房名
     */
    private void switchWarehouse(String itemName) {
        if (viewModel.isHavDataNotProcessed()) {
            BruceDialog.showPromptDialog(ShipmentActivity.this,
                    getString(R.string.shipmentHasNoDealData));
        } else {
            //更新库房
            WarehouseKeeper.getInstance().setOnDutyWarehouse(itemName);
            //更新打印机
            PrinterManager.getInstance().resetLinkDeviceModel(itemName);
            //更新库房名显示
            updateWarehouseName();
            if (!TextUtils.isEmpty(choiceBranchCode) && !(choiceBranchCode.equals("null"))) {
                queryBranchOrderList();
            }
        }
    }

    /**
     * 手动输入条码处理
     *
     * @param code 条码
     */
    private void handlerManualCodeInput(String code) {
        Tool.hideSoftInput(this);
        queryCodeInformation(code);
    }

    /**
     * 处理扫描结果--按键监听方式
     *
     * @param scan 扫描结果
     */
    private void handlerScanResultInKeyListen(String scan) {
        queryCodeInformation(scan);
    }

    /**
     * 处理扫描结果--广播方式
     *
     * @param scan scan
     */
    private void handlerScanResultInBroadcast(String scan) {
        queryCodeInformation(scan);
    }

    /**
     * 处理条码输入
     * 如果第一次扫描，这是查询该条码上的分店的代发货清单
     * 否则是查询条码信息
     *
     * @param code 条码
     */
    private void queryCodeInformation(String code) {
        BruceDialog.showProgressDialog(this, getString(R.string.data_query));
        viewModel.queryCodeInformation(code);
    }

    /**
     * 更新库房名
     */
    private void updateWarehouseName() {
        String value = getString(R.string.warehouse_name) + "\u3000\u3000\u3000\u3000\u3000" + WarehouseKeeper.getInstance().getOnDutyWarehouse().name;
        this.warehouseName.setText(value);
    }

    /**
     * 更新分店ID
     *
     * @param branchID 分店ID
     */
    private void updateBranchID(String branchID) {
        String value;
        if (TextUtils.isEmpty(branchID)) {
            value = "";
        } else {
            value = getString(R.string.branch_id) + "\u3000\u3000\u3000\u3000" + branchID;
        }
        choiceBranchCode = branchID;
        this.branch.setText(value);
    }

    /**
     * 更新商品信息
     */
    private void updateBusinessInformation() {
        //更新商品信息
        this.adapter.notifyDataSetChanged();
        this.adapter2.notifyDataSetChanged();
        //合计更新
        this.total.setText(String.valueOf(this.viewModel.getTotal()));
        this.scanned.setText(String.valueOf(this.viewModel.getScannedTotal()));
        this.scan.setText(String.valueOf(this.viewModel.getNotScanTotal()));
        if (this.viewModel.getScannedTotal() > 0) {        //有扫描的订单就可以提交
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
        super.onDestroy();
    }
}
