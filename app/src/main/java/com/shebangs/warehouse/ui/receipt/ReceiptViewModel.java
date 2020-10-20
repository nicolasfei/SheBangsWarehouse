package com.shebangs.warehouse.ui.receipt;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.nicolas.toollibrary.Tool;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateError;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.data.OrderInformation;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.warehouse.WarehouseInterface;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReceiptViewModel extends ViewModel {
    private List<OrderInformation> goodsInformationList;
    private List<String> branchList;
    private boolean isHaveInvalidGoods;

    private ReceiptVoucher voucher;             //收货小票信息
    private ReceiptVoucher previousVoucher;     //上一张收货小票信息

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryCodeInformationResult;  //订单查询结果
    private MutableLiveData<OperateResult> submitBusinessDataResult;    //订单入库提交结果
    private MutableLiveData<OperateResult> queryPreviousBillResult;     //查询上一张收货小票结果

    public ReceiptViewModel() {
        this.queryCodeInformationResult = new MutableLiveData<>();
        this.inputFormState = new MutableLiveData<>();
        this.submitBusinessDataResult = new MutableLiveData<>();
        this.queryPreviousBillResult = new MutableLiveData<>();
        this.goodsInformationList = new ArrayList<>();
        this.branchList = new ArrayList<>();

        this.isHaveInvalidGoods = false;
    }

    LiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    LiveData<OperateResult> getQueryCodeInformationResult() {
        return queryCodeInformationResult;
    }

    LiveData<OperateResult> getSubmitBusinessDataResult() {
        return submitBusinessDataResult;
    }

    LiveData<OperateResult> getQueryPreviousBillResult() {
        return queryPreviousBillResult;
    }

    List<OrderInformation> getGoodsInformationList() {
        return goodsInformationList;
    }

    /**
     * 扫码查询订单
     *
     * @param code 订单条码
     */
    void queryCodeInformation(String code) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehousingQueryInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", code);
        parameters.put("storeRoomId", WarehouseKeeper.getInstance().getOnDutyWarehouse().id);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询上一张小票
     */
    public void queryPreviousBill() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehouseGoodsBillQueryInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("storeRoomId", WarehouseKeeper.getInstance().getOnDutyWarehouse().id);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 订单入库提交
     */
    void submitBusinessData() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehousingSubmitInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (OrderInformation information : goodsInformationList) {
            builder.append(information.id).append(",");
        }
        String arg = builder.toString();
        parameters.put("idList", arg.substring(0, arg.length() - 1));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                //扫描查询订单
                case WarehouseInterface.WarehousingQueryInterface:
                    if (!result.success) {
                        queryCodeInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        boolean repeatOrder = false;            //重复订单
                        boolean otherSupplierOrder = false;     //其他供货商订单
                        OrderInformation goods = new OrderInformation(result.data);
                        for (OrderInformation order : goodsInformationList) {
                            if (order.id.equals(goods.id)) {
                                repeatOrder = true;
                                break;
                            }
                            if (!(order.sId.equals(goods.sId))) {
                                otherSupplierOrder = true;
                                break;
                            }
                        }
                        if (repeatOrder) {
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.repeatOrder);
                            queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else if (otherSupplierOrder) {
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.otherSupplierOrder);
                            queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else {
                            goodsInformationList.add(goods);
                            branchList.add(goods.fId);
                            queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                        }
                    }
                    break;
                //查询上一张小票
                case WarehouseInterface.WarehouseGoodsBillQueryInterface:
                    if (!result.success) {
                        queryPreviousBillResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            //记录收货小票信息
                            ReceiptVoucher vouch = new ReceiptVoucher();
                            if (!TextUtils.isEmpty(result.data)) {
                                JSONArray array = new JSONArray(result.data);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = new JSONObject(array.getString(i));
                                    if (i == 0) {
                                        vouch.warehouse = object.getString("storeRoomName");
                                        vouch.supplier = object.getString("sId");
                                        vouch.date = object.getString("roomReceiveTime");
                                    }
                                    vouch.addFID(object.getString("fId"));
                                }
                            }
                            vouch.receiptStaff = result.receiveName;
                            vouch.code = result.receiptId;
                            Log.d("--->", "execResult: receiptId is " + vouch.code);
                            voucher = vouch;
                            previousVoucher = voucher;              //备份上一张小票
                            queryPreviousBillResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            queryPreviousBillResult.setValue(new OperateResult(new OperateError(-1,
                                    WarehouseApp.getInstance().getString(R.string.code_format_error), null)));
                        }
                    }
                    break;
                //订单入库提交
                case WarehouseInterface.WarehousingSubmitInterface:
                    if (!result.success) {
                        submitBusinessDataResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        //获取提交结果，并处理
                        //记录收货小票信息
                        previousVoucher = voucher;              //备份上一张小票
                        voucher = new ReceiptVoucher();
                        voucher.receiptStaff = WarehouseKeeper.getInstance().getOnDutyStaffName();
                        voucher.warehouse = WarehouseKeeper.getInstance().getOnDutyWarehouse().pId;
                        voucher.supplier = goodsInformationList.get(0).sId;
                        voucher.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
                        voucher.code = result.receiptId;
                        voucher.fIDs.addAll(branchList);
                        submitBusinessDataResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 清空已扫描的订单
     */
    void clearGoodsInformation() {
        //清空已扫描的订单
        goodsInformationList.clear();
        branchList.clear();
    }

    /**
     * 获取收货小票信息
     *
     * @return 收货小票信息
     */
    ReceiptVoucher getReceiptVoucher() {
        return voucher;
    }

    /**
     * 获取上一张小票信息
     *
     * @return 上一张小票信息
     */
    ReceiptVoucher getPreviousReceiptVoucher() {
        return previousVoucher;
    }

    /**
     * 条码输出框发生变化，检查是否格式有错误
     *
     * @param code 条码
     */
    void codeDataChanged(String code) {
        if (Tool.isOrderCodeValid(code)) {
            inputFormState.setValue(new InputFormState(true));
        } else {
            inputFormState.setValue(new InputFormState(WarehouseApp.getInstance().getString(R.string.code_format_error)));
        }
    }

    int getTotal() {
        return goodsInformationList.size();
    }

    /**
     * 删除商品
     *
     * @param position 商品位置
     */
    void deleteGoods(int position) {
        OrderInformation item = this.goodsInformationList.get(position);
        for (int i = 0; i < branchList.size(); i++) {
            if (branchList.get(i).equals(item.fId)) {
                this.branchList.remove(i);
                break;
            }
        }
        this.goodsInformationList.remove(position);
        this.queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    /**
     * 商品信息中是否含有无效或者错误的商品信息
     *
     * @return yes/no
     */
    boolean isHaveInvalidGoods() {
        return this.isHaveInvalidGoods;
    }

    /**
     * 清空条件
     */
    void resetCondition() {
//        this.businessTime = "";
    }

    /**
     * 清空数据
     */
    void clearReceiptVoucherData() {
        this.voucher = null;
        this.previousVoucher = null;
    }

    /**
     * 是否还有未处理的数据
     *
     * @return yes/no
     */
    public boolean isHavDataNotProcessed() {
        return goodsInformationList.size() > 0;
    }
}
