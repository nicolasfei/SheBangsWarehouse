package com.shebangs.warehouse.ui.receipt;

import com.printer.command.EscCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ReceiptVoucher {
    public String code;            //小票编码--最长17个字节
    public String date;            //收货日期
    public String supplier;        //供应商
    public String warehouse;       //库房编号
    public String receiptStaff;    //收货账号
    public List<String> fIDs;      //分店编号list

    public static String remark = "尊敬的供货商，请妥善保管此小票，请认真核对此小票的送货分店\n此小票只作为核查分店是否入库依据，不作为数量金额入账依据";

    public ReceiptVoucher() {
        this.code = "11111111";
        this.date = "2020-03-06";
        this.supplier = "9527";
        this.warehouse = "H库房";
        this.receiptStaff = "某某某";
        this.fIDs = new ArrayList<>();
    }

    public void addFID(String fid) {
        this.fIDs.add(fid);
    }

    public static ReceiptVoucher getTestData() {
        ReceiptVoucher voucher = new ReceiptVoucher();
        voucher.code = "2020 0907-01M11-030609";
        voucher.date = "2020年9月7号";
        voucher.supplier = "9001";
        voucher.warehouse = "M345";
        voucher.receiptStaff = "龚敬";

        for (int i = 0; i < 20; i++) {
            voucher.fIDs.add("MO" + i);
        }

        return voucher;
    }

    /**
     * 票据打印测试页
     *
     * @return
     */
    public Vector<Byte> getPrintBill() {
        EscCommand esc = new EscCommand();
        //初始化打印机
        esc.addInitializePrinter();
        //打印走纸多少个单位
        esc.addPrintAndFeedLines((byte) 3);
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);
        // 设置为倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 打印文字
        esc.addText("收货凭据\n\n");
        //打印并换行
        esc.addPrintAndLineFeed();
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF); // 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);      // 设置打印左对齐

        //打印条码
        // 设置条码可识别字符位置在条码下方
        esc.addText("小票编号:\n");
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
        // 设置条码高度为60点
        esc.addSetBarcodeHeight((byte) 60);
        // 设置条码宽窄比为2
        esc.addSetBarcodeWidth((byte) 2);
        // 打印Code128码
        esc.addCODE93(code.length() > 17 ? code.substring(0, 17) : code);
        esc.addPrintAndLineFeed();

        esc.addText("\n");      //换行
        //打印日期，供货商，库房，收货人
        esc.addText("收货日期:" + date + "\n");
        esc.addText("供应商编号:" + supplier + "\n");
        esc.addText("库房编号:" + warehouse + "\n");
        esc.addText("收货账号:" + receiptStaff + "\n");
        esc.addPrintAndLineFeed();

        //打印收到货的分店列表
        int i = 1;
        for (; i <= fIDs.size(); i++) {
//            String value = (i < 10 ? ("0" + i) : i) + " " + fIDs.get(i - 1) + "\t";
            String value = fIDs.get(i - 1) + "\t";
            esc.addText(value);
            if (i % 4 == 0) {
                esc.addText("\n\n");
            }
        }
        if ((i-1) % 4 != 0) {
            esc.addText("\n");
        }
        esc.addPrintAndLineFeed();

        //打印收货合计
        esc.addSelectJustification(EscCommand.JUSTIFICATION.RIGHT);
        esc.addText("此单合计分店个数:" + fIDs.size() + "个" + "\n");
        esc.addPrintAndLineFeed();

        //打印备注
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);
        esc.addText(remark);
        esc.addPrintAndLineFeed();

        //打印走纸n个单位
        esc.addPrintAndFeedLines((byte) 4);
        // 开钱箱
        //esc.addGeneratePlus(LabelCommand.FOOT.F2, (byte) 255, (byte) 255);
        //开启切刀
        esc.addCutPaper();
        //添加缓冲区打印完成查询
        byte[] bytes = {0x1D, 0x72, 0x01};
        //添加用户指令
        esc.addUserCommand(bytes);
        Vector<Byte> datas = esc.getCommand();
        return datas;
    }

}
