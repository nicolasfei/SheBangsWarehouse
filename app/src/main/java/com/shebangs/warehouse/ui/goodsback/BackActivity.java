package com.shebangs.warehouse.ui.goodsback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.nicolas.printerlibraryforufovo.PrinterManager;
import com.nicolas.scannerlibrary.PPdaScanner;
import com.nicolas.scannerlibrary.Scanner;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Tool;
import com.nicolas.toollibrary.Utils;
import com.nicolas.toollibrary.VibratorUtil;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.data.OrderInformationOfBackGoodsAdapter;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

public class BackActivity extends BaseActivity implements View.OnClickListener {
    private TextView warehouseName;
    private EditText codeEdit;
    private Scanner scanner;                //扫描头
    private TextView total;
    private Button submit;

    private OrderInformationOfBackGoodsAdapter adapter;
    private BackViewModel viewModel;

    private String lastCode = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_back);

        //viewModel
        this.viewModel = new ViewModelProvider(this).get(BackViewModel.class);
        //库房
        this.warehouseName = findClickView(R.id.warehouseName);
        this.warehouseName.setClickable(true);
        this.updateWarehouseName();

        //初始化扫描头
        scanner = new PPdaScanner(this);
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
            @Override
            public void scanResult(String scan) {
                handlerScanResultInBroadcast(scan);
            }
        });

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

        //订单信息展示swipeMenuListView
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
                deleteItem.setWidth(Tool.dp2px(BackActivity.this, 90));
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
        this.adapter = new OrderInformationOfBackGoodsAdapter(this, viewModel.getGoodsInformationOfBackGoodsList());
        swipeMenuListView.setAdapter(this.adapter);

        //合计
        this.total = findViewById(R.id.total);
        //提交
        this.submit = findClickView(R.id.submit);
        this.submit.setClickable(false);


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
                    if (operateResult.getSuccess().getMessage() != null) {
                        VibratorUtil.getInstance().showWarning();
                        BruceDialog.showPromptDialog(BackActivity.this, (String) operateResult.getSuccess().getMessage().obj);
                    } else {
                        updateBusinessInformation();
                    }
                }
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(BackActivity.this, operateResult.getError().getErrorMsg());
                }
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
                    BruceDialog.showPromptDialog(BackActivity.this, operateResult.getError().getErrorMsg());
                }
                if (operateResult.getSuccess() != null) {
                    //提交后处理
                    updateBusinessInformation();
                    Utils.toast(BackActivity.this, BackActivity.this.getString(R.string.submit_success));
                }
            }
        });

        updateBusinessInformation();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (this.viewModel.isHavDataNotProcessed()) {
                    BruceDialog.showAlertDialog(this, getString(R.string.warning), getString(R.string.isHavDataNotProcessed),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {
                                    if (confirm) {
                                        BackActivity.this.finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warehouseName:
                warehouseChoice();
                break;
            case R.id.submit:
                BruceDialog.showAlertDialog(BackActivity.this, getString(R.string.nav_goodsback), getString(R.string.submit_sure), new BruceDialog.OnAlertDialogListener() {
                    @Override
                    public void onSelect(boolean confirm) {
                        if (confirm) {
                            BruceDialog.showProgressDialog(BackActivity.this, getString(R.string.submitting));
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
     * 处理条码扫描结果--按键监听方式
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
     * 处理条码扫描结果--广播方式
     *
     * @param scan scan
     */
    private void handlerScanResultInBroadcast(String scan) {
        codeEdit.setText("");
//        lastCode = scan;
        Tool.hideSoftInput(this);
        queryCodeInformation(scan);
    }

    /**
     * 处理条码输入--手动输入条码处理
     *
     * @param code 条码
     */
    private void handlerManualCodeInput(String code) {
        lastCode = code;
        Tool.hideSoftInput(this);
        queryCodeInformation(code);
    }

    /**
     * 查条码
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
        String value = getString(R.string.warehouse_name) + "\u3000\u3000" + WarehouseKeeper.getInstance().getOnDutyWarehouse().name + "/" +
                WarehouseKeeper.getInstance().getOnDutyStaffName();
        this.warehouseName.setText(value);
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
     * 库房选择
     */
    private void warehouseChoice() {
        BruceDialog.showSingleChoiceDialog(R.string.warehouse_choice, this, WarehouseKeeper.getInstance().getWarehouseInformationList(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (!TextUtils.isEmpty(itemName)) {
                    if (!itemName.equals(WarehouseKeeper.getInstance().getOnDutyWarehouse().name)) {
//                        if (viewModel.isHavDataNotProcessed()) {
//                            BruceDialog.showAlertDialog(BackActivity.this, getString(R.string.warning),
//                                    getString(R.string.switchWillClearData), new BruceDialog.OnAlertDialogListener() {
//                                        @Override
//                                        public void onSelect(boolean confirm) {
//                                            viewModel.clearData();
//                                            switchWarehouse(itemName);
//                                            updateBusinessInformation();
//                                        }
//                                    });
//                        } else {
                        switchWarehouse(itemName);
//                        }
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
        //更新库房
        WarehouseKeeper.getInstance().setOnDutyWarehouse(itemName);
        //更新打印机
        PrinterManager.getInstance().resetLinkDeviceModel(itemName);
        //更新库房名显示
        updateWarehouseName();
    }

    /**
     * 更新商品信息
     */
    private void updateBusinessInformation() {
        //更新商品信息
        this.adapter.notifyDataSetChanged();
        //合计更新
        String value = String.valueOf(this.viewModel.getTotal());
        this.total.setText(value);
        if (this.viewModel.getTotal() > 0 && !this.viewModel.isHaveInvalidGoods()) {
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
