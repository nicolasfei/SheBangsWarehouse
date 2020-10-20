package com.shebangs.warehouse.ui.receipt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.shebangs.warehouse.component.ReceiveBillView;
import com.shebangs.warehouse.data.OrderInformationAdapter;
import com.shebangs.warehouse.ui.set.printer.PrinterActivity;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import java.io.IOException;
import java.util.Vector;

/**
 * 收货增加分店对比功能--以第一个扫描的分店为准，如果后面的订单的分店号不是第一个订单的分店号则报错
 */
public class ReceiptActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "ReceiptActivity";
    private TextView warehouseName;
    private EditText codeEdit;
    private Scanner scanner;                //扫描头
    private TextView total;
    private Button submit;

    private DrawerLayout drawerLayout;

    private ReceiptViewModel viewModel;
    private OrderInformationAdapter adapter;
    private SwipeMenuListView swipeMenuListView;

    //打印机页面请求码
    private static final int LINK_PRINTER = 1;
    private static final int SET_PRINTER = 2;

    private String lastCode = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse_receipt);

        this.viewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);

        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //库房
        this.warehouseName = findClickView(R.id.warehouseName);
        this.warehouseName.setClickable(true);

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
        scanner = new PPdaScanner(this);
        scanner.setOnScannerScanResultListener(new Scanner.OnScannerScanResultListener() {
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
        this.submit = findClickView(R.id.submit);
        this.submit.setClickable(false);

        //SwipeMenuListView
        this.swipeMenuListView = findViewById(R.id.swipeMenuListView);
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
                deleteItem.setWidth(Tool.dp2px(ReceiptActivity.this, 90));
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
        this.swipeMenuListView.setMenuCreator(creator);
        this.swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
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
        this.adapter = new OrderInformationAdapter(this, viewModel.getGoodsInformationList());
        this.swipeMenuListView.setAdapter(this.adapter);

        findClickView(R.id.printPrevious);      //打印上一张订单

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

        //监听条码查询结果
        this.viewModel.getQueryCodeInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    if (operateResult.getSuccess().getMessage() != null) {
                        VibratorUtil.getInstance().showWarning();
                        BruceDialog.showPromptDialog(ReceiptActivity.this, (String) operateResult.getSuccess().getMessage().obj);
                    } else {
                        updateBusinessInformation();
                    }
                }
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(ReceiptActivity.this, operateResult.getError().getErrorMsg());
                }
                codeEdit.setText("");
                codeEdit.requestFocus();        //codeEdit获取焦点
                codeEdit.setSelection(0);       //codeEdit光标移到起始位置
            }
        });

        //监听查询上一张收货小票结果
        this.viewModel.getQueryPreviousBillResult().observe(ReceiptActivity.this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    printReceiveBill();
                }
                if (operateResult.getError() != null) {
                    VibratorUtil.getInstance().showWarning();
                    BruceDialog.showPromptDialog(ReceiptActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });

        //监听数据提交结果
        this.viewModel.getSubmitBusinessDataResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getError() != null) {
                    BruceDialog.showPromptDialog(ReceiptActivity.this, operateResult.getError().getErrorMsg());
                }
                if (operateResult.getSuccess() != null) {
                    //提交后处理
                    submitBusinessDataSuccess();
                }
            }
        });

        updateWarehouseName();
    }

    /**
     * 数据提交成功
     */
    private void submitBusinessDataSuccess() {
        //更新数据
        submitDataAndUpdate();
        //打印收货小票
        printReceiveBill();
    }

    /**
     * 更新提交数据
     */
    private void submitDataAndUpdate() {
        //更新数据
        viewModel.clearGoodsInformation();
        updateBusinessInformation();
        Utils.toast(ReceiptActivity.this, ReceiptActivity.this.getString(R.string.submit_success));
    }

    /**
     * 打印小票
     */
    private void printReceiveBill() {
        Vector<Byte> vector;
        ReceiptVoucher voucher = viewModel.getReceiptVoucher();
        if (voucher == null || voucher.getPrintBill() == null) {
            BruceDialog.showPromptDialog(this, getString(R.string.getBillError));
            return;
        }
        vector = voucher.getPrintBill();
        if (PrinterManager.getInstance().isLinkedPrinter()) {
            try {
                PrinterManager.getInstance().printBill(vector);
                Utils.toast(this, getString(R.string.print_bill_success));
            } catch (IOException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.printer_no_link)
                        .setMessage(getString(R.string.jump_to_printer_set))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                jumpToPrinterActivity(LINK_PRINTER);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BruceDialog.showPromptDialog(ReceiptActivity.this, getString(R.string.print_default_nolink));
                            }
                        })
                        .create().show();
            }
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.printer_no_link)
                    .setMessage(getString(R.string.jump_to_printer_set))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            jumpToPrinterActivity(LINK_PRINTER);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BruceDialog.showPromptDialog(ReceiptActivity.this, getString(R.string.print_default_nolink));
                        }
                    })
                    .create().show();
        }
    }

    /**
     * 跳转到打印机设置页面
     */
    private void jumpToPrinterActivity(int requestCode) {
        Intent intent = new Intent(this, PrinterActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SET_PRINTER:
                break;
            case LINK_PRINTER:
                printReceiveBill();
                break;
            default:
                break;
        }
    }

    /**
     * 打印收货小票对话框
     */
    private void showPrintReceiveBillDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.receive_print_title)
                .setMessage(R.string.receive_print)
                .setView(new ReceiveBillView(ReceiptActivity.this, viewModel.getReceiptVoucher()))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printReceiveBill();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submitDataAndUpdate();
                    }
                })
                .create().show();
    }

    /**
     * 处理扫描结果--广播方式
     *
     * @param scan scan
     */
    private void handlerScanResultInBroadcast(String scan) {
//        lastCode = scan;
        queryCodeInformation(scan);
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
//        codeEdit.setText(currCode);
//        lastCode = currCode;
        queryCodeInformation(scan);
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

    /**
     * 条件确认
     */
    private void conditionSubmit() {
        this.drawerLayout.closeDrawer(Gravity.RIGHT, true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.receipt_menu, menu);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.app_bar_search));
//        searchView.setQueryHint(getString(R.string.search_hint));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_printer:
                jumpToPrinterActivity(SET_PRINTER);
                break;
            case R.id.menu_more:
                this.drawerLayout.openDrawer(GravityCompat.END);
                break;
            case android.R.id.home:
                if (this.viewModel.isHavDataNotProcessed()) {
                    BruceDialog.showAlertDialog(this, getString(R.string.warning), getString(R.string.isHavDataNotProcessed),
                            new BruceDialog.OnAlertDialogListener() {
                                @Override
                                public void onSelect(boolean confirm) {
                                    if (confirm) {
                                        ReceiptActivity.this.finish();
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
     * 删除商品
     *
     * @param position pos
     */
    private void deleteGoods(int position) {
        this.viewModel.deleteGoods(position);
        this.updateBusinessInformation();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.warehouse_operate_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        super.onOptionsItemSelected(item);
//        switch (item.getItemId()) {
//            case R.id.menu_query:
//                this.drawerLayout.openDrawer(GravityCompat.END);
//                break;
//            default:
//                break;
//        }
//        return true;
//    }


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
                    if (!itemName.equals(WarehouseKeeper.getInstance().getOnDutyWarehouse().name)) {
                        if (viewModel.getGoodsInformationList().size() > 0) {
                            BruceDialog.showAlertDialog(ReceiptActivity.this, getString(R.string.warning), getString(R.string.receipt_not_submit),
                                    new BruceDialog.OnAlertDialogListener() {
                                        @Override
                                        public void onSelect(boolean confirm) {
                                            if (confirm) {
                                                switchWarehouse(itemName);
                                            }
                                        }
                                    });
                        } else {
                            switchWarehouse(itemName);
                        }
                    }
                }
            }
        });
    }

    /**
     * 切换库房
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
        //清空上一张小票备份
        viewModel.clearReceiptVoucherData();
        //清空商品数据
        viewModel.clearGoodsInformation();
        //更新商品信息
        updateBusinessInformation();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warehouseName:        //选择库房
                warehouseChoice();
                break;
            case R.id.submit:
                BruceDialog.showAlertDialog(ReceiptActivity.this, getString(R.string.receipt), getString(R.string.submit_sure), new BruceDialog.OnAlertDialogListener() {
                    @Override
                    public void onSelect(boolean confirm) {
                        if (confirm) {
                            BruceDialog.showProgressDialog(ReceiptActivity.this, getString(R.string.submitting));
                            viewModel.submitBusinessData();
                        }
                    }
                });
                break;
            case R.id.printPrevious:
                printPreviousBill();
                break;
            default:
                break;
        }
    }

    /**
     * 打印上一张小票
     */
    private void printPreviousBill() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(Gravity.RIGHT, true);
        }
        ReceiptVoucher voucher = viewModel.getReceiptVoucher();
        if (voucher == null || voucher.getPrintBill() == null || voucher.fIDs.size() == 0) {
            BruceDialog.showProgressDialog(this, getString(R.string.previous_bill_query));
            viewModel.queryPreviousBill();
        } else {
            printReceiveBill();
        }
    }

    /**
     * 更新商品信息
     */
    private void updateBusinessInformation() {
        //更新商品信息
        this.adapter.notifyDataSetChanged();
        //合计更新
        String value = getString(R.string.totalOrder) + ":" + this.viewModel.getTotal();
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
//        scanner.scannerClose();
        viewModel.clearReceiptVoucherData();
        super.onDestroy();
    }
}
