package com.shebangs.warehouse.hardware.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;

public class PPdaScanner extends Scanner {

    private ScanManager mScanManager;
    int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID.WEDGE_INTENT_DATA_STRING_TAG};
    int[] idmodebuf = new int[]{PropertyID.WEDGE_KEYBOARD_ENABLE, PropertyID.TRIGGERING_MODES};
    String[] action_value_buf = new String[]{ScanManager.ACTION_DECODE, ScanManager.BARCODE_STRING_TAG};
    int[] idmode;

    public PPdaScanner(Context context) {
        super(context);
    }

    @Override
    public void scannerOpen() {

        mScanManager = new ScanManager();
        mScanManager.openScanner();

        action_value_buf = mScanManager.getParameterString(idbuf);
        idmode = mScanManager.getParameterInts(idmodebuf);

        IntentFilter filter = new IntentFilter();
        action_value_buf = mScanManager.getParameterString(idbuf);
        filter.addAction(action_value_buf[0]);
        super.mContext.registerReceiver(mScanReceiver, filter);
    }

    @Override
    public void scannerSuspend() {
        super.mContext.unregisterReceiver(mScanReceiver);
    }

    @Override
    public void scannerClose() {
        if (mScanManager != null) {
            mScanManager.closeScanner();
        }
    }


    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            String result = intent.getStringExtra(action_value_buf[1]);

            if (result != null) {
                if (mListener != null) {
                    mListener.scanResult(result);
                }
            }
        }
    };
}
