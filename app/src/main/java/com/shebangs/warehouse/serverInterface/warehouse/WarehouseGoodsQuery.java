package com.shebangs.warehouse.serverInterface.warehouse;

public class WarehouseGoodsQuery extends WarehouseInterface {
    @Override
    public String getUrlParam() {
        return WarehouseGoodsBackQueryInterface;
    }
}
