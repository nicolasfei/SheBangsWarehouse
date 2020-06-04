package com.shebangs.warehouse.serverInterface.warehouse;

import com.shebangs.warehouse.serverInterface.Command;
import com.shebangs.warehouse.serverInterface.CommandTypeEnum;
import com.shebangs.warehouse.serverInterface.CommandVo;

public class WarehouseCommand extends Command {
    @Override
    public String execute(CommandVo vo) {
        return super.firstNode.handleMessage(vo);
    }

    @Override
    protected void buildDutyChain() {
        WarehouseInterface warehousingQuery = new WarehousingQuery();
        WarehouseInterface warehousingSubmit = new WarehousingSubmit();
        WarehouseInterface warehousingStatistics = new WarehousingStatistics();
        WarehouseInterface exWarehouseQuery = new EXWarehouseQuery();
        WarehouseInterface exWarehouseSubmit = new EXWarehouseSubmit();
        WarehouseInterface exWarehouseStatistics = new EXWarehouseStatistics();

        warehousingQuery.setNextHandler(exWarehouseQuery);
        exWarehouseQuery.setNextHandler(warehousingStatistics);
        warehousingStatistics.setNextHandler(exWarehouseStatistics);
        exWarehouseStatistics.setNextHandler(warehousingSubmit);
        warehousingSubmit.setNextHandler(exWarehouseSubmit);

        super.firstNode = warehousingQuery;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
    }
}
