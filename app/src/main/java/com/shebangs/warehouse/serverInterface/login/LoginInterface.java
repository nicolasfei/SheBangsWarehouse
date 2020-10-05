package com.shebangs.warehouse.serverInterface.login;


import com.shebangs.warehouse.serverInterface.AbstractInterface;

public abstract class LoginInterface extends AbstractInterface {
    //库房登陆接口
    public final static String WarehouseLogin = AbstractInterface.COMMAND_URL+"StoreRoom/Login";
    //库房登出接口
    public final static String WarehouseLogout = AbstractInterface.COMMAND_URL+"StoreRoom/LoginOut";
}
