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

public class ShipmentGoodsStatisticsAdapter extends BaseAdapter {
    private Context mContext;
    private List<ShipmentGoodsStatistics> statistics;
    private OnShipmentGoodsStatisticsClickListener listener;

    //上一次点击时间---点击防抖
    private static long lastClickTime = 0;
    private static final int INTERVAL_TIME = 600;

    public ShipmentGoodsStatisticsAdapter(Context context, List<ShipmentGoodsStatistics> statistics) {
        this.mContext = context;
        this.statistics = statistics;
    }

    public void setOnShipmentGoodsStatisticsClickListener(OnShipmentGoodsStatisticsClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return this.statistics == null ? 0 : this.statistics.size();
    }

    @Override
    public Object getItem(int position) {
        return this.statistics == null ? null : this.statistics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.shipment_goods_statistics_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ShipmentGoodsStatistics statistic = this.statistics.get(position);

        String supplierValue = statistic.supplier;
        holder.supplier.setText(Html.fromHtml(supplierValue, Html.FROM_HTML_MODE_COMPACT));

        String statisticValue = String.valueOf(statistic.statistic);
        holder.statistic.setText(Html.fromHtml(statisticValue, Html.FROM_HTML_MODE_COMPACT));
        if (statistic.toBeScanned) {        //订单待扫描
            holder.statistic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (System.currentTimeMillis() - lastClickTime > INTERVAL_TIME) {
                        if (listener != null) {
                            listener.onNotScannedClick(position);
                        }
                        lastClickTime = System.currentTimeMillis();
                    }
                }
            });
            holder.statistic.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mContext.getDrawable(R.drawable.ic_right_arrow), null);
            convertView.setBackgroundColor(Color.RED);
        } else {                        //订单已经扫描
            holder.statistic.setOnClickListener(null);
            holder.statistic.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            convertView.setBackgroundColor(Color.GREEN);
        }
        return convertView;
    }

    private static class ViewHolder {
        private TextView supplier;
        private TextView statistic;

        private ViewHolder(View root) {
            this.supplier = root.findViewById(R.id.supplier);
            this.statistic = root.findViewById(R.id.statistic);
        }
    }

    public interface OnShipmentGoodsStatisticsClickListener {
        void onNotScannedClick(int position);
    }
}
