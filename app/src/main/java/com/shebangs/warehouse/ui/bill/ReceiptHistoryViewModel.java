package com.shebangs.warehouse.ui.bill;

import android.os.Message;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
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

import java.util.HashMap;
import java.util.Map;

public class ReceiptHistoryViewModel extends ViewModel {

    private MutableLiveData<OperateResult> queryReceiptHistoryResult;   //收货历史小票查询结果

    public void receiptHistoryQuery() {
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

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                //收货历史小票查询结果
                case WarehouseInterface.WarehouseGoodsBillQueryInterface:
                    if (!result.success) {
                        queryReceiptHistoryResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        boolean repeatOrder = false;
                        OrderInformationOfBackGoods goods = new OrderInformationOfBackGoods(result.data);
//                        for (OrderInformationOfBackGoods order : goodsInformationList) {
//                            if (order.id.equals(goods.id)) {
//                                repeatOrder = true;
//                                break;
//                            }
//                        }
//                        if (repeatOrder) {
//                            Message msg = new Message();
//                            msg.obj = WarehouseApp.getInstance().getString(R.string.repeatOrder);
//                            queryReceiptHistoryResult.setValue(new OperateResult(new OperateInUserView(msg)));
//                        } else {
//                            goodsInformationList.add(goods);
//                            queryReceiptHistoryResult.setValue(new OperateResult(new OperateInUserView(null)));
//                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
