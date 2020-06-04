package com.shebangs.warehouse.warehouse;

import org.json.JSONException;
import org.json.JSONObject;

public class WarehouseStaff {

    public String id;
    public String name;

    public WarehouseStaff(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public WarehouseStaff(String data) throws JSONException {
        JSONObject jsonObject = new JSONObject(data);
        this.id = jsonObject.getString("Id");
        this.id = jsonObject.getString("Name");
    }
}
