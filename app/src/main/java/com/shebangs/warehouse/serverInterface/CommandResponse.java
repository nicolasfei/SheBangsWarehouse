package com.shebangs.warehouse.serverInterface;

import android.util.Log;

import com.shebangs.warehouse.R;
import com.shebangs.warehouse.app.WarehouseApp;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandResponse {
    private static final String TAG = "CommandResponse";
    public boolean success = false;
    public String msg = "";
    public int code = 0;
    public String data;
    public CommandTypeEnum typeEnum;
    public String url;

    public CommandResponse(String response, String requestUrl) {
        if (response != null) {
            try {
                Log.i(TAG, "CommandResponse: " + response + " requestUrl is " + requestUrl);
                JSONObject rep = new JSONObject(response);
                this.success = rep.getBoolean("success");
                this.msg = rep.getString("msg");
                this.code = rep.getInt("code");

                if (rep.has("data")) {
                    this.data = rep.getString("data");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.msg = WarehouseApp.getInstance().getString(R.string.link_server_failed);
            }
        }
        this.url = requestUrl;
    }
}
