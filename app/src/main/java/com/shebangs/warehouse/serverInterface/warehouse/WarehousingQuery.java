package com.shebangs.warehouse.serverInterface.warehouse;

import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.HttpHandler;

/**
 * 入库查询
 * by bruce
 * 2020.05.25
 */
public class WarehousingQuery extends WarehouseInterface {
    @Override
    public String getUrlParam() {
        return WarehousingQueryInterface;
    }

    @Override
    public String echo(CommandVo vo) {
        return HttpHandler.handlerHttpRequest(vo.url, vo.parameters, vo.requestMode, vo.contentType);
    }
}
