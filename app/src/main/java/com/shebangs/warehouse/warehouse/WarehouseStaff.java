package com.shebangs.warehouse.warehouse;

import org.json.JSONException;
import org.json.JSONObject;

public class WarehouseStaff {

    public String id = "";
    public String tel = "";
    public String name = "";
    public String remark = "";
    public String valid = "";

    public WarehouseStaff(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        this.id = jsonObject.getString("id");
        this.tel = jsonObject.getString("tel");
        this.name = jsonObject.getString("name");
        this.remark = jsonObject.getString("remark");
        this.valid = jsonObject.getString("valid");
    }

    public WarehouseStaff() {

    }
}
