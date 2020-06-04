package com.shebangs.warehouse.warehouse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WarehouseKeeper {

    private static final WarehouseKeeper keeper = new WarehouseKeeper();

    public String loginName;            //登陆用户名
    public String loginPassword;        //登陆密码
    public String userId;               //库房ID
    public String userKey;              //登陆成功后返回key

    public WarehouseInformation information;        //库房信息
    public List<WarehouseStaff> staffs;             //店铺导购
    public WarehouseStaff onDutyStaff;              //值班人员

    private WarehouseKeeper() {
        this.staffs = new ArrayList<>();
    }

    public static WarehouseKeeper getInstance() {
        return keeper;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setInformation(String data) throws JSONException {
        this.information = new WarehouseInformation(data);
    }

    public void setOnDutyStaff(String name) {
        if (this.staffs != null) {
            for (WarehouseStaff g : this.staffs) {
                if (g.name.equals(name)) {
                    this.onDutyStaff = g;
                    break;
                }
            }
        }
    }

    public void setInformation(WarehouseInformation branchInformation) {
        this.information = branchInformation;
    }

    public void setStaffs(List<WarehouseStaff> staffs) {
        this.staffs = staffs;
    }

    public String[] getStaffs() {
        String[] guideNames = null;
        if (this.staffs != null) {
            guideNames = new String[this.staffs.size()];
            for (int i = 0; i < this.staffs.size(); i++) {
                guideNames[i] = this.staffs.get(i).name;
            }
        }
        return guideNames;
    }

    public int getOnDutyStaffPosition() {
        if (this.staffs != null) {
            for (int i = 0; i < this.staffs.size(); i++) {
                if (this.staffs.get(i).name.equals(this.onDutyStaff.name)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * 库房名集合
     * @return 库房名集合
     */
    public String[] getWarehouses() {
        return new String[0];
    }
}
