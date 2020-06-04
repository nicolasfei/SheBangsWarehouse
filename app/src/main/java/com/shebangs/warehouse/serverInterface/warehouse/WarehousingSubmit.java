package com.shebangs.warehouse.serverInterface.warehouse;

import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.HttpHandler;

/**
 * 入库提交
 * by bruce
 * 2020.05.25
 */
public class WarehousingSubmit extends WarehouseInterface {
    @Override
    public String getUrlParam() {
        return WarehousingSubmitInterface;
    }

    @Override
    public String echo(CommandVo vo) {
        return HttpHandler.handlerHttpRequest(vo.url, vo.parameters, vo.requestMode, vo.contentType);
    }
}
