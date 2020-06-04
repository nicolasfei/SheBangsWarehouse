package com.shebangs.warehouse.ui.receipt;

public class ReceiptGoodsInformation {
    public String id = "";                  //编号
    public String shopID = "";              //店铺ID
    public String warehouseID = "";         //库房
    public String gearClassID = "";         //挡位类别ID
    public String goodsCode = "";           //货号
    public String goodsClass = "";          //类别
    public String oldGoodsCode = "";        //旧货号
    public String newGoodsCode = "";        //新货号
    public String size = "";                //尺码
    public float bid = 0;                   //进价
    public float OriginalBid = 0;           //原进价
    public float orderPrice = 0;            //订货价
    public int orderNum = 0;                //下单数量
    public float salePrice = 0;             //销售价
    public String orderClass = "";          //下单类型（统下单All/首单first/补货单repair）
    public String orderTime = "";           //下单时间
    public String invalidTime = "";         //订单过期时间
    public String isPrint = "";             //是否已经打印
    public String printTime = "";           //打印时间
    public String orderStatus = "";         //订单状态（swait供应商待接单/swaited供应商已接单/roomreceive库房已收货/roomsend库房已发货/branchreceive分店已收货）
    public String receiptTime = "";         //库房收货时间
    public String shipmentTime = "";        //库房发货时间
    public String shipmentBatch = "";       //发货批次（大码）
    public String remark = "";              //备注
    public boolean status = true;           //启用/禁用
    public boolean show = false;            //显示/隐藏细节
    public String area = "";                //片区

    public ReceiptGoodsInformation() {

    }

    public static ReceiptGoodsInformation getTestData() {
        ReceiptGoodsInformation goods = new ReceiptGoodsInformation();
        goods.id = "9079";
        goods.shopID = "JM12";
        goods.warehouseID = "JM库房";
        goods.goodsCode = "98765";
        goods.goodsClass = "裙子";
        goods.oldGoodsCode = "785";
        goods.newGoodsCode = "9122365B";
        goods.size = "M";
        goods.orderPrice = 12.00f;
        goods.orderNum = 100;
        goods.orderClass = "统下单";
        goods.orderTime = "12-03";
        goods.invalidTime = "01-28";
        goods.remark = "这单备注了什么";
        goods.area = "连锁事业部";
        goods.show = false;
        return goods;
    }
}
