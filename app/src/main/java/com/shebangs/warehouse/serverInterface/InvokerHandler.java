package com.shebangs.warehouse.serverInterface;

import android.text.TextUtils;

import com.nicolas.toollibrary.HttpHandler;
import com.shebangs.warehouse.serverInterface.manager.ManagerInterface;

import java.util.HashMap;
import java.util.Map;

public class InvokerHandler {

    private static InvokerHandler handler = new InvokerHandler();

    private InvokerHandler() {
    }

    public static InvokerHandler getInstance() {
        return handler;
    }

    /**
     * 获取命令---获取库房信息
     *
     * @return 库房信息命令
     */
    public CommandVo getStorehouseInformationCommand() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_MANAGER;
        vo.url = ManagerInterface.GetWarehouseInformation;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", "1");
        parameters.put("pageSize", "10000");
        parameters.put("pageCount", "0");
        vo.parameters = parameters;
        return vo;
    }

    /**
     * 获取命令---获取分店信息
     *
     * @return 分店信息命令
     */
    public CommandVo getBranchInformationCommand() {
        CommandVo vo = new CommandVo();
        vo.typeEnum = CommandTypeEnum.COMMAND_WAREHOUSE_MANAGER;
        vo.url = ManagerInterface.GetBranchInformation;
        vo.contentType = HttpHandler.ContentType_APP;
        vo.requestMode = HttpHandler.RequestMode_POST;
        Map<String, String> parameters = new HashMap<>();
        parameters.put("currentPage", "1");
        parameters.put("pageSize", "10000");
        parameters.put("pageCount", "0");
//        parameters.put("fId","1");         //分店编号---选填
        vo.parameters = parameters;
        return vo;
    }
}
