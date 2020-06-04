package com.shebangs.warehouse.ui.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.shebangs.warehouse.MainActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.tool.Utils;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ProgressDialog loginDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);

        /**
         * 监听登陆信息输入
         */
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        /**
         * 监听登陆结果
         */
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult result) {
                if (result.getError() != null) {
                    dismissLoginDialog();
                    Utils.toast(LoginActivity.this, result.getError());
                }
                if (result.getSuccess() != null) {
                    showLoginDialog(getString(R.string.getting_warehouse));
                    //获取库房信息
                    loginViewModel.queryWarehouseInformation(WarehouseKeeper.getInstance().userKey);
                }
            }
        });

        /**
         * 监听获取库房信息
         */
        loginViewModel.getWarehouseInformationResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult result) {

                if (result.getError() != null) {
                    dismissLoginDialog();
                    Utils.toast(LoginActivity.this, result.getError());
                }
                if (result.getSuccess() != null) {
                    showLoginDialog(getString(R.string.getting_staff));
                    //获取库员list
                    loginViewModel.queryWarehouseKeeperList("");
                }
            }
        });

        /**
         * 监听获取库员信息
         */
        loginViewModel.getWarehouseStaffListResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(LoginResult result) {
                dismissLoginDialog();
                if (result.getError() != null) {
                    Utils.toast(LoginActivity.this, result.getError());
                }
                if (result.getSuccess() != null) {
                    //选择当前在岗导购
                    choiceOnDutyGuide();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    showLoginDialog(getString(R.string.login_ing));
                    String userName = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    WarehouseKeeper.getInstance().setLoginName(userName);
                    WarehouseKeeper.getInstance().setLoginPassword(password);
                    loginViewModel.login(userName, password);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
                showLoginDialog(getString(R.string.login_ing));
                String userName = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                WarehouseKeeper.getInstance().setLoginName(userName);
                WarehouseKeeper.getInstance().setLoginPassword(password);
                loginViewModel.login(userName, password);
            }
        });
    }

    /**
     * 选择在岗库员
     */
    private void choiceOnDutyGuide() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.login_onduty_staff);
        builder.setCancelable(false);

        final String items[] = new String[WarehouseKeeper.getInstance().staffs.size()];
        for (int i = 0; i < WarehouseKeeper.getInstance().staffs.size(); i++) {
            items[i] = WarehouseKeeper.getInstance().staffs.get(i).name;
        }
        // -1代表没有条目被选中
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 设置当前在岗人员
                WarehouseKeeper.getInstance().setOnDutyStaff(items[which]);
                updateUiWithUser(new LoggedInUserView(items[which]));       //登陆成功
                // [2]把对话框关闭
                dialog.dismiss();
            }
        });

        // 最后一步 一定要记得 和Toast 一样 show出来
        builder.create().show();
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showLoginDialog(String loginMsg) {
        if (loginDialog == null) {
            loginDialog = new ProgressDialog(this);
            loginDialog.setCancelable(false);
            loginDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        loginDialog.setMessage(loginMsg);
        if (!loginDialog.isShowing()) {
            loginDialog.show();
        }
    }

    private void dismissLoginDialog() {
        if (loginDialog.isShowing()) {
            loginDialog.dismiss();
        }
    }
}
