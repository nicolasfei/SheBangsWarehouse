package com.shebangs.warehouse.serverInterface.warehouse;

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
}
