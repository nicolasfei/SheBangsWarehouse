package com.shebangs.warehouse.ui.my.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.Utils;
import com.shebangs.warehouse.BaseActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;
import com.shebangs.warehouse.warehouse.WarehouseStaff;

public class UserActivity extends BaseActivity implements View.OnClickListener {

    private UserViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        this.viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        WarehouseStaff staff = WarehouseKeeper.getInstance().getOnDutyStaff();

        TextView name = findViewById(R.id.name);
        String nameValue = getString(R.string.name) + "\u3000\u3000\u3000\u3000\u3000" + staff.name;
        name.setText(nameValue);

        TextView phone = findViewById(R.id.phone);
        String phoneValue = getString(R.string.phone) + "\u3000\u3000\u3000\u3000\u3000" + staff.tel;
        phone.setText(phoneValue);

        TextView userName = findViewById(R.id.userName);
        String userNameValue = getString(R.string.userName) + "\u3000\u3000\u3000\u3000" + WarehouseKeeper.getInstance().getLoginName();
        userName.setText(userNameValue);

        TextView password = findClickView(R.id.password);
        String passwordValue = getString(R.string.password);
        password.setText(passwordValue);
        password.setClickable(true);

        TextView remark = findViewById(R.id.remark);
        String remarkValue = getString(R.string.remark) + "\u3000\u3000\u3000\u3000\u3000" + staff.remark;
        remark.setText(remarkValue);

        /**
         * 监听密码修改结果
         */
        viewModel.getModifyPassResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult operateResult) {
                BruceDialog.dismissProgressDialog();
                if (operateResult.getSuccess() != null) {
                    Utils.toast(UserActivity.this, R.string.modifyPassSuccess);
                }
                if (operateResult.getError() != null) {
                    Utils.toast(UserActivity.this, operateResult.getError().getErrorMsg());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password:
                BruceDialog.showPasswordModifyDialog(UserActivity.this, new BruceDialog.OnPasswordModifyListener() {
                    @Override
                    public void onPasswordModify(String old, String newPass, String newPassAgain) {
                        if (TextUtils.isEmpty(old) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(newPassAgain)) {   //TextUtils.isEmpty(old) ||
                            Utils.toast(UserActivity.this, R.string.inputNull);
                            return;
                        }

                        if (!(old.equals(WarehouseKeeper.getInstance().getLoginPassword()))) {
                            Utils.toast(UserActivity.this, R.string.oldPassError);
                            return;
                        }

                        if (!(newPass.equals(newPassAgain))) {
                            Utils.toast(UserActivity.this, R.string.newPassError);
                            return;
                        }

                        BruceDialog.showProgressDialog(UserActivity.this, getString(R.string.modifying));
                        viewModel.modifyPassword(old, newPass, newPassAgain);
                    }
                });
                break;
            default:
                break;
        }
    }
}
