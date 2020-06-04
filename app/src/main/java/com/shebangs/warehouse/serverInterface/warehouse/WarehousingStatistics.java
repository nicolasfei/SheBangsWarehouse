package com.shebangs.warehouse.serverInterface.warehouse;

import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.HttpHandler;

public class WarehousingStatistics extends WarehouseInterface {
    @Override
    public String getUrlParam() {
        return WarehousingStatisticsInterface;
    }

    @Override
    public String echo(CommandVo vo) {
        return HttpHandler.handlerHttpRequest(vo.url, vo.parameters, vo.requestMode, vo.contentType);
    }
}
