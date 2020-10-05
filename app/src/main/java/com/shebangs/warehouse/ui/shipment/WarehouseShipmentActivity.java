package com.shebangs.warehouse.ui.shipment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
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

import com.nicolas.componentlibrary.pullrefresh.PullRefreshListView;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.data.ShipmentGoodsStatisticsAdapter;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;


public class WarehouseShipmentActivity extends BaseActivity implements View.OnClickListener {
    private TextView warehouseName;
    private TextView branch;
    private EditText codeEdit;
    private Scanner scanner;                //扫描头
    private TextView total;
    private Button submit;

    private WarehouseShipmentViewModel viewModel;

    private PullRefreshListView pullRefreshListView1;
    private PullRefreshListView pullRefreshListView2;
    private ShipmentGoodsStatisticsAdapter adapter;
    private ShipmentGoodsStatisticsAdapter adapter2;

    private String lastCode = "";            //上一次扫描的条码

    private String choiceBranchCode = "";      //当前选择的要发货的分店编码

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehouse_shipment_content);
        this.viewModel = new ViewModelProvider(this).get(WarehouseShipmentViewModel.class);
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

        //提交
        this.submit = findViewById(R.id.submit);
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.submitBusinessData();
            }
        });
        this.submit.setClickable(false);

        //待发货清单
        this.pullRefreshListView1 = findViewById(R.id.swipeMenuListView1);
        //适配器
        this.adapter = new ShipmentGoodsStatisticsAdapter(this, viewModel.getScanStatistics());
        this.adapter.setOnShipmentGoodsStatisticsClickListener(new ShipmentGoodsStatisticsAdapter.OnShipmentGoodsStatisticsClickListener() {
            @Override
            public void onNotScannedClick(int position) {       //某个供应商的所有订单被手动扫描
                BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.hint),
                        getString(R.string.sureToSetThisStatisticsToScannedAll), new BruceDialog.OnAlertDialogListener() {
                            @Override
                            public void onSelect(boolean confirm) {
                                if (confirm) {
                                    viewModel.setShipmentStatisticsAllScan(position);
                                }
                            }
                        });
            }
        });
        this.pullRefreshListView1.setAdapter(this.adapter);
        this.pullRefreshListView1.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshData();
            }
        });
        this.pullRefreshListView1.disablePushLoadMore();

        //已扫描清单
        this.pullRefreshListView2 = findViewById(R.id.swipeMenuListView2);
        //适配器
        this.adapter2 = new ShipmentGoodsStatisticsAdapter(this, viewModel.getScannedStatistics());
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
                dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    Message msg = operateResult.getSuccess().getMessage();
                    if (msg != null) {
                        BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.success), (String) msg.obj,
                                new BruceDialog.OnAlertDialogListener() {
                                    @Override
                                    public void onSelect(boolean confirm) {

                                    }
                                });
                    } else {
                        updateBusinessInformation();
                    }
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.failed), operateResult.getError().getErrorMsg(),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
                }
                codeEdit.requestFocus();        //codeEdit获取焦点
                codeEdit.setSelection(0);       //codeEdit光标移到起始位置
            }
        });

        //监听数据提交结果
        this.viewModel.getSubmitBusinessDataResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.failed), operateResult.getError().getErrorMsg(),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
                }
                if (operateResult.getSuccess() != null) {
                    BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.success),
                            getString(R.string.submit_success),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
                    updateBusinessInformation();
                }
            }
        });

        //监听拉取分店订单列表
        this.viewModel.getQueryDeliveryRequiredBranchGoodsListResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                if (operateResult.getSuccess() != null) {
                    if (operateResult.getSuccess().getMessage() != null) {
                        BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.success), (String) operateResult.getSuccess().getMessage().obj,
                                new BruceDialog.OnAlertDialogListener() {
                                    @Override
                                    public void onSelect(boolean confirm) {

                                    }
                                });
                    }
                    //更新分店名字
                    choiceBranchCode = viewModel.getCurrentDeliveryRequiredBranchCode();
                    updateBranchID(choiceBranchCode);
                    updateBusinessInformation();
                }
                if (operateResult.getError() != null) {
                    BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.failed), operateResult.getError().getErrorMsg(),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {

                                }
                            });
                }
                if (pullRefreshListView1.isPullToRefreshing()) {
                    pullRefreshListView1.refreshFinish();
                }
                dismissProgressDialog();
            }
        });

        updateWarehouseName();
    }

    /**
     * 手动输入条码处理
     *
     * @param code 条码
     */
    private void handlerManualCodeInput(String code) {
        lastCode = code;
        Tool.hideSoftInput(this);
        queryCodeInformation(code);
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
                WarehouseShipmentActivity.this, WarehouseKeeper.getInstance().getBranchList(), new BruceDialog.OnInputFinishListener() {
                    @Override
                    public void onInputFinish(String itemName) {
                        if (TextUtils.isEmpty(itemName)) {
                            BruceDialog.showAlertDialog(WarehouseShipmentActivity.this, getString(R.string.hit),
                                    getString(R.string.no_choice_branch),
                                    new BruceDialog.OnAlertDialogListener() {
                                        @Override
                                        public void onSelect(boolean confirm) {

                                        }
                                    });
                        } else {
                            if (!choiceBranchCode.equals(itemName)) {
                                choiceBranchCode = itemName;
                                updateBranchID(choiceBranchCode);
                                queryBranchOrderList();
                            }
                        }
                    }
                });
    }

    /**
     * 查询分店订单列表
     */
    private void queryBranchOrderList() {
        showProgressDialog();
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

    /**
     * 库房选择
     */
    private void warehouseChoice() {
//        TreeNodeViewDialog.showTreeNodeViewDialog(WarehouseShipmentActivity.this, getString(R.string.warehouse_choice),
//                WarehouseKeeper.getInstance().getWarehouseTree(), false, new TreeNodeViewDialog.OnTreeNodeViewDialogListener() {
//                    @Override
//                    public void onChoice(List<TreeNode> node) {
//                        if (node != null && node.size() > 0) {
//                            if (node.size() == 1) {
//                                WarehouseKeeper.getInstance().setOnDutyWarehouse(node.get(0).name);
//                                updateWarehouseName();
//                                queryBranchOrderList();
//                            }
////                            updateStoreRoomId((node.size() == 1 ? node.get(0).name : node.get(0).name + "..."));
////                            for (TreeNode item : node) {
////                                viewModel.getQueryCondition().addStoreRoomID(item.id);
////                            }
//                        }
//                    }
//                });
        BruceDialog.showSingleChoiceDialog(R.string.warehouse_choice, this, WarehouseKeeper.getInstance().getWarehouseInformationList(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (TextUtils.isEmpty(itemName)) {
                    return;
                } else {
                    if (!itemName.equals(WarehouseKeeper.getInstance().getOnDutyWarehouse().name)) {
                        WarehouseKeeper.getInstance().setOnDutyWarehouse(itemName);
                        updateWarehouseName();
                        queryBranchOrderList();
                    }
                }
            }
        });
    }

    /**
     * 处理扫描结果--按键监听方式
     *
     * @param scan 扫描结果
     */
    private void handlerScanResultInKeyListen(String scan) {
//        String currCode;
//        if (!TextUtils.isEmpty(lastCode)) {
//            currCode = scan.substring(0, scan.length() - lastCode.length());
//        } else {
//            currCode = scan;
//        }
        codeEdit.setText("");
//        lastCode = currCode;
        queryCodeInformation(scan);
    }

    /**
     * 处理扫描结果--广播方式
     *
     * @param scan scan
     */
    private void handlerScanResultInBroadcast(String scan) {
        codeEdit.setText("");
//        lastCode = scan;
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
        showProgressDialog();
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
        String value = getString(R.string.branch_id) + "\u3000\u3000\u3000\u3000" + branchID;
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
        String value = getString(R.string.totalOrder);
        if (this.viewModel.getTotal() > 0) {
            value += ":" + this.viewModel.getTotal() + "\u3000" +
                    "<font color=\"green\">" + getString(R.string.scaned) + getString(R.string.colon_zh) + this.viewModel.getScanTotal() + "\u3000" + "</font>" +
                    "<font color=\"red\">" + getString(R.string.unScan) + getString(R.string.colon_zh) + (this.viewModel.getNotScanTotal()) + "</font>";
        }
        this.total.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_COMPACT));
        if (this.viewModel.getTotal() > 0 && !this.viewModel.isHaveInvalidGoods() && this.viewModel.getScanTotal() > 0) {
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
