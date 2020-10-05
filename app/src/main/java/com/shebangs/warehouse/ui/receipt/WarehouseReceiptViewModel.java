package com.shebangs.warehouse.ui.receipt;

import android.os.Message;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WarehouseReceiptViewModel extends ViewModel {
    private int total;
    private float totalPrice;
    private List<OrderInformation> goodsInformationList;
    private List<String> branchList;
    private boolean isHaveInvalidGoods;

    private ReceiptVoucher voucher;             //收货小票信息
    private ReceiptVoucher previousVoucher;     //上一张收货小票信息

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryCodeInformationResult;  //订单查询结果
    private MutableLiveData<OperateResult> submitBusinessDataResult;    //订单入库提交结果

    public WarehouseReceiptViewModel() {
        this.queryCodeInformationResult = new MutableLiveData<>();
        this.inputFormState = new MutableLiveData<>();
        this.submitBusinessDataResult = new MutableLiveData<>();
        this.goodsInformationList = new ArrayList<>();
        this.branchList = new ArrayList<>();

        this.isHaveInvalidGoods = false;
    }

    public LiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    public LiveData<OperateResult> getQueryCodeInformationResult() {
        return queryCodeInformationResult;
    }

    public LiveData<OperateResult> getSubmitBusinessDataResult() {
        return submitBusinessDataResult;
    }

    public List<OrderInformation> getGoodsInformationList() {
        return goodsInformationList;
    }

    /**
     * 扫码查询订单
     *
     * @param code 订单条码
     */
    public void queryCodeInformation(String code) {
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
     * 订单入库提交
     */
    public void submitBusinessData() {
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
                        boolean repeatOrder = false;        //重复订单
                        boolean otherSupplierOrder = false;   //其他供货商订单
                        OrderInformation goods = new OrderInformation(result.data);
                        for (OrderInformation order : goodsInformationList) {
                            if (order.id.equals(goods.id)) {
                                repeatOrder = true;
                                break;
                            }
                            if (!order.supplierId.equals(goods.supplierId)) {
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
                            total++;
                            queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
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
                        voucher.date = new SimpleDateFormat("yyyy年MM月DD日", Locale.CHINA).format(new Date());
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
    public void clearGoodsInformation() {
        //清空已扫描的订单
        goodsInformationList.clear();
        branchList.clear();
        total = 0;
    }

    /**
     * 获取收货小票信息
     *
     * @return 收货小票信息
     */
    public ReceiptVoucher getReceiptVoucher() {
        return voucher;
    }

    /**
     * 获取上一张小票信息
     *
     * @return 上一张小票信息
     */
    public ReceiptVoucher getPreviousReceiptVoucher() {
        return previousVoucher;
    }

    /**
     * 条码输出框发生变化，检查是否格式有错误
     *
     * @param code 条码
     */
    public void codeDataChanged(String code) {
        if (Tool.isOrderCodeValid(code)) {
            inputFormState.setValue(new InputFormState(true));
        } else {
            inputFormState.setValue(new InputFormState(WarehouseApp.getInstance().getString(R.string.code_format_error)));
        }
    }

    public int getTotal() {
        return total;
    }

    public String getTotalPrice() {
        return Tool.float2StringFor2Decimals(totalPrice);
    }

    /**
     * 删除商品
     *
     * @param position 商品位置
     */
    public void deleteGoods(int position) {
        OrderInformation item = this.goodsInformationList.get(position);
        this.total -= item.originalPrice;
        this.totalPrice -= item.orderPrice;
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
    public boolean isHaveInvalidGoods() {
        return this.isHaveInvalidGoods;
    }

    /**
     * 清空条件
     */
    public void resetCondition() {
//        this.businessTime = "";
    }
}
