package com.ccservice.b2b2c.atom.pay.gp.certificate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.pay.gp.AirSupper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.ben.Airticketform;
import com.ccservice.b2b2c.util.FileUtils;

/**
 * Gp快钱支付凭证生成
 * @author wzc
 *
 */
public class GpChangePaucertificateCreateMessageListener extends AirSupper implements MessageListener {
    private String orderNoticeResult;

    private long orderid;

    @Override
    public void onMessage(Message message) {
        try {
            orderNoticeResult = ((TextMessage) message).getText();//转换成文本信息
            orderid = Long.parseLong(orderNoticeResult);//转换成long类型的  订单id
            cerCreate(orderid);
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new GpcertificateCreateMessageListener().cerCreate(1739);
    }

    public void cerCreate(long orderid) {
        try {
            Airticketform airform = new Airticketform();
            airform.setId(orderid);
            Orderinfo orderinfo = Server.getInstance().getB2BAirticketService().findB2bOrderinfo(airform);//通过订单id   去库里 拿到 一条订单信息 
            if (orderinfo != null) {
                String gooddesc = orderinfo.getSegmentlist().get(0).getStartairportname() + "到→"
                        + orderinfo.getSegmentlist().get(0).getEndairportname() + "航班号："
                        + orderinfo.getSegmentlist().get(0).getAirname() + "仓位："
                        + orderinfo.getSegmentlist().get(0).getDiscount() + "机票机票款";
                String sql = "SELECT * FROM dbo.GpPayCertificate WITH(NOLOCK) WHERE orderId='" + orderinfo.getTradeno()
                        + "' AND payResult='10'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                PayEntryInfo info = findAgentInfo(orderinfo.getPolicyagentid() + "", 1);
                if (list.size() > 0 && info != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Map map = (Map) list.get(0);
                    String orderId = map.get("orderId").toString();//商家订单号
                    String orderTime = map.get("orderTime").toString();//订单日期
                    String dealTime = map.get("dealTime").toString();//结束日期
                    String payeeAmount = map.get("payeeAmount").toString();//交易金额(元)
                    String dealId = map.get("dealId").toString();//交易号
                    String bankDealId = map.get("bankDealId").toString();//银行订单号
                    String pnr = orderinfo.getPnr();//pnr
                    String compayname = info.getCompayName();//公司全称
                    String TempletPath = PropertyUtil.getValue("TempletPath", "Gpcertification.properties");
                    String FileCreatePath = PropertyUtil.getValue("FileCreatePath", "Gpcertification.properties");
                    String fileContent = FileUtils.readFile(TempletPath, "UTF-8");
                    fileContent = fileContent.replace("[orderId]", orderId);                                                                   //商家订单号
                    fileContent = fileContent.replace("[orderTime]", sdf2.format(sdf.parse(orderTime)));             //交易创建时间        
                    fileContent = fileContent.replace("[dealTime]", sdf2.format(sdf.parse(dealTime)));                  //交易结束时间  
                    fileContent = fileContent.replace("[pnr]", pnr);                                                                               //pnr编号        
                    fileContent = fileContent.replace("[gooddesc]", gooddesc);                                                         //航班信息 
                    																						//------------------------------------------------机票张数
                    fileContent = fileContent.replace("[payeeAmount]",  
                    		formatMoney(Double.parseDouble(payeeAmount) / 100));                                                   //交易金额（元）      
                    																						//-------------------------------------------------账户类型
                    fileContent = fileContent.replace("[dealId]", dealId);                                                                      //交易号         
                    fileContent = fileContent.replace("[bankDealId]", bankDealId);                                                    //银行订单号 
                    																						//-------------------------------------------------交易类型
                    																						//-------------------------------------------------交易状态
                    																						//-------------------------------------------------备注
                    fileContent = fileContent.replace("[compayname]", compayname);                                            //公司全称           
                    String filePath = FileCreatePath + orderinfo.getOrdernumber() + ".html";                                                 
                    FileUtils.writeFileUTF8(fileContent, filePath);                                                                                                         

                    File f = new File(filePath);
                    if (f.exists()) {
                        System.out.println(f.getAbsolutePath().replace("\\", "/"));
                        getAjaxCotnent("file:///" + f.getAbsolutePath().replace("\\", "/"), orderinfo.getOrdernumber()
                                + "");
                    }
                }
                else {
                    WriteLog.write("快钱生成凭证", orderinfo.getOrdernumber() + ":生成凭证数据缺失");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            WriteLog.write("快钱生成凭证", orderid + ":报错:" + e.getMessage());
        }
    }
    public static String getAjaxCotnent(String url, String fileName) throws Exception {
        Runtime rt = Runtime.getRuntime();
        rt.exec("phantomjs.exe d:/phantomjs/codes.js " + url + " " + fileName);//这里我的codes.js是保存在c盘下面的phantomjs目录     
        return url;
    }
}
