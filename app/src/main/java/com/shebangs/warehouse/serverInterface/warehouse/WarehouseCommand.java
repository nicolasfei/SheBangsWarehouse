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
        //入库
        WarehouseInterface warehousingQuery = new WarehousingQuery();
        WarehouseInterface warehousingSubmit = new WarehousingSubmit();
        //出库
        WarehouseInterface exWarehouseQuery = new EXWarehouseQuery();
        WarehouseInterface eXWarehouseBranchQuery = new EXWarehouseBranchQuery();
        WarehouseInterface eXWarehouseBranchFormCodeQuery = new EXWarehouseBranchFormCodeQuery();
        WarehouseInterface exWarehouseSubmit = new EXWarehouseSubmit();
        //返货
        WarehouseInterface warehouseGoodsBackQuery = new WarehouseGoodsQuery();
        WarehouseInterface warehouseGoodsBackSubmit = new WarehouseGoodsBackSubmit();
        //收货小票查询
        WarehouseInterface warehouseGoodsBillQuery = new WarehouseGoodsBillQuery();

        //责任链
        warehousingQuery.setNextHandler(warehousingSubmit);
        warehousingSubmit.setNextHandler(exWarehouseQuery);
        exWarehouseQuery.setNextHandler(exWarehouseSubmit);
        exWarehouseSubmit.setNextHandler(eXWarehouseBranchQuery);
        eXWarehouseBranchQuery.setNextHandler(eXWarehouseBranchFormCodeQuery);
        eXWarehouseBranchFormCodeQuery.setNextHandler(warehouseGoodsBackQuery);
        warehouseGoodsBackQuery.setNextHandler(warehouseGoodsBackSubmit);
        warehouseGoodsBackSubmit.setNextHandler(warehouseGoodsBillQuery);
        warehouseGoodsBillQuery.setNextHandler(null);

        super.firstNode = warehousingQuery;
    }

    @Override
    public CommandTypeEnum getCommandType() {
        return CommandTypeEnum.COMMAND_WAREHOUSE_IN_OUT;
    }
}
