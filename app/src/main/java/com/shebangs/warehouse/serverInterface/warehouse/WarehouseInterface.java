package com.shebangs.warehouse.serverInterface.warehouse;

import com.shebangs.warehouse.serverInterface.AbstractInterface;

public abstract class WarehouseInterface extends AbstractInterface {
    //库房入库商品查询接口
    public final static String WarehousingQueryInterface = AbstractInterface.COMMAND_URL + "api/Login/WarehousingQueryInterface";
    //库房入库商品提交接口
    public final static String WarehousingSubmitInterface = AbstractInterface.COMMAND_URL + "api/Login/WarehousingSubmitInterface";
    //库房入库统计接口
    public final static String WarehousingStatisticsInterface = AbstractInterface.COMMAND_URL + "api/Login/WarehousingStatisticsInterface";

    //库房出库商品查询接口
    public final static String EX_WarehouseQueryInterface = AbstractInterface.COMMAND_URL + "api/Login/EX_WarehouseQueryInterface";
    //库房须出库商店查询接口
    public final static String EX_WarehouseBranchInterface = AbstractInterface.COMMAND_URL + "api/Login/EX_WarehouseBranchInterface";
    //查询分店须发货物清单
    public final static String EX_WarehouseBranchGoodsListInterface = AbstractInterface.COMMAND_URL + "api/Login/EX_WarehouseBranchGoodsListInterface";
    //库房出库商品提交接口
    public final static String EX_WarehouseSubmitInterface = AbstractInterface.COMMAND_URL + "api/Login/EX_WarehouseSubmitInterface";
    //库房出库统计接口
    public final static String EX_WarehouseStatisticsInterface = AbstractInterface.COMMAND_URL + "api/Login/EX_WarehouseStatisticsInterface";
}
