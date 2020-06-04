package com.shebangs.warehouse.serverInterface.login;

import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.HttpHandler;

public class WarehouseLogin extends LoginInterface {
    @Override
    public String getUrlParam() {
        return WarehouseLogin;
    }

    @Override
    public String echo(CommandVo vo) {
        return HttpHandler.handlerHttpRequest(vo.url, vo.parameters, vo.requestMode, vo.contentType);
    }
}
