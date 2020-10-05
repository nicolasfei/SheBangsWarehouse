package com.shebangs.warehouse.serverInterface.login;

import com.shebangs.warehouse.serverInterface.Command;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;

public class LoginCommand extends Command {

    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        LoginInterface warehouseLogin = new WarehouseLogin();
        LoginInterface warehouseLogout = new WarehouseLogout();
        warehouseLogin.setNextHandler(warehouseLogout);
        warehouseLogout.setNextHandler(null);
        super.firstNode = warehouseLogin;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_WAREHOUSE_LOGIN;
    }
}
