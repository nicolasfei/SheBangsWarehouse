package com.shebangs.warehouse.warehouse;

import org.json.JSONException;
import org.json.JSONObject;

public class WarehouseInformation {

    public WarehouseInformation(String information) throws JSONException {
        JSONObject object = new JSONObject(information);
    }
}
