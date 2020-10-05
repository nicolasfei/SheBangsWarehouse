package com.shebangs.warehouse.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nicolas.toollibrary.HttpHandler;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;
import com.shebangs.warehouse.common.OperateError;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.login.LoginInterface;
import com.shebangs.warehouse.serverInterface.manager.ManagerInterface;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<OperateResult> loginFormState;
    private MutableLiveData<OperateResult> loginResult;
    private MutableLiveData<OperateResult> warehouseStaffListResult;
    private MutableLiveData<OperateResult> warehouseInformationResult;


    public LoginViewModel() {
        loginFormState = new MutableLiveData<>();
        loginResult = new MutableLiveData<>();
        warehouseStaffListResult = new MutableLiveData<>();
        warehouseInformationResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<OperateResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<OperateResult> getWarehouseStaffListResult() {
        return warehouseStaffListResult;
    }

    public LiveData<OperateResult> getWarehouseInformationResult() {
        return warehouseInformationResult;
    }

    /**
     * 库房登陆
     *
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_LOGIN;
        vo.url = LoginInterface.WarehouseLogin;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userName", username);
        parameters.put("password", password);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 获取库房信息
     */
    public void queryWarehouseInformation() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_MANAGER;
        vo.url = ManagerInterface.GetWarehouseInformation;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", "1");
        parameters.put("pageSize", "10000");
        parameters.put("pageCount", "0");
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
                        loginResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            JSONObject token = new JSONObject(result.token);
                            WarehouseKeeper.getInstance().setToken(token.getString("token"));
                            WarehouseKeeper.getInstance().setOnDutyStaff(result.data);
                            loginResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loginResult.setValue(new OperateResult(new OperateError(-1, WarehouseApp.getInstance().getString(R.string.errorData), null)));
                        }
                    }
                    break;
                case ManagerInterface.GetWarehouseInformation:            //获取库房信息
                    if (!result.success) {
                        warehouseInformationResult.setValue(new OperateResult(new OperateError(result.code, result.msg, null)));
                    } else {
                        try {
                            WarehouseKeeper.getInstance().setWarehouseInformation(result.data, result.jsonData);
                            warehouseInformationResult.setValue(new OperateResult(new OperateInUserView(null)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            warehouseInformationResult.setValue(new OperateResult(new OperateError(-1, WarehouseApp.getInstance().getString(R.string.errorData), null)));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new OperateResult(new OperateError(-1, WarehouseApp.getInstance().getString(R.string.invalid_username), null)));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new OperateResult(new OperateError(-2, WarehouseApp.getInstance().getString(R.string.invalid_password), null)));
        } else {
            loginFormState.setValue(new OperateResult(new OperateInUserView(null)));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
