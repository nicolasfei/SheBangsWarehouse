package com.shebangs.warehouse.serverInterface.login;


import com.shebangs.warehouse.serverInterface.AbstractInterface;

public abstract class LoginInterface extends AbstractInterface {
    //库房登陆接口
    public final static String WarehouseLogin = AbstractInterface.COMMAND_URL+"api/Login/BranchSaleLogin";
    //获取库房信息
    public final static String GetWarehouseInformation = AbstractInterface.COMMAND_URL+"api/Login/GetWarehouseInformation";
    //获取库员信息集合
    public final static String GetWarehouseKeeperList = AbstractInterface.COMMAND_URL+"api/Login/GetWarehouseKeeperList";
}
