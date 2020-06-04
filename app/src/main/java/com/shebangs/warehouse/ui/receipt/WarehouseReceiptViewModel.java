package com.shebangs.warehouse.ui.receipt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.InputFormState;
import com.shebangs.warehouse.common.OperateError;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.component.datetimepicker.DateFormatUtils;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.HttpHandler;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.login.LoginInterface;
import com.shebangs.warehouse.serverInterface.warehouse.WarehouseInterface;
import com.shebangs.warehouse.tool.Tool;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseReceiptViewModel extends ViewModel {
    private String warehouseName;
    private String onDutyStaff;
    private String newStaffLogin;
    private String businessTime;
    private int total;
    private float totalPrice;
    private List<ReceiptGoodsInformation> goodsInformationList;
    private boolean isHaveInvalidGoods;

    private MutableLiveData<InputFormState> inputFormState;             //输入格式是否正确
    private MutableLiveData<OperateResult> setWarehouseNameResult;
    private MutableLiveData<OperateResult> setNewOnDutyStaffSwitchResult;
    private MutableLiveData<OperateResult> setNewOnDutyStaffLoginResult;
    private MutableLiveData<OperateResult> setBusinessTimeResult;
    private MutableLiveData<OperateResult> queryCodeInformationResult;
    private MutableLiveData<OperateResult> submitBusinessDataResult;

    public WarehouseReceiptViewModel() {
        this.setWarehouseNameResult = new MutableLiveData<>();
        this.setNewOnDutyStaffSwitchResult = new MutableLiveData<>();
        this.setNewOnDutyStaffLoginResult = new MutableLiveData<>();
        this.setBusinessTimeResult = new MutableLiveData<>();
        this.queryCodeInformationResult = new MutableLiveData<>();
        this.inputFormState = new MutableLiveData<>();
        this.submitBusinessDataResult = new MutableLiveData<>();
        this.goodsInformationList = new ArrayList<>();

        this.warehouseName = "测试库房";
        this.onDutyStaff = "测试员";
        this.businessTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        this.isHaveInvalidGoods = false;

        this.goodsInformationList.add(ReceiptGoodsInformation.getTestData());
        this.goodsInformationList.add(ReceiptGoodsInformation.getTestData());
        this.goodsInformationList.add(ReceiptGoodsInformation.getTestData());
        this.goodsInformationList.add(ReceiptGoodsInformation.getTestData());
        this.goodsInformationList.add(ReceiptGoodsInformation.getTestData());
        this.goodsInformationList.add(ReceiptGoodsInformation.getTestData());
    }

    public LiveData<OperateResult> getSubmitBusinessDataResult() {
        return submitBusinessDataResult;
    }

    public LiveData<InputFormState> getInputFormState() {
        return inputFormState;
    }

    public LiveData<OperateResult> getSetWarehouseNameResult() {
        return setWarehouseNameResult;
    }

    public LiveData<OperateResult> getSetNewOnDutyStaffSwitchResult() {
        return setNewOnDutyStaffSwitchResult;
    }

    public LiveData<OperateResult> getSetNewOnDutyStaffLoginResult() {
        return setNewOnDutyStaffLoginResult;
    }

    public LiveData<OperateResult> getSetBusinessTimeResult() {
        return setBusinessTimeResult;
    }

    public LiveData<OperateResult> getQueryCodeInformationResult() {
        return queryCodeInformationResult;
    }

    public List<ReceiptGoodsInformation> getGoodsInformationList() {
        return goodsInformationList;
    }

    public void setWarehouseName(String name) {
        if (!this.warehouseName.equals(name)) {
            this.warehouseName = name;
            this.setWarehouseNameResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setNewOnDutyStaffSwitch(String name) {
        if (!this.onDutyStaff.equals(name)) {
            this.newStaffLogin = name;
            this.setNewOnDutyStaffSwitchResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    public String getOnDutyStaff() {
        return onDutyStaff;
    }

    public void newOnDutyStaffLogin(String password) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_LOGIN;
        vo.url = LoginInterface.WarehouseLogin;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("loginName", this.newStaffLogin);
        parameters.put("loginPwd", password);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    public void queryCodeInformation(String code) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehousingQueryInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userkey", WarehouseKeeper.getInstance().userKey);
        parameters.put("code", code);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    public void submitBusinessData() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
        vo.url = WarehouseInterface.WarehousingSubmitInterface;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userkey", WarehouseKeeper.getInstance().userKey);
        parameters.put("code", "code");
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case LoginInterface.WarehouseLogin:        //库房登陆
                    if (!result.success) {
                        setNewOnDutyStaffLoginResult.setValue(new OperateResult(new OperateError(0, WarehouseApp.getInstance().getString(R.string.login_failed), null)));
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(result.data);
                            WarehouseKeeper.getInstance().setUserId(jsonObject.getString("userid"));
                            WarehouseKeeper.getInstance().setUserKey(jsonObject.getString("userkey"));
                            WarehouseKeeper.getInstance().setOnDutyStaff(onDutyStaff);
                            onDutyStaff = newStaffLogin;
                            setNewOnDutyStaffLoginResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setNewOnDutyStaffLoginResult.setValue(new OperateResult(new OperateError(0, WarehouseApp.getInstance().getString(R.string.login_failed), null)));
                        }
                    }
                    break;
                case WarehouseInterface.WarehousingQueryInterface:
                    if (!result.success) {
                        queryCodeInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    }
                    break;
                case WarehouseInterface.WarehousingSubmitInterface:
                    if (!result.success) {
                        submitBusinessDataResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        submitBusinessDataResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void setBusinessTime(String dateTime) {
        if (!dateTime.equals(this.businessTime)) {
            this.businessTime = dateTime;
            this.setBusinessTimeResult.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    public String getBusinessTime() {
        return businessTime;
    }

    /**
     * 条码输出框发生变化，检查是否格式有错误
     *
     * @param code 条码
     */
    public void codeDataChanged(String code) {
        if (Tool.isCodeValid(code)) {
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

    public void deleteGoods(int position) {
        ReceiptGoodsInformation item = this.goodsInformationList.get(position);
        this.total -= item.orderNum;
        this.totalPrice -= item.orderPrice;
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
     * 数据提交成功后的处理
     */
    public void updateSubmitSuccess() {
        //清空商品数据
        this.goodsInformationList.clear();
        //更新业务时间
        this.businessTime = DateFormatUtils.long2Str(System.currentTimeMillis(), true);
        this.queryCodeInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
        this.setBusinessTimeResult.setValue(new OperateResult(new OperateInUserView(null)));
    }
}
