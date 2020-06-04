package com.shebangs.warehouse.hardware.scanner;

import android.content.Context;

public abstract class Scanner {

    protected OnScannerScanResultListener mListener;
    protected Context mContext;

    public Scanner(Context context) {
        this.mContext = context;
    }

    public abstract void scannerOpen();

    public abstract void scannerSuspend();

    public abstract void scannerClose();

    public void setOnScannerScanResultListener(OnScannerScanResultListener listener) {
        this.mListener = listener;
    }

    public interface OnScannerScanResultListener {
        void scanResult(String scan);
    }
}
