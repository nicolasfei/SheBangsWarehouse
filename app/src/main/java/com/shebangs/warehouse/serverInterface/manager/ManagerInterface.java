package com.shebangs.warehouse.serverInterface.manager;

import com.shebangs.warehouse.serverInterface.AbstractInterface;

public abstract class ManagerInterface extends AbstractInterface {
    //获取库房信息
    public final static String GetWarehouseInformation = AbstractInterface.COMMAND_URL + "StoreRoom/StoreRoom";
    //获取库员信息集合
//    public final static String GetWarehouseKeeperList = AbstractInterface.COMMAND_URL + "api/Login/GetWarehouseKeeperList";
    //分店信息接口
    public final static String GetBranchInformation = AbstractInterface.COMMAND_URL + "StoreRoom/Branch";
    //库员密码修改接口
    public final static String StaffPassWordModify = AbstractInterface.COMMAND_URL + "StoreRoom/EditPassword";
    //app更新
    public final static String VersionCheck = "http://updatestoreroom.scdawn.com/v.json";
}
