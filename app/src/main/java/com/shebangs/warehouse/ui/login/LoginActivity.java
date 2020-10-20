package com.shebangs.warehouse.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.BruceDialog;
import com.nicolas.toollibrary.LoginAutoMatch;
import com.nicolas.toollibrary.Utils;
import com.shebangs.warehouse.MainActivity;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.common.OperateResult;
import com.shebangs.warehouse.warehouse.WarehouseKeeper;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private static boolean loginIng = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppActivityManager.getInstance().addActivity(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        //初始化界面
        final AutoCompleteTextView usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        //添加自动匹配登陆用户账号信息
        LoginAutoMatch.getInstance().init(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, LoginAutoMatch.getInstance().getLoginUserName());
        usernameEditText.setAdapter(adapter);
        usernameEditText.setThreshold(1);   //设置输入几个字符后开始出现提示 默认是2

        //监听登陆信息输入
        loginViewModel.getLoginFormState().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(@Nullable OperateResult loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                if (loginFormState.getSuccess() != null) {
                    loginButton.setEnabled(true);
                }
                if (loginFormState.getError() != null) {
                    switch (loginFormState.getError().getErrorCode()) {
                        case -1:
                            usernameEditText.setError(loginFormState.getError().getErrorMsg());
                            break;
                        case -2:
                            passwordEditText.setError(loginFormState.getError().getErrorMsg());
                            break;
                    }
                }
            }
        });

        //监听登陆结果
        loginViewModel.getLoginResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult result) {
                if (result.getError() != null) {
                    BruceDialog.dismissProgressDialog();
                    Utils.toast(LoginActivity.this, result.getError().getErrorMsg());
                    loginIng = false;
                }
                if (result.getSuccess() != null) {
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.getting_warehouse));
                    //获取库房信息
                    loginViewModel.queryWarehouseInformation();
                }
            }
        });

        //监听获取库房信息
        loginViewModel.getWarehouseInformationResult().observe(this, new Observer<OperateResult>() {
            @Override
            public void onChanged(OperateResult result) {
                BruceDialog.dismissProgressDialog();
                if (result.getError() != null) {
                    Utils.toast(LoginActivity.this, result.getError().getErrorMsg());
                    loginIng = false;
                }
                if (result.getSuccess() != null) {
                    //选择库房
                    choiceWarehouse();
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
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.login_ing));
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
                if (!loginIng) {
                    loginIng = true;
                    BruceDialog.showProgressDialog(LoginActivity.this, getString(R.string.login_ing));
                    String userName = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    WarehouseKeeper.getInstance().setLoginName(userName);
                    WarehouseKeeper.getInstance().setLoginPassword(password);
                    loginViewModel.login(userName, password);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            loginIng = false;
        }
    }

    /**
     * 用户选择当前库房
     */
    private void choiceWarehouse() {
        BruceDialog.showSingleChoiceDialog(R.string.warehouse_choice, this, WarehouseKeeper.getInstance().getWarehouseInformationList(), new BruceDialog.OnChoiceItemListener() {
            @Override
            public void onChoiceItem(String itemName) {
                if (TextUtils.isEmpty(itemName)) {
                    choiceWarehouse();
                } else {
                    WarehouseKeeper.getInstance().setOnDutyWarehouse(itemName);
                    updateUiWithUser(WarehouseKeeper.getInstance().getOnDutyStaffName());
                }
            }
        });
    }

    /**
     * 欢迎登陆
     *
     * @param name 用户名
     */
    private void updateUiWithUser(String name) {
        String welcome = getString(R.string.welcome) + name;
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        //添加登陆用户
        LoginAutoMatch.getInstance().addLoginUser(WarehouseKeeper.getInstance().getLoginName(), WarehouseKeeper.getInstance().getLoginPassword());
        //跳转到主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onDestroy() {
        //打印机模块注销
        AppActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
