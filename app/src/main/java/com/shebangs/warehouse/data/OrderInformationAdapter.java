package com.shebangs.warehouse.data;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shebangs.warehouse.R;

import java.util.List;

public class OrderInformationAdapter extends BaseAdapter {
    private List<OrderInformation> goods;
    private Context mContext;

    public OrderInformationAdapter(Context context, List<OrderInformation> goods) {
        this.mContext = context;
        this.goods = goods;
    }

    @Override
    public int getCount() {
        return this.goods == null ? 0 : this.goods.size();
    }

    @Override
    public Object getItem(int position) {
        return this.goods == null ? null : this.goods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.receipt_order_show, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OrderInformation order = goods.get(position);
        //加载图片
//        new ImageLoadClass(mContext, holder.photo, order.img).load();
        //显示数据
        String supplierIdValue = mContext.getString(R.string.supplier_id) + mContext.getString(R.string.colon_zh) + order.sId;
        holder.supplierId.setText(Html.fromHtml(supplierIdValue, Html.FROM_HTML_MODE_COMPACT));

        String storeRoomNameValue = mContext.getString(R.string.storeRoomName) + mContext.getString(R.string.colon_zh) + order.storeRoomName;
        holder.storeRoomName.setText(Html.fromHtml(storeRoomNameValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsClassNameValue = mContext.getString(R.string.goodsClassName) + mContext.getString(R.string.colon_zh) + order.goodsClassName;
        holder.goodsClassName.setText(Html.fromHtml(goodsClassNameValue, Html.FROM_HTML_MODE_COMPACT));

        String fIdValue = mContext.getString(R.string.branchName) + mContext.getString(R.string.colon_zh) + "<font color=\"black\"><big>" + order.fId + "</big></font>";
        holder.fId.setText(Html.fromHtml(fIdValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.new_goods_code1) + mContext.getString(R.string.colon_zh) + order.goodsId;
        holder.goodsId.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String oldGoodsIdValue = mContext.getString(R.string.old_goods_code1) + mContext.getString(R.string.colon_zh) + order.oldGoodsId;
        holder.oldGoodsId.setText(Html.fromHtml(oldGoodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String inValidTimeValue = mContext.getString(R.string.invalidData) + mContext.getString(R.string.colon_zh) + order.inValidTime;
        holder.inValidTime.setText(Html.fromHtml(inValidTimeValue, Html.FROM_HTML_MODE_COMPACT));

        String sendAmountValue = mContext.getString(R.string.sendAmount) + mContext.getString(R.string.colon_zh) + order.sendAmount;
        holder.sendAmount.setText(Html.fromHtml(sendAmountValue, Html.FROM_HTML_MODE_COMPACT));

        if (order.isExWarehouseScan) {
            convertView.setBackgroundColor(Color.GREEN);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView supplierId;
        private TextView storeRoomName;
        private TextView goodsClassName;
        private TextView oldGoodsId;
        private TextView goodsId;
        private TextView fId;
        private TextView inValidTime;
        private TextView sendAmount;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);
            this.supplierId = root.findViewById(R.id.supplierId);
            this.storeRoomName = root.findViewById(R.id.storeRoomName);
            this.goodsClassName = root.findViewById(R.id.goodsClassName);
            this.oldGoodsId = root.findViewById(R.id.oldGoodsId);
            this.sendAmount = root.findViewById(R.id.sendAmount);
            this.goodsId = root.findViewById(R.id.goodsId);
            this.fId = root.findViewById(R.id.fId);
            this.inValidTime = root.findViewById(R.id.inValidTime);
        }
    }
}
