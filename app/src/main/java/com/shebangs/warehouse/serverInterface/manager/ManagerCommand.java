package com.shebangs.warehouse.serverInterface.manager;

import com.shebangs.warehouse.serverInterface.Command;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;

import static com.shebangs.warehouse.serverInterface.CommandTypeEnum.COMMAND_WAREHOUSE_MANAGER;

public class ManagerCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return firstNode.echo(vo);
    }

    @Override
    protected void buildDutyChain() {
        ManagerInterface warehouseInfo = new WarehouseInformation();
        ManagerInterface passwordModify = new StaffPassWordModify();
        ManagerInterface versionCheck = new VersionCheck();

        warehouseInfo.setNextHandler(passwordModify);
        passwordModify.setNextHandler(versionCheck);
        versionCheck.setNextHandler(null);

        super.firstNode = warehouseInfo;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return COMMAND_WAREHOUSE_MANAGER;
    }
}
