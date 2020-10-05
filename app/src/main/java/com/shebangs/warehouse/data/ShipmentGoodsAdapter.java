package com.shebangs.warehouse.data;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shebangs.warehouse.R;

import java.util.List;

public class ShipmentGoodsAdapter extends BaseAdapter {

    private List<OrderInformation> goods;
    private Context mContext;

    public ShipmentGoodsAdapter(Context context, List<OrderInformation> goods) {
        this.mContext = context;
        this.goods = goods;
    }

    @Override
    public int getCount() {
        return goods == null ? 0 : goods.size();
    }

    @Override
    public Object getItem(int position) {
        return goods == null ? null : goods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shipment_goods_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderInformation order = goods.get(position);

        //供应商
//        String supplierIdValue = mContext.getString(R.string.supplier) + mContext.getString(R.string.colon_zh) + order.sId;
        String supplierIdValue = order.sId + "---" + order.goodsId;
        holder.supplier.setText(Html.fromHtml(supplierIdValue, Html.FROM_HTML_MODE_COMPACT));
        //新货号
        String goodsIdValue = mContext.getString(R.string.new_goods_code1) + mContext.getString(R.string.colon_zh) + order.goodsId;
        holder.goodsID.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        //设置背景颜色
        if (order.isExWarehouseScan) {
            convertView.setBackgroundColor(Color.GREEN);
        } else {
            convertView.setBackgroundColor(Color.RED);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView supplier;      //供应商
        private TextView goodsID;       //新货号

        private ViewHolder(View root) {
            this.supplier = root.findViewById(R.id.supplier);
            this.goodsID = root.findViewById(R.id.goodsId);
        }
    }
}
