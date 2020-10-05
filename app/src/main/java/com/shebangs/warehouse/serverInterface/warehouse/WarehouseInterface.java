package com.shebangs.warehouse.serverInterface.warehouse;

import com.shebangs.warehouse.serverInterface.AbstractInterface;

public abstract class WarehouseInterface extends AbstractInterface {
    //库房入库商品查询接口
    public final static String WarehousingQueryInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrderGetById";
    //库房入库商品提交接口
    public final static String WarehousingSubmitInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrderReceive";
    //库房入库统计接口
    public final static String WarehousingStatisticsInterface = AbstractInterface.COMMAND_URL + "api/Login/WarehousingStatisticsInterface";

    //库房出库商品查询接口
    public final static String EX_WarehouseQueryInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrderSendById";
    //查询分店须发货物清单
    public final static String EX_WarehouseBranchGoodsListInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrder";
    //通过条码查询分店须发货物清单
    public final static String EX_WarehouseBranchGoodsListFormCodeInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrderAllSendById";
    //库房出库商品提交接口
    public final static String EX_WarehouseSubmitInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrderSend";
    //库房出库统计接口
    public final static String EX_WarehouseStatisticsInterface = AbstractInterface.COMMAND_URL + "api/Login/EX_WarehouseStatisticsInterface";

    //库房返货提交
    public final static String WarehouseGoodsBackSubmitInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsBack";
    //库房返货查询
    public final static String WarehouseGoodsBackQueryInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsBackById";

    //小票查询
    public final static String WarehouseGoodsBillQueryInterface = AbstractInterface.COMMAND_URL + "StoreRoom/GoodsOrderGetByLastReceiptId";
}
