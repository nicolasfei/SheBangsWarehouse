package com.shebangs.warehouse;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.nicolas.toollibrary.AppActivityManager;
import com.nicolas.toollibrary.shakeproof.EventListener;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppActivityManager.getInstance().addActivity(this);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 显示查询进度条
     */
    protected void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.data_query));
            dialog.create();
        }
        dialog.show();
    }

    /**
     * 取消查询进度条
     */
    protected void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Find出来的View，自带防抖功能
     */
    public <T extends View> T findClickView(int id) {

        T view = (T) findViewById(id);
        view.setOnClickListener(new EventListener(this));
        return view;
    }

    @Override
    protected void onDestroy() {
        AppActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
