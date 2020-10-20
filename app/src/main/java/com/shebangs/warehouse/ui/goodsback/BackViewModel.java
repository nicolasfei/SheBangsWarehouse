package com.shebangs.warehouse.ui.goodsback;

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
import com.shebangs.warehouse.data.OrderInformationOfBackGoods;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.warehouse.WarehouseInterface;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackViewModel extends ViewModel {

    private static final int MAX_BACK_GOODS = 50;
    private List<OrderInformationOfBackGoods> goodsInformationList;
    private boolean isHaveInvalidGoods;

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryCodeInformationResult;  //订单查询结果
    private MutableLiveData<OperateResult> submitBusinessDataResult;    //订单入库提交结果

    public BackViewModel() {
        this.queryCodeInformationResult = new MutableLiveData<>();
        this.inputFormState = new MutableLiveData<>();
        this.submitBusinessDataResult = new MutableLiveData<>();
        this.goodsInformationList = new ArrayList<>();

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

    public List<OrderInformationOfBackGoods> getGoodsInformationOfBackGoodsList() {
        return goodsInformationList;
    }

    /**
     * 扫码查询条码
     *
     * @param code 条码
     */
    public void queryCodeInformation(String code) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehouseGoodsBackQueryInterface;
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
     * 订单返货提交
     */
    public void submitBusinessData() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehouseGoodsBackSubmitInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (OrderInformationOfBackGoods information : goodsInformationList) {
            builder.append(information.id).append(",");
        }
        String arg = builder.toString();
        parameters.put("idAll", arg.substring(0, arg.length() - 1));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 删除商品信息
     *
     * @param position 商品信息pos
     */
    public void deleteGoods(int position) {
        this.goodsInformationList.remove(position);
        this.queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
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

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                //扫描查询订单
                case WarehouseInterface.WarehouseGoodsBackQueryInterface:
                    if (!result.success) {
                        queryCodeInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        boolean repeatOrder = false;
                        OrderInformationOfBackGoods goods = new OrderInformationOfBackGoods(result.data);
                        for (OrderInformationOfBackGoods order : goodsInformationList) {
                            if (order.id.equals(goods.id)) {
                                repeatOrder = true;
                                break;
                            }
                        }
                        if (repeatOrder) {
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.repeatOrder);
                            queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else {
                            if (goodsInformationList.size() > MAX_BACK_GOODS) {
                                queryCodeInformationResult.setValue(new OperateResult(new OperateError(-1,
                                        WarehouseApp.getInstance().getString(R.string.maxBackGoods), null)));
                            } else {
                                goodsInformationList.add(goods);
                                queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        }
                    }
                    break;
                //订单返货提交
                case WarehouseInterface.WarehouseGoodsBackSubmitInterface:
                    if (!result.success) {
                        submitBusinessDataResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        //获取提交结果，并处理
                        //？？？？
                        goodsInformationList.clear();   //清空已扫描的订单
                        isHaveInvalidGoods = false;
                        submitBusinessDataResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public int getTotal() {
        return this.goodsInformationList.size();
    }

    public boolean isHaveInvalidGoods() {
        return isHaveInvalidGoods;
    }

    public boolean isHavDataNotProcessed() {
        return goodsInformationList.size() > 0;
    }

    public void clearData() {
        goodsInformationList.clear();   //清空已扫描的订单
        isHaveInvalidGoods = false;
    }
}
