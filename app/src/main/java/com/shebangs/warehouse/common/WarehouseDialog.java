package com.shebangs.warehouse.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import com.shebangs.warehouse.R;
import com.shebangs.warehouse.component.datetimepicker.CustomDatePicker;
import com.shebangs.warehouse.component.datetimepicker.DateFormatUtils;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

public class WarehouseDialog {

    /**
     * 在岗工作人员选择
     */
    public static void showOnDutyStaffChoiceDialog(Context context, OnSingleChoiceItemListener listener) {
        String[] values = WarehouseKeeper.getInstance().getStaffs();
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.on_duty_staff_choice))
                .setSingleChoiceItems(values, WarehouseKeeper.getInstance().getOnDutyStaffPosition(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WarehouseKeeper.getInstance().setOnDutyStaff(values[which]);
                        if (listener != null) {
                            listener.onChoiceItem(values[which]);
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * 在岗工作人员切换
     */
    public static void showOnDutyStaffChangeLoginDialog(Context context, OnSingleInputItemListener listener) {
        EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        editText.setHint(context.getString(R.string.input_password));
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.login))
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = editText.getText().toString();
                        if (listener != null) {
                            listener.onInputItem(value);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onNothing();
                        }
                    }
                })
                .create().show();
    }

    /**
     * 库房选择
     */
    public static void showWarehouseChoiceDialog(Context context, OnSingleChoiceItemListener listener) {
        String[] values = WarehouseKeeper.getInstance().getWarehouses();
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.warehouse_choice))
                .setSingleChoiceItems(values, WarehouseKeeper.getInstance().getOnDutyStaffPosition(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WarehouseKeeper.getInstance().setOnDutyStaff(values[which]);
                        if (listener != null) {
                            listener.onChoiceItem(values[which]);
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * 分店选择
     */
    public static void showAndChoiceDeliveryRequiredBranchDialog(Context context, String[] values, OnSingleChoiceItemListener listener){
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.branch_choice))
                .setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WarehouseKeeper.getInstance().setOnDutyStaff(values[which]);
                        if (listener != null) {
                            listener.onChoiceItem(values[which]);
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private static CustomDatePicker mTimerPicker;       //时间日期对话框
    /**
     * 显示时间日期对话框
     *
     * @param context  context
     * @param listener listener
     */
    public static void showDateTimePickerDialog(Context context, OnDateTimePickListener listener) {
        String beginTime = "1970-01-01 12:00";
        String nowTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        String endTime = DateFormatUtils.long2Str(System.currentTimeMillis() + 63072000000L, true);
        if (mTimerPicker == null) {
            // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
            mTimerPicker = new CustomDatePicker(context, new CustomDatePicker.Callback() {
                @Override
                public void onTimeSelected(long timestamp) {
                    if (listener != null) {
                        listener.OnDateTimePick(DateFormatUtils.long2Str(timestamp, true));
                    }
                    mTimerPicker.onDestroy();
                    mTimerPicker = null;
                }
            }, beginTime, endTime);
            // 允许点击屏幕或物理返回键关闭
            mTimerPicker.setCancelable(true);
            // 显示时和分
            mTimerPicker.setCanShowPreciseTime(true);
            // 允许循环滚动
            mTimerPicker.setScrollLoop(false);
            // 允许滚动动画
            mTimerPicker.setCanShowAnim(true);
        }
        mTimerPicker.show("yyyy-MM-dd HH:mm");
        mTimerPicker.setSelectedTime(nowTime, true);
    }

    //单选监听
    public interface OnSingleChoiceItemListener {
        void onChoiceItem(String value);
    }

    //单输入框监听
    public interface OnSingleInputItemListener {
        void onInputItem(String value);

        void onNothing();
    }

    /**
     * 时间日期选择返回
     */
    public interface OnDateTimePickListener {
        void OnDateTimePick(String dateTime);
    }
}
