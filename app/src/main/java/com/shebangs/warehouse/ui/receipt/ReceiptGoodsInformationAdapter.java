package com.shebangs.warehouse.ui.receipt;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shebangs.warehouse.R;

import java.util.List;

public class ReceiptGoodsInformationAdapter extends BaseAdapter {

    private Context context;
    private List<ReceiptGoodsInformation> informationList;

    public ReceiptGoodsInformationAdapter(Context context, List<ReceiptGoodsInformation> informationList) {
        this.context = context;
        this.informationList = informationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReceiptViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.receipt_list_item, null);
            viewHolder = new ReceiptViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReceiptViewHolder) convertView.getTag();
        }

        ReceiptGoodsInformation goods = informationList.get(position);

        //供应商编码
        String w0Value = "<font color=\"black\">" + context.getString(R.string.supplier_id) + context.getString(R.string.colon_zh) + goods.id + "</font>";
        viewHolder.w0.setText(Html.fromHtml(w0Value, Html.FROM_HTML_MODE_COMPACT));
        //片区，库房
        String w1Value = context.getString(R.string.area) + context.getString(R.string.colon_zh) + context.getString(R.string.area_prxd) +
                "\n" + context.getString(R.string.warehouse) + context.getString(R.string.colon_zh) + goods.warehouseID;
        viewHolder.w1.setText(w1Value);
        //下单属性，类别
        String w2Value = context.getString(R.string.order_class) + context.getString(R.string.colon_zh) + goods.orderClass +
                "\n" + context.getString(R.string.goods_class) + context.getString(R.string.colon_zh) + goods.goodsClass;
        viewHolder.w2.setText(w2Value);
        //旧货号，备注
        String w3Value = context.getString(R.string.old_goods_code) + context.getString(R.string.colon_zh) + goods.oldGoodsCode +
                "\n" + context.getString(R.string.warehouse_remark) + context.getString(R.string.colon_zh) + goods.remark;
        viewHolder.w3.setText(w3Value);

        //货物条码
        String b0Value = "<font color=\"black\">" + context.getString(R.string.goods_code) + context.getString(R.string.colon_zh) + goods.goodsCode + "</font>";
        viewHolder.b0.setText(Html.fromHtml(b0Value, Html.FROM_HTML_MODE_COMPACT));
        //分店编号
        String b1Value = context.getString(R.string.branch_code) + context.getString(R.string.colon_zh) + goods.shopID;
        viewHolder.b1.setText(b1Value);
        //下单日期，交货截至日期
        String b2Value = context.getString(R.string.order_date) + context.getString(R.string.colon_zh) + goods.orderTime +
                "\n" + context.getString(R.string.invalid_date) + context.getString(R.string.colon_zh) + goods.invalidTime;
        viewHolder.b2.setText(b2Value);
        //新货号，数量，尺码
        String b3Value = context.getString(R.string.new_goods_code) + context.getString(R.string.colon_zh) + goods.newGoodsCode +
                "\n" + context.getString(R.string.num) + context.getString(R.string.colon_zh) + goods.orderNum +
                "\n" + context.getString(R.string.size) + context.getString(R.string.colon_zh) + goods.size;
        viewHolder.b3.setText(b3Value);
        if (!goods.show) {
            viewHolder.b1.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
            viewHolder.w2.setVisibility(View.GONE);
            viewHolder.w3.setVisibility(View.GONE);
            viewHolder.b2.setVisibility(View.GONE);
            viewHolder.b3.setVisibility(View.GONE);
        } else {
            viewHolder.b1.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
            viewHolder.w2.setVisibility(View.VISIBLE);
            viewHolder.w3.setVisibility(View.VISIBLE);
            viewHolder.b2.setVisibility(View.VISIBLE);
            viewHolder.b3.setVisibility(View.VISIBLE);
        }

        viewHolder.b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.show) {
                    goods.show = false;
                    viewHolder.b1.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
                    viewHolder.w2.setVisibility(View.GONE);
                    viewHolder.w3.setVisibility(View.GONE);
                    viewHolder.b2.setVisibility(View.GONE);
                    viewHolder.b3.setVisibility(View.GONE);
                } else {
                    goods.show = true;
                    viewHolder.b1.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
                    viewHolder.w2.setVisibility(View.VISIBLE);
                    viewHolder.w3.setVisibility(View.VISIBLE);
                    viewHolder.b2.setVisibility(View.VISIBLE);
                    viewHolder.b3.setVisibility(View.VISIBLE);
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return this.informationList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.informationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ReceiptViewHolder {
        private TextView w0, w1, w2, w3;
        private TextView b0, b1, b2, b3;

        private ReceiptViewHolder(View view){
            this.w0 = view.findViewById(R.id.w0);
            this.w1 = view.findViewById(R.id.w1);
            this.w2 = view.findViewById(R.id.w2);
            this.w3 = view.findViewById(R.id.w3);

            this.b0 = view.findViewById(R.id.b0);
            this.b1 = view.findViewById(R.id.b1);
            this.b2 = view.findViewById(R.id.b2);
            this.b3 = view.findViewById(R.id.b3);
        }
    }
}
