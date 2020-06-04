package com.shebangs.warehouse.ui.receipt;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shebangs.warehouse.R;

import java.util.List;

public class ReceiptGoodsInformationAdapter1 extends BaseAdapter {
    private Context context;
    private List<ReceiptGoodsInformation> informationList;

    public ReceiptGoodsInformationAdapter1(Context context, List<ReceiptGoodsInformation> informationList) {
        this.context = context;
        this.informationList = informationList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ReceiptGoodsInformationAdapter1.ReceiptViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.receipt_list_item1, null);
            viewHolder = new ReceiptGoodsInformationAdapter1.ReceiptViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ReceiptGoodsInformationAdapter1.ReceiptViewHolder) convertView.getTag();
        }

        ReceiptGoodsInformation goods = informationList.get(position);

        //供应商编码
        String w0Value = "<font color=\"black\">" + context.getString(R.string.supplier_id1) + context.getString(R.string.colon_zh) + goods.id + "</font>";
        viewHolder.w0.setText(Html.fromHtml(w0Value, Html.FROM_HTML_MODE_COMPACT));
        //分店号
        String w1Value = context.getString(R.string.branch_code1) + context.getString(R.string.colon_zh) + goods.shopID;
        viewHolder.w1.setText(w1Value);
        //数量
        String w2Value = context.getString(R.string.num1) + context.getString(R.string.colon_zh) + goods.orderNum;
        viewHolder.w2.setText(w2Value);

        //旧货号
        String g0Value = context.getString(R.string.old_goods_code1) + context.getString(R.string.colon_zh) + goods.oldGoodsCode;
        viewHolder.g0.setText(g0Value);
        //新货号
        String g1Value = context.getString(R.string.new_goods_code1) + context.getString(R.string.colon_zh) + goods.newGoodsCode;
        viewHolder.g1.setText(g1Value);
        //类别
        String g2Value = context.getString(R.string.goods_class1) + context.getString(R.string.colon_zh) + goods.goodsClass;
        viewHolder.g2.setText(g2Value);

        //下单日期
        String b0Value = context.getString(R.string.order_date1) + context.getString(R.string.colon_zh) + goods.orderTime;
        viewHolder.b0.setText(b0Value);
        //交货截至日期
        String b1Value = context.getString(R.string.invalid_date1) + context.getString(R.string.colon_zh) + goods.invalidTime;
        viewHolder.b1.setText(b1Value);
        //尺码
        String b2Value = context.getString(R.string.size1) + context.getString(R.string.colon_zh) + goods.size;
        viewHolder.b2.setText(b2Value);

        //片区
        String h0Value = context.getString(R.string.area1) + context.getString(R.string.colon_zh) + goods.area;
        viewHolder.h0.setText(h0Value);
        //库房
        String h1Value = context.getString(R.string.warehouse1) + context.getString(R.string.colon_zh) + goods.warehouseID;
        viewHolder.h1.setText(h1Value);
        //属性
        String h2Value = context.getString(R.string.order_class1) + context.getString(R.string.colon_zh) + goods.orderClass;
        viewHolder.h2.setText(h2Value);

        //备注
        String remark = context.getString(R.string.remark) + context.getString(R.string.colon_zh) + goods.remark;

        if (!goods.show) {
            viewHolder.g2.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
            viewHolder.b.setVisibility(View.GONE);
            viewHolder.h.setVisibility(View.GONE);
            viewHolder.remark.setVisibility(View.GONE);
        } else {
            viewHolder.g2.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
            viewHolder.b.setVisibility(View.VISIBLE);
            viewHolder.h.setVisibility(View.VISIBLE);
            viewHolder.remark.setVisibility(View.VISIBLE);
        }

        viewHolder.g2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (goods.show) {
                    goods.show = false;
                    viewHolder.g2.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_down), null);
                    viewHolder.b.setVisibility(View.GONE);
                    viewHolder.h.setVisibility(View.GONE);
                    viewHolder.remark.setVisibility(View.GONE);
                } else {
                    goods.show = true;
                    viewHolder.g2.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_sj_up), null);
                    viewHolder.b.setVisibility(View.VISIBLE);
                    viewHolder.h.setVisibility(View.VISIBLE);
                    viewHolder.remark.setVisibility(View.VISIBLE);
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
        private TextView w0, w1, w2;
        private LinearLayout w;
        private TextView g0, g1, g2;
        private LinearLayout g;
        private TextView b0, b1, b2;
        private LinearLayout b;
        private TextView h0, h1, h2;
        private LinearLayout h;
        private TextView remark;

        private ReceiptViewHolder(View view) {
            this.w0 = view.findViewById(R.id.w0);
            this.w1 = view.findViewById(R.id.w1);
            this.w2 = view.findViewById(R.id.w2);
            this.w = view.findViewById(R.id.w);

            this.g0 = view.findViewById(R.id.g0);
            this.g1 = view.findViewById(R.id.g1);
            this.g2 = view.findViewById(R.id.g2);
            this.g = view.findViewById(R.id.g);

            this.b0 = view.findViewById(R.id.b0);
            this.b1 = view.findViewById(R.id.b1);
            this.b2 = view.findViewById(R.id.b2);
            this.b = view.findViewById(R.id.b);

            this.h0 = view.findViewById(R.id.h0);
            this.h1 = view.findViewById(R.id.h1);
            this.h2 = view.findViewById(R.id.h2);
            this.h = view.findViewById(R.id.h);

            this.remark = view.findViewById(R.id.remark);
        }
    }
}
