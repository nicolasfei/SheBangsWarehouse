package com.shebangs.warehouse.data;

public class ShipmentGoodsStatistics {
    public String supplier;     //供应商
    public int statistic;       //商品数量统计
    public boolean toBeScanned; //获取是否是待扫描，还是已扫描

    public ShipmentGoodsStatistics(String supplier, int statistic, boolean toBeScanned) {
        this.supplier = supplier;
        this.statistic = statistic;
        this.toBeScanned = toBeScanned;
    }

    /**
     * 根据是否代发货来更新数量
     *
     * @param num 数量
     */
    public void deliver(int num) {
        if (toBeScanned) {
            //待扫描则减num
            this.statistic -= num;
        } else {
            //已扫描则加num
            this.statistic += num;
        }
    }
}
