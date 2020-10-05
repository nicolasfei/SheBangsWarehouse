package com.shebangs.warehouse.serverInterface;

import com.shebangs.warehouse.warehouse.WarehouseKeeper;

import java.util.Map;

public class CommandVo {
    public String url;
    public String token;
    public Map<String, String> parameters;
    public String requestMode;
    public String contentType;
    public CommandTypeEnum typeEnum;

    public CommandVo() {
        this.token = WarehouseKeeper.getInstance().getToken();
    }
}
