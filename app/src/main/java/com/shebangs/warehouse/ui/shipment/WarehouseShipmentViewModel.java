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

public class WarehouseShipmentViewModel extends ViewModel {
    private static final String TAG = "WarehouseShipmentViewModel";
    private boolean isHaveInvalidGoods;

    private String currentDeliveryRequiredBranchID;       //当前须出库的分店ID--用于查询条码
    private String currentDeliveryRequiredBranchCode;       //当前须出库的分店的code
    private List<OrderInformation> branchOrders;            //分店订单列表
    private List<OrderInformation> scannedBranchOrders;     //已扫描分店订单列表
    private List<String> scannedOrderIds;                   //已经扫描了得订单号
    private List<ShipmentGoodsStatistics> scanStatistics;           //订单统计--未扫描
    private List<ShipmentGoodsStatistics> scannedStatistics;        //订单统计--已扫描

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> queryCodeInformationResult;
    private MutableLiveData<OperateResult> submitBusinessDataResult;
    private MutableLiveData<OperateResult> queryDeliveryRequiredBranchGoodsListResult;

    private String scanCodeFormQueryBranchList = "";        //通过扫码查询代发货的清单的条码
    private boolean refresh = false;            //是否是刷新数据

    public WarehouseShipmentViewModel() {
        this.queryCodeInformationResult = new MutableLiveData<>();
        this.inputFormState = new MutableLiveData<>();
        this.submitBusinessDataResult = new MutableLiveData<>();
        this.queryDeliveryRequiredBranchGoodsListResult = new MutableLiveData<>();
        this.branchOrders = new ArrayList<>();
        this.scannedBranchOrders = new ArrayList<>();
        this.scannedOrderIds = new ArrayList<>();
        this.scanStatistics = new ArrayList<>();
        this.scannedStatistics = new ArrayList<>();
        this.isHaveInvalidGoods = false;
    }

    public LiveData<OperateResult> getQueryDeliveryRequiredBranchGoodsListResult() {
        return queryDeliveryRequiredBranchGoodsListResult;
    }

    public List<ShipmentGoodsStatistics> getScannedStatistics() {
        return scannedStatistics;
    }

    public List<ShipmentGoodsStatistics> getScanStatistics() {
        return scanStatistics;
    }

    public String getCurrentDeliveryRequiredBranchCode() {
        return currentDeliveryRequiredBranchCode;
    }

    public LiveData<OperateResult> getSubmitBusinessDataResult() {
        return submitBusinessDataResult;
    }

    public LiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    public LiveData<OperateResult> getQueryCodeInformationResult() {
        return queryCodeInformationResult;
    }

    public List<OrderInformation> getGoodsInformationList() {
        return branchOrders;
    }

    public List<OrderInformation> getScannedBranchOrders() {
        return scannedBranchOrders;
    }

    /**
     * 处理条码输入
     * 如果第一次扫描，这是查询该条码上的分店的代发货清单
     * 否则是查询条码信息
     *
     * @param code 条码
     */
    public void queryCodeInformation(String code) {
        //检查是否已经选择了分店
//        if (TextUtils.isEmpty(this.currentDeliveryRequiredBranch)) {
//            queryCodeInformationResult.setValue(new OperateResult(new OperateError(0,
//                    WarehouseApp.getInstance().getString(R.string.no_choice_branch), null)));
//            return;
//        }
        //检查分店是否有需要发的货物
        if (this.branchOrders.size() == 0 && this.scannedBranchOrders.size() == 0) {
//            queryCodeInformationResult.setValue(new OperateResult(new OperateError(0,
//                    WarehouseApp.getInstance().getString(R.string.branch_not_delivery_required), null)));
//            return;
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
    public void submitBusinessData() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.EX_WarehouseSubmitInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        StringBuilder builder = new StringBuilder();
        for (String item : scannedOrderIds) {
            builder.append(item).append(",");
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
    public void queryDeliveryRequiredBranch(String branchCode) {
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
     * 刷新
     */
    public void refreshData() {
        this.refresh = true;
        queryBranchShipmentOrder();
    }

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
                            if (order.id.equals(item.id)) {
                                isThisBranchId = true;
                                if (!item.isExWarehouseScan) {
                                    item.isExWarehouseScan = true;
                                    //待扫描订单统计减去此订单
                                    for (int k = 0; k < scanStatistics.size(); k++) {
                                        ShipmentGoodsStatistics s = scanStatistics.get(k);
                                        if (item.sId.equals(s.supplier)) {
                                            s.statistic--;
                                            //统计为0时，删除这个统计
                                            if (s.statistic == 0) {
                                                scanStatistics.remove(k);
                                            }
                                            break;
                                        }
                                    }
                                    branchOrders.remove(i);

                                    order.isExWarehouseScan = true;
                                    //已扫描订单统计加上此订单
                                    boolean find = false;
                                    for (ShipmentGoodsStatistics s : scannedStatistics) {
                                        if (order.sId.equals(s.supplier)) {
                                            s.statistic++;
                                            find = true;
                                            break;
                                        }
                                    }
                                    if (!find) {
                                        scannedStatistics.add(new ShipmentGoodsStatistics(order.sId, 1, true));
                                    }
                                    scannedBranchOrders.add(order);
                                } else {
                                    isRepeatScan = true;
                                }
                                break;
                            }
                        }
                        if (!isThisBranchId) {
                            for (OrderInformation item : scannedBranchOrders) {
                                if (order.id.equals(item.id)) {
                                    Message msg = new Message();
                                    msg.obj = WarehouseApp.getInstance().getString(R.string.repeatOrder);
                                    queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(msg)));
                                    return;
                                }
                            }
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.notThisBranchOrder);
                            queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        } else {
                            if (isRepeatScan) {
                                Message msg = new Message();
                                msg.obj = WarehouseApp.getInstance().getString(R.string.repeatOrder);
                                queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                scannedOrderIds.add(order.id);
                                queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        }
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
                //分店订单列表查询
                case WarehouseInterface.EX_WarehouseBranchGoodsListInterface:
                    if (!result.success) {
                        queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        branchOrders.clear();
                        scanStatistics.clear();
                        try {
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                scannedOrderIds.clear();        //不是刷新情况下要清空scannedOrderIds
                                scannedBranchOrders.clear();
                                scannedStatistics.clear();
                                Message msg = new Message();
                                msg.obj = WarehouseApp.getInstance().getString(R.string.branch_not_delivery_required);
                                queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                for (int i = 0; i < array.length(); i++) {
                                    OrderInformation order = new OrderInformation(array.getString(i));
                                    branchOrders.add(order);
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
                                if (refresh) {
                                    refresh = false;
                                    for (int i = scannedOrderIds.size() - 1; i >= 0; i--) {
                                        String scanID = scannedOrderIds.get(i);
                                        for (int j = branchOrders.size() - 1; j >= 0; j--) {
                                            OrderInformation order = branchOrders.get(j);
                                            if (scanID.equals(order.id)) {
                                                //待扫描统计减去这个订单
                                                for (int k = 0; k < scanStatistics.size(); k++) {
                                                    ShipmentGoodsStatistics s = scanStatistics.get(k);
                                                    if (s.supplier.equals(order.sId)) {
                                                        s.statistic--;
                                                        //统计为0时，删除这个统计
                                                        if (s.statistic == 0) {
                                                            scanStatistics.remove(k);
                                                        }
                                                        break;
                                                    }
                                                }

                                                //待扫描订单列表删除这个订单
                                                branchOrders.remove(j);     //移除已经扫描的订单
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    scannedOrderIds.clear();        //不是刷新情况下要清空scannedOrderIds
                                    scannedBranchOrders.clear();
                                    scannedStatistics.clear();
                                }
                                queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(null)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.obj = WarehouseApp.getInstance().getString(R.string.errorData);
                            queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                        }
                    }
                    break;
                case WarehouseInterface.EX_WarehouseBranchGoodsListFormCodeInterface:
                    if (!result.success) {
                        queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            JSONArray array = new JSONArray(result.data);
                            if (array.length() == 0) {
                                Message msg = new Message();
                                msg.obj = WarehouseApp.getInstance().getString(R.string.branch_not_delivery_required);
                                queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                            } else {
                                //判断是否是本库房的代发货的分店
                                OrderInformation test = new OrderInformation(array.getString(0));
                                if (!test.storeRoomId.equals(WarehouseKeeper.getInstance().getOnDutyWarehouse().id)) {
                                    //此分店不属于本库房
                                    Message msg = new Message();
                                    msg.obj = WarehouseApp.getInstance().getString(R.string.branch_not_this_storeRoom);
                                    queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(msg)));
                                } else {
                                    currentDeliveryRequiredBranchID = test.branchId;      //记录分店ID，用于扫码
                                    currentDeliveryRequiredBranchCode = test.fId;
                                    branchOrders.clear();
                                    scanStatistics.clear();
                                    scannedStatistics.clear();
                                    scannedBranchOrders.clear();
                                    scannedOrderIds.clear();
                                    //解析发货清单
                                    for (int i = 0; i < array.length(); i++) {
                                        OrderInformation shipOrder = new OrderInformation(array.getString(i));
                                        if (shipOrder.id.equals(scanCodeFormQueryBranchList)) {     //这个已经扫码的订单加入已经扫码订单列表
                                            scannedBranchOrders.add(shipOrder);
                                            scannedOrderIds.add(shipOrder.id);
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
                                }
                            }
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
    public void codeDataChanged(String code) {
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
    public int getTotal() {
        return branchOrders.size() + scannedBranchOrders.size();
    }

    /**
     * 已扫描的订单数
     *
     * @return 订单数
     */
    public int getScanTotal() {
        return scannedOrderIds.size();
    }

    /**
     * 未扫描的订单数
     *
     * @return 订单数
     */
    public int getNotScanTotal() {
        return branchOrders.size();
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
     * 数据提交成功后的处理
     */
    public void updateSubmitSuccess() {
        //清空所有数据
        this.branchOrders.clear();
        this.scannedBranchOrders.clear();
        this.scannedOrderIds.clear();
        this.scanStatistics.clear();
        this.scannedStatistics.clear();
        this.queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
    }

    /**
     * 获取已经扫描过的订单
     *
     * @return 已经扫描过的订单
     */
    public List<String> getScannedOrderIds() {
        return scannedOrderIds;
    }

    /**
     * 待扫描统计list中的position位置的统计被手动全部设置为已经扫描
     *
     * @param position position
     */
    public void setShipmentStatisticsAllScan(int position) {
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
                scannedOrderIds.add(order.id);
                branchOrders.remove(i);
            }
        }
        //删除此统计
        scanStatistics.remove(position);
        //操作完成
        queryDeliveryRequiredBranchGoodsListResult.setValue(new OperateResult(new OperateInUserView(null)));
    }
}
