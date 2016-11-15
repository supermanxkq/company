package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import jxl.Cell;
import jxl.Sheet;
import java.util.*;
import jxl.Workbook;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.ccservice.b2b2c.atom.server.Server;

public class RefundExcelDemo {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        String url = "http://121.199.25.199:9026/cn_interface/AliRefundPrice.jsp?payTradeNos=";
        Map<String, List> numMap = new HashMap<String, List>();
        String file = "C:/Users/WH/Desktop/HS车站退票";
        Workbook readwb = Workbook.getWorkbook(new File(file + ".xls"));
        //获取第一张Sheet表   
        Sheet readsheet = readwb.getSheet(0);
        //获取Sheet表中所包含的总列数   
        int rsColumns = readsheet.getColumns();
        //获取Sheet表中所包含的总行数   
        int rsRows = readsheet.getRows();
        //获取指定单元格的对象引用   
        for (int i = 1; i < rsRows; i++) {
            System.out.print(i + "----->");
            for (int j = 0; j < rsColumns; j++) {
                if (j == 0) {
                    Cell cell = readsheet.getCell(j, i);
                    String oid = cell.getContents();
                    System.out.print("订单：" + oid);
                    if (numMap.containsKey(oid)) {
                        System.out.print("--->已处理，跳过");
                        break;
                    }
                    String sql = "select distinct o.C_SUPPLYTRADENO NUM from T_TRAINORDER o with(nolock) "
                            + "join T_UNIONTRADE u with(nolock) on u.C_TRANDNUM = o.C_SUPPLYTRADENO "
                            + "where o.C_QUNARORDERNUMBER = '" + oid + "' and u.C_BUSSTYPE = 2";
                    List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    if (list.size() == 1) {
                        numMap.put(oid, list);
                        Map map = (Map) list.get(0);
                        String num = map.get("NUM").toString();
                        System.out.print("--->" + num);
                        pool.execute(new ReqRefund(i, num, url + num));
                    }
                    System.out.print("");
                }
                else {
                    break;
                }
            }
            System.out.println();
        }
        readwb.close();
        pool.shutdown();
    }
}