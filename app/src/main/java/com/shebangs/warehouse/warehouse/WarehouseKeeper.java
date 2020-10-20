package com.shebangs.warehouse.warehouse;

import android.text.TextUtils;
import android.util.Log;

import com.nicolas.componentlibrary.multileveltree.TreeNode;
import com.nicolas.componentlibrary.pullrefreshswipemenu.PullRefreshMenuItem;
import com.nicolas.toollibrary.Tool;
import com.shebangs.warehouse.data.BranchInformation;
import com.shebangs.warehouse.data.StorehouseInformation;
import com.shebangs.warehouse.serverInterface.CommandVo;
import com.shebangs.warehouse.serverInterface.Invoker;
import com.shebangs.warehouse.serverInterface.InvokerHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WarehouseKeeper {
    private static String TAG = "WarehouseKeeper";
    private static final WarehouseKeeper keeper = new WarehouseKeeper();

    private String loginName;            //登陆用户名
    private String loginPassword;        //登陆密码
    private String token;                //登陆成功后返回key

    private List<StorehouseInformation> warehouses;     //库房信息
    private TreeNode warehouseTree;                     //库房信息树形结构
    private WarehouseStaff onDutyStaff;                 //值班人员
    private StorehouseInformation onDutyWarehouse;      //值班人员所在库房

    private List<BranchInformation> branchList;     //分店信息
    private List<String> branchCodeList;            //分店编号信息

    private InformationObserveTask timerTask;       //信息定时查询任务


    private WarehouseKeeper() {
        onDutyStaff = new WarehouseStaff();
        onDutyWarehouse = new StorehouseInformation();
        this.warehouses = new ArrayList<>();
        this.branchList = new ArrayList<>();
        this.branchCodeList = new ArrayList<>();
        this.timerTask = new InformationObserveTask();
    }

    /**
     * 单例接口
     *
     * @return WarehouseKeeper
     */
    public static WarehouseKeeper getInstance() {
        return keeper;
    }

    /**
     * 设置登陆名
     *
     * @param loginName 用户名
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * 设置密码
     *
     * @param loginPassword 登陆密码
     */
    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    /**
     * 获取登录名
     *
     * @return 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 获取登陆密码
     *
     * @return 密码
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * 设置token
     *
     * @param token token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 获取token
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * 设置库房信息
     *
     * @param data     list数据
     * @param jsonData 树形数据
     * @throws JSONException JSONException
     */
    public void setWarehouseInformation(String data, String jsonData) throws JSONException {
        this.warehouses.clear();
        JSONArray array = new JSONArray(data);
        for (int i = 0; i < array.length(); i++) {
            StorehouseInformation house = new StorehouseInformation(array.getString(i));
            if (house.pId.compareTo("1") > 0) {
                this.warehouses.add(house);
            }

        }
        Tool.longPrint(jsonData);
        this.warehouseTree = StorehouseInformation.buildTree(jsonData);
    }

    /**
     * 库房名字列表
     *
     * @return 名字列表
     */
    public String[] getWarehouseInformationList() {
        String[] warehouses = new String[this.warehouses.size()];
        int i = 0;
        for (StorehouseInformation attr : this.warehouses) {
            warehouses[i] = attr.name;
            i++;
        }
        return warehouses;
    }

    /**
     * 获取库房集合
     *
     * @return 库房集合
     */
    public List<StorehouseInformation> getWarehouseName() {
        return warehouses;
    }

    /**
     * 获取库房树形数据
     *
     * @return 树形数据
     */
    public TreeNode getWarehouseTree() {
        synchronized (WarehouseKeeper.class) {
            return warehouseTree;
        }
    }

    /**
     * 设置在岗人员
     *
     * @param json json
     * @throws JSONException JSONException
     */
    public void setOnDutyStaff(String json) throws JSONException {
        this.onDutyStaff = new WarehouseStaff(json);
    }

    /**
     * 库房人员信息
     *
     * @return 人员信息
     */
    public WarehouseStaff getOnDutyStaff() {
        return onDutyStaff;
    }

    /**
     * 获取在岗人员信息
     *
     * @return 人员信息
     */
    public String getOnDutyStaffName() {
        return onDutyStaff == null ? "" : onDutyStaff.name;
    }

    /**
     * 设置刚在人员所在库房
     *
     * @param warehouse 所在库房名
     */
    public void setOnDutyWarehouse(String warehouse) {
        for (StorehouseInformation attr : this.warehouses) {
            if (attr.name.equals(warehouse)) {
                this.onDutyWarehouse = attr;
                break;
            }
        }
    }

    /**
     * 获取在岗人员所在库房
     *
     * @return 所在库房
     */
    public StorehouseInformation getOnDutyWarehouse() {
        return onDutyWarehouse;
    }

    /**
     * 开始定时查询任务
     */
    public void startTimerTask() {
        this.timerTask.start();
    }

    /**
     * 退出定时查询任务
     */
    public void cancelTimerTask() {
        this.timerTask.cancel();
    }

    /**
     * 获取分店id list
     *
     * @return list
     */
    public List<String> getBranchList() {
        return branchCodeList;
    }

    /**
     * 根据分店Fid获取分店ID
     *
     * @param branchFID branchFID
     * @return 分店ID
     */
    public String getBranchID(String branchFID) {
        for (BranchInformation branch : branchList) {
            if (branch.fId.equals(branchFID)) {
                return branch.id;
            }
        }
        return null;
    }

    /**
     * 查询库房，分店信息数据类
     */
    private class InformationObserveTask {
        private long PERIOD = 300 * 60 * 1000;       //30分钟执行一次
        private Timer timer;

        private void start() {
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //查询库房
                    CommandVo storehouseVo = InvokerHandler.getInstance().getStorehouseInformationCommand();
                    String storehouse = Invoker.getInstance().synchronousExec(storehouseVo);
                    TreeNode storeTree;
                    try {
                        JSONObject object = new JSONObject(storehouse);
                        if (object.getBoolean("success")) {
                            if (object.has("jsonData")) {
                                synchronized (WarehouseKeeper.class) {
                                    warehouseTree = StorehouseInformation.buildTree(object.getString("jsonData"));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //查询分店
                    List<BranchInformation> branchInformationList = new ArrayList<>();
                    try {
                        CommandVo branchVo = InvokerHandler.getInstance().getBranchInformationCommand();
                        String branch = Invoker.getInstance().synchronousExec(branchVo);
                        if (!TextUtils.isEmpty(branch)) {
                            JSONObject object = new JSONObject(branch);
                            if (object.getBoolean("success")) {
                                JSONArray array = object.getJSONArray("data");
                                if (array.length() > 0) {
                                    synchronized (WarehouseKeeper.class) {
                                        branchList.clear();
                                        branchCodeList.clear();
                                        for (int j = 0; j < array.length(); j++) {
                                            BranchInformation information = new BranchInformation(array.getString(j));
                                            branchList.add(information);
                                            branchCodeList.add(information.fId);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, PERIOD);
        }

        private void cancel() {
            this.timer.cancel();
        }
    }
}
