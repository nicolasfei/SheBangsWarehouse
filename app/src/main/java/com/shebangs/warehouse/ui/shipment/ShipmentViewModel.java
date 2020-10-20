package com.shebangs.warehouse.ui.shipment;

import android.os.Message;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateError;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.data.OrderInformation;
import com.shebangs.warehouse.data.ShipmentGoodsStatistics;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.warehouse.WarehouseInterface;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShipmentViewModel extends ViewModel {
    private static final String TAG = "WarehouseShipmentViewModel";

    private String currentDeliveryRequiredBranchID;       //当前须出库的分店ID--用于查询条码
    private String currentDeliveryRequiredBranchCode;       //当前须出库的分店的code

    private List<OrderInformation> branchOrders;            //待扫描分店订单列表
    private List<OrderInformation> scannedBranchOrders;     //已扫描分店订单列表

    private List<ShipmentGoodsStatistics> scanStatistics;           //订单统计--未扫描
    private List<ShipmentGoodsStatistics> scannedStatistics;        //订单统计--已扫描

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryCodeInformationResult;
    private MutableLiveData<OperateResult> submitBusinessDataResult;
    private MutableLiveData<OperateResult> queryDeliveryRequiredBranchGoodsListResult;

    private String scanCodeFormQueryBranchList = "";        //通过扫码查询代发货的清单的条码
    private boolean refresh = false;            //是否是刷新数据

    public ShipmentViewModel() {
        this.queryCodeInformationResult = new MutableLiveData<>();
        this.inputFormState = new MutableLiveData<>();
        this.submitBusinessDataResult = new MutableLiveData<>();
        this.queryDeliveryRequiredBranchGoodsListResult = new MutableLiveData<>();
        this.branchOrders = new ArrayList<>();
        this.scannedBranchOrders = new ArrayList<>();
        this.scanStatistics = new ArrayList<>();
        this.scannedStatistics = new ArrayList<>();
    }

    LiveData<OperateResult> getQueryDeliveryRequiredBranchGoodsListResult() {
        return queryDeliveryRequiredBranchGoodsListResult;
    }

    List<ShipmentGoodsStatistics> getScannedStatistics() {
        return scannedStatistics;
    }

    List<ShipmentGoodsStatistics> getScanStatistics() {
        return scanStatistics;
    }

    String getCurrentDeliveryRequiredBranchCode() {
        return currentDeliveryRequiredBranchCode;
    }

    LiveData<OperateResult> getSubmitBusinessDataResult() {
        return submitBusinessDataResult;
    }

    LiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    LiveData<OperateResult> getQueryCodeInformationResult() {
        return queryCodeInformationResult;
    }

    List<OrderInformation> getGoodsInformationList() {
        return branchOrders;
    }

    List<OrderInformation> getScannedBranchOrders() {
        return scannedBranchOrders;
    }

    /**
     * 处理条码输入
     * 如果第一次扫描，这是查询该条码上的分店的代发货清单
     * 否则是查询条码信息
     *
     * @param code 条码
     */
    void queryCodeInformation(String code) {
        //检查分店是否有需要发的货物
        if (this.branchOrders.size() == 0 && this.scannedBranchOrders.size() == 0) {
            //查询分店发货清单
            queryBranchShipmentListFormCode(code);
        } else {
            //查询商品
            queryGoodsInformation(code);
        }
    }

    /**
     * 通过扫码方式查询分店待发货清单
     *
     * @param code 条码
     */
    private void queryBranchShipmentListFormCode(String code) {
        //代发货清单查询
        this.scanCodeFormQueryBranchList = code;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.EX_WarehouseBranchGoodsListFormCodeInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", code);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 查询商品信息
     *
     * @param code 条码
     */
    private void queryGoodsInformation(String code) {
        //查询商品
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.EX_WarehouseQueryInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", code);
        parameters.put("storeRoomId", WarehouseKeeper.getInstance().getOnDutyWarehouse().id);
        parameters.put("branchId", currentDeliveryRequiredBranchID);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 出库订单提交
     */
    void submitBusinessData() {
        if (scannedBranchOrders.size() == 0) {
            return;
        }
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.EX_WarehouseSubmitInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (OrderInformation item : scannedBranchOrders) {
            builder.append(item.id).append(",");
        }
        String arg = builder.toString();
        parameters.put("idAll", arg.substring(0, arg.length() - 1));
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 根据分店来查询须发货订单
     */
    void queryDeliveryRequiredBranch(String branchCode) {
        String branchId = WarehouseKeeper.getInstance().getBranchID(branchCode);
        if (TextUtils.isEmpty(branchId)) {
            queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateError(-1,
                    WarehouseApp.getInstance().getString(R.string.branch_id_error), null)));
        } else {
            this.currentDeliveryRequiredBranchID = branchId;
            this.currentDeliveryRequiredBranchCode = branchCode;
            queryBranchShipmentOrder();
        }
    }

    /**
     * 查询分店待发货订单
     */
    private void queryBranchShipmentOrder() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.EX_WarehouseBranchGoodsListInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", "1");
        parameters.put("pageSize", "10000");
        parameters.put("pageCount", "0");
        parameters.put("branchId", this.currentDeliveryRequiredBranchID);
        parameters.put("inState", "roomreceive");
        parameters.put("storeRoomId", WarehouseKeeper.getInstance().getOnDutyWarehouse().id);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                //查询出库订单
                case WarehouseInterface.EX_WarehouseQueryInterface:
                    if (result.success) {
                        boolean isRepeatScan = false;
                        boolean isThisBranchId = false;
                        OrderInformation order = new OrderInformation(result.data);
                        for (int i = 0; i < branchOrders.size(); i++) {
                            OrderInformation item = branchOrders.get(i);
                            //在待扫描列表中找到了此订单
                            if (order.id.equals(item.id)) {
                                isThisBranchId = true;      //本分店订单

                                //1）此订单加入到已扫描订单列表中
                                order.isExWarehouseScan = true;
                                scannedBranchOrders.add(order);
                                //2）已扫描统计订单列表中加入此订单的统计
                                boolean find = false;
                                for (ShipmentGoodsStatistics s : scannedStatistics) {
                                    if (order.sId.equals(s.supplier)) {     //订单上的供应商已经包含在统计中
                                        s.statistic++;
                                        find = true;
                                        break;
                                    }
                                }
                                if (!find) {
                                    //添加新的供应商统计
                                    scannedStatistics.add(new ShipmentGoodsStatistics(order.sId, 1, false));
                                }

                                //3）待扫描订单列表删除itm
                                branchOrders.remove(i);
                                //4）待扫描订单统计列表减去此订单
                                for (int k = 0; k < scanStatistics.size(); k++) {
                                    ShipmentGoodsStatistics s = scanStatistics.get(k);
                                    if (order.sId.equals(s.supplier)) {
                                        s.statistic--;
                                        //统计为0时，删除这个统计
                                        if (s.statistic == 0) {
                                            scanStatistics.remove(k);
                                        }
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        //不是本分店的订单，直接返回
                        if (!isThisBranchId) {
                            queryCodeInformationResult.setValue(new OperateResult(new OperateError(-1,
                                    WarehouseApp.getInstance().getString(R.string.notThisBranchOrder), null)));
                            return;
                        }

                        //检测是否是重复订单
                        for (OrderInformation item : scannedBranchOrders) {
                            if (item.id.equals(order.id)) {
                                isRepeatScan = true;
                                break;
                            }
                        }
                        //重复订单直接返回
                        if (isRepeatScan) {
                            queryCodeInformationResult.setValue(new OperateResult(new OperateError(-1,
                                    WarehouseApp.getInstance().getString(R.string.repeatOrder), null)));
                            return;
                        }

                        //扫描到新订单成功
                        queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                    } else {
                        queryCodeInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                //订单出库提交
                case WarehouseInterface.EX_WarehouseSubmitInterface:
                    if (!result.success) {
                        submitBusinessDataResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        updateSubmitSuccess();
                        submitBusinessDataResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                //通过选择分店来查询分店订单列表
                case WarehouseInterface.EX_WarehouseBranchGoodsListInterface:
                    if (!result.success) {
                        queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        //先清空数据
                        branchOrders.clear();
                        scanStatistics.clear();
                        scannedStatistics.clear();
                        scannedBranchOrders.clear();
                        try {
                            JSONArray array = new JSONArray(result.data);
                            //分店无需发货，直接返回
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = WarehouseApp.getInstance().getString(R.string.branch_not_delivery_required);
                                queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                                return;
                            }
                            //解析数据
                            for (int i = 0; i < array.length(); i++) {
                                OrderInformation order = new OrderInformation(array.getString(i));
                                branchOrders.add(order);            //待发货列表添加订单

                                //待发货统计列表添加此订单
                                boolean find = false;
                                for (ShipmentGoodsStatistics s : scanStatistics) {
                                    if (s.supplier.equals(order.sId)) {
                                        s.statistic++;
                                        find = true;
                                        break;
                                    }
                                }
                                if (!find) {
                                    ShipmentGoodsStatistics ns = new ShipmentGoodsStatistics(order.sId, 1, true);
                                    scanStatistics.add(ns);
                                }
                            }
                            currentDeliveryRequiredBranchCode = branchOrders.get(0).fId;
                            queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.errorData);
                            queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        }
                    }
                    break;
                case WarehouseInterface.EX_WarehouseBranchGoodsListFormCodeInterface:       //通过条码查询分店须发货物清单
                    if (!result.success) {
                        queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            //先清空之前的数据
                            branchOrders.clear();
                            scanStatistics.clear();
                            scannedStatistics.clear();
                            scannedBranchOrders.clear();

                            JSONArray array = new JSONArray(result.data);
                            //分店没有待发货的货
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = WarehouseApp.getInstance().getString(R.string.branch_not_delivery_required);
                                queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                                return;
                            }
                            //判断是否是本库房的待发货的分店
                            OrderInformation test = new OrderInformation(array.getString(0));
                            if (!test.storeRoomId.equals(WarehouseKeeper.getInstance().getOnDutyWarehouse().id)) {
                                //此分店不属于本库房
                                Message msg = new Message();
                                msg.obj = WarehouseApp.getInstance().getString(R.string.branch_not_this_storeRoom);
                                queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                                return;
                            }
                            currentDeliveryRequiredBranchID = test.branchId;      //记录分店ID，用于扫码
                            currentDeliveryRequiredBranchCode = test.fId;
                            //解析发货清单
                            for (int i = 0; i < array.length(); i++) {
                                OrderInformation shipOrder = new OrderInformation(array.getString(i));
                                if (shipOrder.id.equals(scanCodeFormQueryBranchList)) {     //这个已经扫码的订单加入已经扫码订单列表
                                    scannedBranchOrders.add(shipOrder);
                                    scannedStatistics.add(new ShipmentGoodsStatistics(shipOrder.sId, 1, false));  //添加统计
                                } else {
                                    branchOrders.add(shipOrder);
                                    boolean find = false;
                                    for (ShipmentGoodsStatistics s : scanStatistics) {
                                        if (s.supplier.equals(shipOrder.sId)) {
                                            s.statistic++;
                                            find = true;
                                            break;
                                        }
                                    }
                                    if (!find) {
                                        ShipmentGoodsStatistics ns = new ShipmentGoodsStatistics(shipOrder.sId, 1, true);
                                        scanStatistics.add(ns);
                                    }
                                }
                            }
                            queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.errorData);
                            queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 条码输出框发生变化，检查是否格式有错误
     *
     * @param code 条码
     */
    void codeDataChanged(String code) {
        if (isCodeValid(code)) {
            inputFormState.setValue(new InputFormState(true));
        } else {
            inputFormState.setValue(new InputFormState(WarehouseApp.getInstance().getString(R.string.code_format_error)));
        }
    }

    private boolean isCodeValid(String code) {
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        if (code.length() < 7) {
            return false;
        } else {
            return code.matches("^[a-z0-9A-Z]+$");
        }
    }

    /**
     * 总订单数
     *
     * @return 总订单数
     */
    int getTotal() {
        return branchOrders.size() + scannedBranchOrders.size();
    }

    /**
     * 已扫描的订单数
     *
     * @return 订单数
     */
    int getScannedTotal() {
        return scannedBranchOrders.size();
    }

    /**
     * 未扫描的订单数
     *
     * @return 订单数
     */
    int getNotScanTotal() {
        return branchOrders.size();
    }

    /**
     * 数据提交成功后的处理
     */
    void updateSubmitSuccess() {
        this.clearData();
        this.queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    /**
     * 待扫描统计list中的position位置的统计被手动全部设置为已经扫描
     *
     * @param position position
     */
    void setShipmentStatisticsAllScan(int position) {
        ShipmentGoodsStatistics sItem = scanStatistics.get(position);
        //已扫描统计增加此统计数据
        boolean findInScanned = false;
        for (ShipmentGoodsStatistics s : scannedStatistics) {
            if (s.supplier.equals(sItem.supplier)) {
                s.statistic += sItem.statistic;
                findInScanned = true;
                break;
            }
        }
        if (!findInScanned) {
            scannedStatistics.add(new ShipmentGoodsStatistics(sItem.supplier, sItem.statistic, false));
        }
        //待扫描订单list和已扫描订单减增此订单数据
        for (int i = branchOrders.size() - 1; i >= 0; i--) {
            OrderInformation order = branchOrders.get(i);
            if (order.sId.equals(sItem.supplier)) {
                scannedBranchOrders.add(order);
                branchOrders.remove(i);
            }
        }
        //删除此统计
        scanStatistics.remove(position);
        //操作完成
        queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    /**
     * 清空数据
     */
    public void clearData() {
        //清空所有数据
        this.branchOrders.clear();
        this.scannedBranchOrders.clear();
        this.scanStatistics.clear();
        this.scannedStatistics.clear();
    }

    /**
     * 是否有未提交的数据
     *
     * @return yes/no
     */
    public boolean isHavDataNotProcessed() {
        return (scanStatistics.size() > 0 || scannedStatistics.size() > 0);
    }
}
