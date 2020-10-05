package com.shebangs.warehouse.data;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OrderInformation {
    public String id;               //ID
    public String gId;              //货号ID
    public String sId;              //sId
    public String supplierId;       //供货商主键编号
    public String supplierName;     //供货商名称
    public String goodsClassId;     //货物类别编号
    public String goodsClassName;   //货物类别名称--"男装",
    public String branchId;         //分店id
    public String fId;              //分店编号
    public String branchName;       //分店名称
    public String storeRoomId;      //库房编号--"A1103",
    public String storeRoomName;    //库房名称或地址--"金牛之心1103",
    public String dearClassId;      //null,
    public String dearClassName;    //null,
    public String oldGoodsId;       //旧货号--"1213",
    public String goodsId;          //新货号--"200710201",
    public float inPrice;           //进价--"30.00",
    public float originalPrice;     //原进价--"30.00",
    public float orderPrice;        //订货价--"30.00",
    public int amount;              //订单数量--20,订单数量
    public int sendAmount;          //实际发货数量--0,实际发货数量
    public String salePrice;        //null,
    public OrderClass orderType;    //订单类型--"统下",
    public String createTime;       //创建日期--"2020-07-12 11:50",
    public String inValidTime;      //订单过期时间--"2020-07-16",
    public String isPrint;          //已打印/未打印--"未打印",
    public String printTime;        //打印时间--"1900-01-01 00:00",
    public String inState;          //"供货商待接单",
    public String roomSendTime;     //库房收货时间---"1900-01-01",
    public String branchReceiveTime;//分店接收时间---"1900-01-01",
    public String roomReceiveTime;  //库房接收时间---"1900-01-01",
    public String sendId;           //"",
    public String remark;           //备注"",
    public String valid;            //"启用",
    public String img;              //图片
    public List<OrderPropertyRecord> propertyRecords;

    public boolean isExWarehouseScan = false;     //是否已经进行了出库扫描

    public OrderInformation(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id"))
            this.id = object.getString("id");
//            this.gId = object.getString("gId");
            if (object.has("sId"))
            this.sId = object.getString("sId");

            if (object.has("supplierId"))
            this.supplierId = object.getString("supplierId");
//            this.supplierName = object.getString("supplierName");
            if (object.has("goodsClassId"))
            this.goodsClassId = object.getString("goodsClassId");

            if (object.has("goodsClassName"))
            this.goodsClassName = object.getString("goodsClassName");

            if (object.has("branchId"))
            this.branchId = object.getString("branchId");
//            this.branchName = object.getString("branchName");

            if (object.has("fId"))
            this.fId = object.getString("fId");

            if (object.has("storeRoomId"))
            this.storeRoomId = object.getString("storeRoomId");

            if (object.has("storeRoomName"))
            this.storeRoomName = object.getString("storeRoomName");

            if (object.has("dearClassId"))
            this.dearClassId = object.getString("dearClassId");

            if (object.has("dearClassName"))
            this.dearClassName = object.getString("dearClassName");

            if (object.has("oldGoodsId"))
            this.oldGoodsId = object.getString("oldGoodsId");

            if (object.has("goodsId"))
            this.goodsId = object.getString("goodsId");
//            this.inPrice = Float.parseFloat(object.getString("inPrice"));
//            this.originalPrice = Float.parseFloat(object.getString("originalPrice"));
//            this.orderPrice = Float.parseFloat(object.getString("orderPrice"));
            if (object.has("amount"))
            this.amount = object.getInt("amount");

            if (object.has("sendAmount"))
            this.sendAmount = object.getInt("sendAmount");
//            this.salePrice = object.getString("salePrice");
//            this.img = object.getString("img");
            if (object.has("orderType"))
            this.orderType = new OrderClass(object.getString("orderType"));
            if (object.has("createTime"))
            this.createTime = object.getString("createTime");
            if (object.has("inValidTime"))
            this.inValidTime = object.getString("inValidTime");
            if (object.has("isPrint"))
            this.isPrint = object.getString("isPrint");
            if (object.has("printTime"))
            this.printTime = object.getString("printTime");
            if (object.has("inState"))
            this.inState = object.getString("inState");
            if (object.has("roomSendTime"))
            this.roomSendTime = object.getString("roomSendTime");
            if (object.has("branchReceiveTime"))
            this.branchReceiveTime = object.getString("branchReceiveTime");
            if (object.has("roomReceiveTime"))
            this.roomReceiveTime = object.getString("roomReceiveTime");
//            this.sendId = object.getString("sendId");
//            this.remark = object.getString("remark");
//            this.valid = object.getString("valid");
//            this.propertyRecords = new ArrayList<>();
//            JSONArray array = new JSONArray(object.getJSONArray("propertyRecord"));
//            for (int i = 0; i < array.length(); i++) {
//                this.propertyRecords.add(new OrderPropertyRecord(array.getString(i)));
//            }
            if (object.has("img"))
            this.img = object.getString("img");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
