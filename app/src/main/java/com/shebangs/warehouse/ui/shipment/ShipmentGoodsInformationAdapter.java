package com.shebangs.warehouse.ui.shipment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.shebangs.warehouse.R;

import java.util.List;

public class ShipmentGoodsInformationAdapter extends BaseAdapter {

    private List<ShipmentGoodsInformation> goodsInformationList;
    private Context context;

    public ShipmentGoodsInformationAdapter(Context context, List<ShipmentGoodsInformation> list) {
        this.context = context;
        this.goodsInformationList = list;
    }

    @Override
    public int getCount() {
        return goodsInformationList == null ? 0 : goodsInformationList.size();
    }

    @Override
    public Object getItem(int position) {
        return goodsInformationList == null ? null : goodsInformationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView = LayoutInflater.from(this.context).inflate(R.layout.shipment_list_item,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder{

        private ViewHolder(View root){

        }
    }
}
