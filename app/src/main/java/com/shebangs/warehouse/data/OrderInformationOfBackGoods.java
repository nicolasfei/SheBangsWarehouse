package com.shebangs.warehouse.data;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 返货订单信息
 */
public class OrderInformationOfBackGoods {
    public String id = "";             //"170228B08299",
    public String goodsOrderId = "";   //"",
    public String sId = "";            //"A001",
    public String supplierId = "";     //null,
    public String supplierName = "";   //null,
    public String goodsClassId = "";   //"4",
    public String goodsClassName = ""; //"牛仔裤",
    public String fId = "";            //"ZA0062",
    public String branchId = "";       //null,
    public String branchName = "";     //null,
    public String img = "";            //"https://file.scdawn.com/cloud/goodsImg/A001/牛仔裤/7022689/1702260917446568E.jpg",
    public String goodsId = "";        //"7022689",
    public String oldGoodsId = "";     //"2828/6991",
    public String goodsType = "";      //"普通",
    public String inPrice = "";        //0,
    public String originalPrice = "";  //0,
    public String state = "";          //"正常"
    public Bitmap photo;

    public OrderInformationOfBackGoods(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("id")) {
                this.id = object.getString("id");
            }
            if (object.has("goodsOrderId")) {
                this.goodsOrderId = object.getString("goodsOrderId");
            }
            if (object.has("sId")) {
                this.sId = object.getString("sId");
            }
            if (object.has("supplierId")) {
                this.supplierId = object.getString("supplierId");
            }
            if (object.has("supplierName")) {
                this.supplierName = object.getString("supplierName");
            }
            if (object.has("goodsClassId")) {
                this.goodsClassId = object.getString("goodsClassId");
            }
            if (object.has("goodsClassName")) {
                this.goodsClassName = object.getString("goodsClassName");
            }
            if (object.has("fId")) {
                this.fId = object.getString("fId");
            }
            if (object.has("branchId")) {
                this.branchId = object.getString("branchId");
            }
            if (object.has("branchName")) {
                this.branchName = object.getString("branchName");
            }
            if (object.has("img")) {
                this.img = object.getString("img");
            }
            if (object.has("goodsId")) {
                this.goodsId = object.getString("goodsId");
            }
            if (object.has("oldGoodsId")) {
                this.oldGoodsId = object.getString("oldGoodsId");
            }
            if (object.has("goodsType")) {
                this.goodsType = object.getString("goodsType");
            }
            if (object.has("inPrice")) {
                this.inPrice = object.getString("inPrice");
            }
            if (object.has("originalPrice")) {
                this.originalPrice = object.getString("originalPrice");
            }
            if (object.has("state")) {
                this.state = object.getString("state");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
