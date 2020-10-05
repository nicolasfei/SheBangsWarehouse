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

import com.nicolas.toollibrary.imageload.ImageLoadClass;
import com.shebangs.warehouse.R;

import java.util.List;

public class OrderInformationOfBackGoodsAdapter extends BaseAdapter {
    private List<OrderInformationOfBackGoods> goods;
    private Context mContext;
    private boolean isBusy = false;          //表示list view是否在快速滑动

    public OrderInformationOfBackGoodsAdapter(Context context, List<OrderInformationOfBackGoods> goods) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.goods_back_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        OrderInformationOfBackGoods order = goods.get(position);

        //加载图片
        if (!isBusy) {
            ImageLoadClass.getInstance().displayImage(order.img, holder.photo, false);
        } else {
            ImageLoadClass.getInstance().displayImage(order.img, holder.photo, true);
        }

        //显示数据
        String supplierIdValue = mContext.getString(R.string.supplier_id) + mContext.getString(R.string.colon_zh) + order.sId;
        holder.supplierId.setText(Html.fromHtml(supplierIdValue, Html.FROM_HTML_MODE_COMPACT));

        //String stateValue = mContext.getString(R.string.inState) + mContext.getString(R.string.colon_zh) + order.state;
        holder.state.setText(Html.fromHtml(order.state, Html.FROM_HTML_MODE_COMPACT));
        holder.state.setBackgroundColor(Color.YELLOW);

        String idValue = mContext.getString(R.string.code) + mContext.getString(R.string.colon_zh) + order.id;
        holder.id.setText(Html.fromHtml(idValue, Html.FROM_HTML_MODE_COMPACT));

        String fIdValue = mContext.getString(R.string.branchName) + mContext.getString(R.string.colon_zh) + order.fId;
        holder.fId.setText(Html.fromHtml(fIdValue, Html.FROM_HTML_MODE_COMPACT));

        String oldGoodsIdValue = mContext.getString(R.string.old_goods_code1) + mContext.getString(R.string.colon_zh) + order.oldGoodsId;
        holder.oldGoodsId.setText(Html.fromHtml(oldGoodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsIdValue = mContext.getString(R.string.new_goods_code1) + mContext.getString(R.string.colon_zh) + order.goodsId;
        holder.goodsId.setText(Html.fromHtml(goodsIdValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsTypeValue = mContext.getString(R.string.goodsType) + mContext.getString(R.string.colon_zh) + order.goodsType;
        holder.goodsType.setText(Html.fromHtml(goodsTypeValue, Html.FROM_HTML_MODE_COMPACT));

        String goodsClassNameValue = mContext.getString(R.string.goods_class_type) + mContext.getString(R.string.colon_zh) + order.goodsClassName;
        holder.goodsClassName.setText(Html.fromHtml(goodsClassNameValue, Html.FROM_HTML_MODE_COMPACT));

        return convertView;
    }

    private static class ViewHolder {
        private ImageView photo;
        private TextView supplierId, state;
        private TextView fId, goodsClassName, id;
        private TextView goodsId, oldGoodsId, goodsType;

        private ViewHolder(View root) {
            this.photo = root.findViewById(R.id.photo);
            this.supplierId = root.findViewById(R.id.supplierId);
            this.state = root.findViewById(R.id.state);

            this.fId = root.findViewById(R.id.fId);
            this.goodsClassName = root.findViewById(R.id.goodsClassName);
            this.id = root.findViewById(R.id.id);

            this.goodsId = root.findViewById(R.id.goodsId);
            this.oldGoodsId = root.findViewById(R.id.oldGoodsId);
            this.goodsType = root.findViewById(R.id.goodsType);
        }
    }
}
