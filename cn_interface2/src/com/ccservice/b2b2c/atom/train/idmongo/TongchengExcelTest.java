package com.ccservice.b2b2c.atom.train.idmongo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.mongodb.DBObject;

public class TongchengExcelTest {
    public static void main(String[] args) {
        int sum = 1;
        int id = 1;
        while (true) {
            List alllist = new ArrayList();
            List list = getCustomerusers(id);
            if (list.size() == 0) {
                System.out.println("over!!!!!!!!!!!!!!!");
                try {
                    Thread.sleep(100000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (list.size() > 0) {
                WriteLog.write("tongchengtongji_ID", "ID:" + ((Map) list.get(list.size() - 1)).get("ID").toString());
                id = Integer.valueOf(((Map) list.get(list.size() - 1)).get("ID").toString());
            }
            for (int i = 0; i < list.size(); i++) {
                if (i % 1000 == 0) {
                    System.out.println("当前次数:" + sum + "--->循环个数:" + i);
                }
                Map map = (Map) list.get(i);
                List<DBObject> personlist = getPersons(map.get("C_LOGINNAME").toString());
                alllist.add(personlist);
            }
            if (alllist.size() > 0) {
                createXLS(alllist, sum);
                sum = sum + 1;
                WriteLog.write("tongchengtongji_sum", "sum" + sum);
            }
        }
    }

    public static List getCustomerusers(int id) {
        String sql = "sp_CustomerUser_SelectAllCanused @ID=" + id;
        List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
        if (list == null) {
            return new ArrayList();
        }
        return list;
    }

    public static List<DBObject> getPersons(String loginname) {
        try {
            return new MongoLogic().FindMongoByCustomerUser(loginname);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ArrayList<DBObject>();
    }

    public static void createXLS(List<List<Object>> list, int xlssum) {
        int excelchangesum = 25000;
        int excelsum = 1;
        int sum = 1;
        //打开文件
        WritableWorkbook book = null;
        //生成名为"第一页"的工作表，参数0表示这是第一页
        WritableSheet sheet = null;
        boolean close = false;
        for (int i = 0; i < list.size(); i++) {
            List<Object> cuslist = list.get(i);
            for (int j = 0; j < cuslist.size(); j++) {
                JSONObject jsonObject = JSONObject.parseObject(cuslist.get(j).toString());
                String loginname = jsonObject.getString("SupplyAccount");
                String RealName = jsonObject.getString("RealName");
                String IDNumber = jsonObject.getString("IDNumber");
                if (IDNumber.length() == 19) {
                    IDNumber = IDNumber.substring(0, 17) + "X";
                }
                try {
                    if (i == 0 && j == 0 || close) {
                        book = Workbook.createWorkbook(new File("D:/同城数据1/同城数据正/JAVA生成EXCEL测试(" + xlssum + "_"
                                + excelsum + ").xls"));
                        sheet = book.createSheet("第1页", 0);
                        //设置字体为宋体,16号字,加粗,颜色为红色
                        WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"), 16, WritableFont.BOLD);
                        font1.setColour(Colour.RED);
                        WritableCellFormat format1 = new WritableCellFormat(font1);
                        format1.setAlignment(jxl.format.Alignment.CENTRE);
                        format1.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
                        Label labelA = new Label(0, 0, "账号", format1);
                        Label labelB = new Label(1, 0, "姓名", format1);
                        Label labelC = new Label(2, 0, "证件号", format1);
                        Label labelD = new Label(3, 0, "证件类型", format1);
                        //将定义好的单元格添加到工作表中
                        sheet.addCell(labelA);
                        sheet.addCell(labelB);
                        sheet.addCell(labelC);
                        sheet.addCell(labelD);
                        excelsum = excelsum + 1;
                        sum = 1;
                        close = false;
                    }
                    /*生成一个保存数字的单元格
                                                                        必须使用Number的完整包路径，否则有语法歧义*/
                    Label labelA1 = new Label(0, sum, "正_" + xlssum + "_" + i);
                    Label labelB1 = new Label(1, sum, RealName);
                    Label labelC1 = new Label(2, sum, IDNumber);
                    Label labelD1 = new Label(3, sum, "身份证");
                    sheet.addCell(labelA1);
                    sheet.addCell(labelB1);
                    sheet.addCell(labelC1);
                    sheet.addCell(labelD1);
                    sum = sum + 1;
                    if ((sum >= excelchangesum || i == list.size() - 1) && j == cuslist.size() - 1) {
                        //写入数据并关闭文件
                        book.write();
                        book.close();
                        close = true;
                        System.out.println("创建文件成功!");
                    }
                }
                catch (Exception e) {
                    System.out.println(e);
                    return;
                }
            }
        }
    }
}
