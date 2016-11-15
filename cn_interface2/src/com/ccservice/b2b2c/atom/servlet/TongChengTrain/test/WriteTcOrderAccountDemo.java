package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.io.*;
import java.util.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.ccservice.b2b2c.atom.train.idmongo.MongoHelper;
import com.mongodb.DBObject;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class WriteTcOrderAccountDemo {

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        String file = "C:/Users/WH/Desktop/航天14号";
        Workbook readwb = Workbook.getWorkbook(new File(file + ".xls"));
        WritableWorkbook workbook = Workbook.createWorkbook(new File(file + "过完.xls"), readwb);
        WritableSheet sheet = workbook.createSheet("航天", 0);
        MongoLogic mongoLogic = new MongoLogic();
        Map<String, Map<String, String>> accountMap = new HashMap<String, Map<String, String>>();
        //获取第一张Sheet表   
        Sheet readsheet = readwb.getSheet(0);
        //获取Sheet表中所包含的总列数   
        int rsColumns = readsheet.getColumns();
        //获取Sheet表中所包含的总行数   
        int rsRows = readsheet.getRows();
        //获取指定单元格的对象引用   
        for (int i = 0; i < rsRows; i++) {
            String orderNum = "";
            for (int j = 0; j < rsColumns; j++) {
                Cell cell = readsheet.getCell(j, i);
                String content = cell.getContents();
                sheet.addCell(new Label(j, i, content));
                //订单号
                if (j == 0) {
                    orderNum = content;
                }
            }
            if (i == 0) {
                sheet.addCell(new Label(rsColumns, i, "账号及对应状态"));
            }
            else {
                JSONArray accounts = new JSONArray();
                //查询订单
                if (!ElongHotelInterfaceUtil.StringIsNull(orderNum)) {
                    String sql = "select ID from T_TRAINORDER with(nolock) where C_QUNARORDERNUMBER = '" + orderNum
                            + "'";
                    List orders = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    //取第一个
                    if (orders != null && orders.size() > 0) {
                        Map map = (Map) orders.get(0);
                        String orderId = map.get("ID").toString();
                        System.out.print(orderNum + "-->" + orderId);
                        sql = "select C_IDNUMBER from T_TRAINPASSENGER with(nolock) where C_ORDERID = " + orderId;
                        List passengers = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                        Map<String, Integer> accountCountMap = new HashMap<String, Integer>();
                        Map<String, String> accountStatusMap = new HashMap<String, String>();
                        //乘客
                        for (int m = 0; m < passengers.size(); m++) {
                            Map p = (Map) passengers.get(m);
                            String C_IDNUMBER = p.get("C_IDNUMBER").toString();
                            //当前乘客有的账号
                            Map<String, String> account = accountMap.get(C_IDNUMBER);
                            if (account == null) {
                                account = new HashMap<String, String>();
                                Map<String, Object> query = new HashMap<String, Object>();
                                query.put("IDNumber", mongoLogic.GetLongFromString(C_IDNUMBER));
                                List<DBObject> users = MongoHelper.getInstance().find("CustomerUser", query);
                                if (users != null && users.size() > 0) {
                                    for (int j = 0; j < users.size(); j++) {
                                        String loginName = String.valueOf(users.get(j).get("SupplyAccount"));
                                        System.out.print("-->" + loginName);
                                        String userSql = "select ID, ISNULL(C_ISENABLE, 0) C_ISENABLE "
                                                + "from T_CUSTOMERUSER with(nolock) where C_LOGINNAME = '" + loginName
                                                + "'";
                                        List userList = Server.getInstance().getSystemService()
                                                .findMapResultBySql(userSql, null);
                                        for (int n = 0; n < userList.size(); n++) {
                                            Map userMap = (Map) userList.get(n);
                                            String ID = userMap.get("ID").toString();
                                            String C_ISENABLE = userMap.get("C_ISENABLE").toString();
                                            int isenable = Integer.parseInt(C_ISENABLE);
                                            if (isenable == 1) {
                                                C_ISENABLE = "可用";
                                            }
                                            else if (isenable == 20 || isenable == 23 || isenable == 33
                                                    || isenable == 36 || isenable == 51) {
                                                C_ISENABLE = "未绑定手机号";
                                            }
                                            //账号被封
                                            else if (isenable == -1 || isenable == 0 || isenable == 2 || isenable == 16
                                                    || isenable == 17 || isenable == 22 || isenable == 25
                                                    || isenable == 31 || isenable == 32 || isenable == 34
                                                    || isenable == 35) {
                                                C_ISENABLE = "账号被封";
                                            }
                                            else {
                                                C_ISENABLE = "其他";
                                            }
                                            account.put(ID, C_ISENABLE);
                                        }
                                    }
                                }
                                accountMap.put(C_IDNUMBER, account);
                            }
                            for (String id : account.keySet()) {
                                int count = accountCountMap.containsKey(id) ? accountCountMap.get(id) : 0;
                                accountCountMap.put(id, count + 1);
                                accountStatusMap.put(id, account.get(id));
                            }
                        }
                        for (String id : accountCountMap.keySet()) {
                            //全有
                            if (accountCountMap.get(id) == passengers.size()) {
                                JSONObject obj = new JSONObject();
                                obj.put("账号", id);
                                obj.put("状态", accountStatusMap.get(id));
                                accounts.add(obj);
                            }
                        }
                    }
                }
                //添加表格
                sheet.addCell(new Label(rsColumns, i, accounts.toString()));
            }
            System.out.println();
        }
        workbook.write();
        workbook.close();
        readwb.close();
    }
}