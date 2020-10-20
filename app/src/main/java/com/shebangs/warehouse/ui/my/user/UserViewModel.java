package com.shebangs.warehouse.ui.my.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.nicolas.toollibrary.HttpHandler;
import com.shebangs.warehouse.common.OperateError;
import com.shebangs.warehouse.common.OperateInUserView;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.serverInterface.CommandResponse;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.manager.ManagerInterface;

import java.util.HashMap;
import java.util.Map;

public class UserViewModel extends ViewModel {
    private String newPass;
    private MutableLiveData<OperateResult> modifyPassResult;

    public UserViewModel() {
        this.modifyPassResult = new MutableLiveData<>();
    }

    public LiveData<OperateResult> getModifyPassResult() {
        return modifyPassResult;
    }

    public void modifyPassword(String oldPassword,String newPass,String password2) {
        this.newPass = newPass;
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_MANAGER;
        vo.url = ManagerInterface.StaffPassWordModify;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("oldPassword", newPass);
        parameters.put("password", newPass);
        parameters.put("password2", newPass);
        vo.parameters = parameters;
        Invoker.getInstance().setOnEchoResultCallback(this.callback);
        Invoker.getInstance().exec(vo);
    }

    private Invoker.OnExecResultCallback callback = new Invoker.OnExecResultCallback() {

        @Override
        public void execResult(CommandResponse result) {
            switch (result.url) {
                case ManagerInterface.StaffPassWordModify:
                    if (!result.success) {
                        modifyPassResult.setValue(new OperateResult(new OperateError(-1, result.msg, null)));
                    } else {
                        //SupplierKeeper.getInstance().getSupplierAccount().password = newPass;
                        modifyPassResult.setValue(new OperateResult(new OperateInUserView(null)));
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
