package com.shebangs.warehouse.ui.login;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shebangs.warehouse.R;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.HttpHandler;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.login.LoginInterface;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;
import com.shebangs.warehouse.warehouse.WarehouseStaff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState;
    private MutableLiveData<LoginResult> loginResult;
    private MutableLiveData<LoginResult> warehouseStaffListResult;
    private MutableLiveData<LoginResult> warehouseInformationResult;


    public LoginViewModel() {
        loginFormState = new MutableLiveData<>();
        loginResult = new MutableLiveData<>();
        warehouseStaffListResult = new MutableLiveData<>();
        warehouseInformationResult = new MutableLiveData<>();
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<LoginResult> getWarehouseStaffListResult() {
        return warehouseStaffListResult;
    }

    public LiveData<LoginResult> getWarehouseInformationResult() {
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
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("loginName", username);
        parameters.put("loginPwd", password);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 获取库房信息
     *
     * @param userKey userKey
     */
    public void queryWarehouseInformation(String userKey) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_LOGIN;
        vo.url = LoginInterface.GetWarehouseInformation;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userkey", userKey);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    /**
     * 获取库员集合
     *
     * @param userKey userKey
     */
    public void queryWarehouseKeeperList(String userKey) {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_LOGIN;
        vo.url = LoginInterface.GetWarehouseKeeperList;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_GET;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userkey", userKey);
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
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(result.data);
                            WarehouseKeeper.getInstance().setUserId(jsonObject.getString("userid"));
                            WarehouseKeeper.getInstance().setUserKey(jsonObject.getString("userkey"));
                            loginResult.setValue(new LoginResult(new LoggedInUserView("")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                        }
                    }
                    break;
                case LoginInterface.GetWarehouseKeeperList:            //获取库员信息集合
                    if (!result.success) {
                        warehouseStaffListResult.setValue(new LoginResult(R.string.get_staffs_failed));
                    } else {
                        List<WarehouseStaff> staffs = new ArrayList<>();
                        try {
                            JSONArray array = new JSONArray(result.data);
                            for (int i = 0; i < array.length(); i++) {
                                WarehouseStaff staff = new WarehouseStaff(array.getString(i));
                                staffs.add(staff);
                            }
                            WarehouseKeeper.getInstance().setStaffs(staffs);
                            warehouseStaffListResult.setValue(new LoginResult(new LoggedInUserView("")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            warehouseStaffListResult.setValue(new LoginResult(R.string.get_staffs_failed));
                        }
                    }
                    break;
                case LoginInterface.GetWarehouseInformation:            //获取库房信息
                    if (!result.success) {
                        warehouseInformationResult.setValue(new LoginResult(R.string.get_warehouse_failed));
                    } else {
                        try {
                            WarehouseKeeper.getInstance().setInformation(result.data);
                            warehouseInformationResult.setValue(new LoginResult(new LoggedInUserView("")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            warehouseInformationResult.setValue(new LoginResult(R.string.get_warehouse_failed));
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
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
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
