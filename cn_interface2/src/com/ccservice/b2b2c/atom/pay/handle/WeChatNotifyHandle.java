package com.ccservice.b2b2c.atom.pay.handle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alipay.util.WeChatPayment;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * 微信支付成功通知
 * @author wzc
 *
 */
public class WeChatNotifyHandle extends NotifyHandleSupport {

    public static void main(String[] args) throws Exception {
        String notify_data = "<xml><appid><![CDATA[wx7851eb51bb80ca5c]]></appid><attach><![CDATA[YDXA201606211531131FgAirnofiryHandle]]></attach><bank_type><![CDATA[ABC_DEBIT]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1365022302]]></mch_id><nonce_str><![CDATA[4bb76f4fb9d046bda33528db014ee0ef]]></nonce_str><openid><![CDATA[oZh8uuDbmFbS3LsDEyF_TQSnGjtw]]></openid><out_trade_no><![CDATA[YDXA2016062115311312016071515190]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[32A6C27DE96D6367F1F7BFE0178643D9]]></sign><time_end><![CDATA[20160715152027]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[NATIVE]]></trade_type><transaction_id><![CDATA[4007102001201607159031634992]]></transaction_id></xml>";
        Document document = DocumentHelper.parseText(notify_data);
        Element root = document.getRootElement();
        String sign = "";
        Map<String, String> params = new HashMap<String, String>();
        for (Iterator it = root.elementIterator(); it.hasNext();) {
            Element element = (Element) it.next();
            if ("sign".equalsIgnoreCase(element.getName())) {
                sign = element.getStringValue();
                continue;
            }
            else {
                params.put(element.getName(), element.getStringValue().trim());
            }
            System.out.println(element.getName() + ":" + element.getStringValue());
        }
        String wesing = WeChatPayment.Sign(params, "e7ccf2e56ff85937c978f4ea846fee56");
        System.out.println(wesing);
        System.out.println(sign);
    }

    /**
     * 获取支付信息
     * @return
     */
    public PayEntryInfo findAgentInfo(int type, String Pid) {
        PayEntryInfo info = null;
        String sql = "SELECT  PId,KeyStr,SellEmail,AgentId,PrivateKey,PublicKey,ISNULL(CompayName,'') CompayName FROM  GPPayInfo  with(nolock) where AccountType="
                + type + " and PID='" + Pid + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() == 1) {
            info = new PayEntryInfo();
            Map map = (Map) list.get(0);
            String pid = map.get("PId") == null ? "" : map.get("PId").toString();
            String KeyStr = map.get("KeyStr") == null ? "" : map.get("KeyStr").toString();
            String AgentId = map.get("AgentId") == null ? "0" : map.get("AgentId").toString();
            String PrivateKey = map.get("PrivateKey") == null ? "" : map.get("PrivateKey").toString();
            String PublicKey = map.get("PublicKey") == null ? "" : map.get("PublicKey").toString();
            info.setSellemail(map.get("SellEmail") == null ? "" : map.get("SellEmail").toString());
            info.setCompayName(map.get("CompayName") == null ? "" : map.get("CompayName").toString());
            info.setPid(pid);
            info.setKey(KeyStr);
            info.setPrivateKey(PrivateKey);
            info.setPublicKey(PublicKey);
            info.setAgentid(Long.parseLong(AgentId));
        }
        return info;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        // 获得POST 过来参数设置到新的params中
        StringBuilder contentBuf = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = br.readLine()) != null) {
                contentBuf.append(line);
            }
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            String returndata = "<xml><return_code><![CDATA[returnflag]]></return_code><return_msg><![CDATA[returnmsg]]></return_msg></xml>";
            Document document = DocumentHelper.parseText(contentBuf.toString());
            Element root = document.getRootElement();
            String sign = "";
            Map<String, String> params = new HashMap<String, String>();
            for (Iterator it = root.elementIterator(); it.hasNext();) {
                Element element = (Element) it.next();
                if ("sign".equalsIgnoreCase(element.getName())) {
                    sign = element.getStringValue();
                    continue;
                }
                else {
                    params.put(element.getName(), element.getStringValue().trim());
                }
                System.out.println(element.getName() + ":" + element.getStringValue());
            }
            WriteLog.write("WeChatTZ_wap", "××微信支付成功通知:支付成功:" + contentBuf.toString());
            String notify_data = contentBuf.toString();//微信返回数据
            response.setContentType("text/plain; charset=utf-8");
            PrintWriter out = response.getWriter();
            if (notify_data.length() > 0 && notify_data.contains("return_code")
                    && notify_data.contains("result_code")) {
                String appid = root.elementTextTrim("appid");
                PayEntryInfo info = findAgentInfo(3, appid);
                String wesign = WeChatPayment.Sign(params, info.getKey());
                String return_code = root.elementTextTrim("return_code");
                String result_code = root.elementTextTrim("result_code");
                if ("SUCCESS".equals(return_code) && "SUCCESS".equalsIgnoreCase(result_code) && sign.equals(wesign)) {
                    String mch_id = root.elementTextTrim("mch_id");
                    String trade_no = root.elementTextTrim("transaction_id");//微信支付订单号
                    String attach = root.elementTextTrim("attach");//商家数据包，原样返回
                    String get_total_fee = root.elementTextTrim("total_fee");// 支付金额
                    WriteLog.write("WeChatTZ_wap", "WeChat交易：" + trade_no + ":handle:" + attach + ";执行交易成功订单处理;"
                            + trade_no + ";" + get_total_fee + ";");
                    super.orderHandle(attach, trade_no, Float.valueOf(get_total_fee), 15, mch_id);
                    returndata = returndata.replaceAll("returnflag", "SUCCESS");
                    returndata = returndata.replaceAll("returnmsg", "ok");
                    out.write(returndata); // 注意一定要返回给微信一个成功的信息(不包含HTML脚本语言)
                    out.flush();
                    out.close();
                }
                else {
                    returndata = returndata.replaceAll("returnflag", "FAIL");
                    returndata = returndata.replaceAll("returnmsg", "fail");
                    out.write(returndata); // 注意一定要返回给微信一个成功的信息(不包含HTML脚本语言)
                    out.flush();
                    out.close();
                }
            }
            else {
                returndata = returndata.replaceAll("returnflag", "FAIL");
                returndata = returndata.replaceAll("returnmsg", "fail");
                out.write(returndata); // 注意一定要返回给微信一个成功的信息(不包含HTML脚本语言)
                out.flush();
                out.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doGet(request, response);
    }

}
