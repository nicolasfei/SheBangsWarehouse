package com.shebangs.warehouse.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRecord {

    private static UserRecord record = new UserRecord();
    private Context mContext;

    private UserRecord() {

    }

    public static UserRecord getInstance() {
        return record;
    }

    public void init(Context context) {
        this.mContext = context;
    }

    public void recordUser(String user) {
        SharedPreferences preferences = this.mContext.getSharedPreferences("userRecord", Context.MODE_PRIVATE);
        if (!preferences.contains(user)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(user, user);
            editor.apply();
        }
    }

    public List<String> getRecords() {
        SharedPreferences preferences = this.mContext.getSharedPreferences("userRecord", Context.MODE_PRIVATE);
        Map<String, ?> values = preferences.getAll();
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, ?> entry : values.entrySet()) {
            list.add((String) entry.getValue());
        }
        return list;
    }
}
