package com.shebangs.warehouse.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nicolas.toollibrary.dcode.DCode;
import com.shebangs.warehouse.R;
import com.shebangs.warehouse.ui.receipt.ReceiptVoucher;

public class ReceiveBillView extends LinearLayout {
    private static String TAG = "ReceiveBillView";
    private ReceiptVoucher voucher;
    private Context mContext;

    private TextView date, supplierID, warehouse, staff, total;
    private ImageView dcode;
    private GridView branchList;

    public ReceiveBillView(Context context) {
        super(context);
        this.mContext = context;
        this.voucher = new ReceiptVoucher();
        initView();
        initData();
    }

    public ReceiveBillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.voucher = new ReceiptVoucher();
        initView();
        initData();
    }

    public ReceiveBillView(Context context, ReceiptVoucher voucher) {
        super(context);
        this.mContext = context;
        this.voucher = voucher;
        initView();
        initData();
    }


    private void initView() {
        View root = LayoutInflater.from(mContext).inflate(R.layout.view_receive_bill, this);
        this.date = root.findViewById(R.id.date);
        this.supplierID = root.findViewById(R.id.supplierID);
        this.warehouse = root.findViewById(R.id.warehouse);
        this.staff = root.findViewById(R.id.staff);
        this.total = root.findViewById(R.id.total);
        this.dcode = root.findViewById(R.id.dcode);
        this.branchList = root.findViewById(R.id.branchList);
    }

    private void initData() {
        if (this.voucher != null) {
            Log.d(TAG, "initData: " + voucher.code + "--" + this.dcode.getWidth() + "--" + this.dcode.getHeight());
            Bitmap code = DCode.createOneDCode(voucher.code, 160, 80);
            if (code != null) {
                this.dcode.setImageBitmap(code);
            }
            String dateV = this.mContext.getString(R.string.receive_date) + this.mContext.getString(R.string.colon_zh) + voucher.date;
            this.date.setText(dateV);

            String supplierIDV = this.mContext.getString(R.string.supplier_id) + this.mContext.getString(R.string.colon_zh) + voucher.supplier;
            this.supplierID.setText(supplierIDV);

            String warehouseV = this.mContext.getString(R.string.warehouse_id) + this.mContext.getString(R.string.colon_zh) + voucher.warehouse;
            this.warehouse.setText(warehouseV);

            String staffV = this.mContext.getString(R.string.receiptStaff) + this.mContext.getString(R.string.colon_zh) + voucher.receiptStaff;
            this.staff.setText(staffV);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.mContext, android.R.layout.simple_list_item_1, voucher.fIDs);
            this.branchList.setAdapter(adapter);

            String totalV = this.mContext.getString(R.string.receipt_total) + this.mContext.getString(R.string.colon_zh) + voucher.fIDs.size();
            this.total.setText(totalV);
        }
    }
}
