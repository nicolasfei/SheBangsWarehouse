package com.shebangs.warehouse.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderPropertyRecord implements Parcelable {

    public String id;//"5555",
    public String orderId;//"222",
    public String color;//"卡其色",
    public String size;//"M",
    public int val;//10

    public String actualColor;        //实际颜色
    public String actualSize;         //实际尺码
    public int actualNum;             //实际件数

    public OrderPropertyRecord() {
        this.id = "4444";
        this.orderId = "222";
        this.color = "白色";
        this.size = "S";
        this.val = 10;
        this.actualNum = this.val;
        this.actualColor = this.color;
        this.actualSize = this.size;
    }

    public OrderPropertyRecord(String json) {
        try {
            JSONObject object = new JSONObject(json);
            this.id = object.getString("id");
            this.orderId = object.getString("orderId");
            this.color = object.getString("color");
            this.size = object.getString("size");
            this.val = object.getInt("val");
            this.actualNum = this.val;
            this.actualColor = this.color;
            this.actualSize = this.size;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected OrderPropertyRecord(Parcel in) {
        id = in.readString();
        orderId = in.readString();
        color = in.readString();
        size = in.readString();
        val = in.readInt();
        actualColor = in.readString();
        actualSize = in.readString();
        actualNum = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(orderId);
        dest.writeString(color);
        dest.writeString(size);
        dest.writeInt(val);
        dest.writeString(actualColor);
        dest.writeString(actualSize);
        dest.writeInt(actualNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderPropertyRecord> CREATOR = new Creator<OrderPropertyRecord>() {
        @Override
        public OrderPropertyRecord createFromParcel(Parcel in) {
            return new OrderPropertyRecord(in);
        }

        @Override
        public OrderPropertyRecord[] newArray(int size) {
            return new OrderPropertyRecord[size];
        }
    };
}
