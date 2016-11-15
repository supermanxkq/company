package com.ccservice.b2b2c.atom.pay;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2g.bean.GameProduct;
import com.ccservice.b2b2g.bean.PhoneInfo;
import com.ccservice.b2b2g.bean.PhoneProductInfo;

/**
 * 19e话费、Q币充值接口
 * 
 * @author 贾建磊
 *
 */
public class OneNineEPayRecharge {

    private String phoneAgentid;//代理商id  (话费充值)

    private String phoneMerchantKey;//密钥  (话费充值)

    private String phoneRechargeAddress;//接口地址 (话费充值)

    private String gameMerchantid;//账号(游戏充值)

    private String gameKey;//密钥(游戏充值)

    private String gameRechargeAddress;//接口地址(游戏充值)

    private String phonenotifyurl;//充值成功通知地址

    /**
     * 手机号码归属地查询
     * 
     * @param mobilenum 手机号码
     * @return
     */
    public PhoneInfo accsegment(String mobilenum) {
        PhoneInfo phoneInfo = new PhoneInfo();
        try {
            String url = this.phoneRechargeAddress + "accegment/accsegment.do";

            String verifystring = "agentid=" + phoneAgentid + "&source=esales&mobilenum=" + mobilenum + "&merchantKey="
                    + phoneMerchantKey;
            String paramContent = "agentid=" + phoneAgentid;
            paramContent += "&source=esales";
            paramContent += "&mobilenum=" + mobilenum;
            paramContent += "&verifystring=" + OneNineEPayRecharge.getKeyedDigest(verifystring, "");
            String result = SendPostandGet.submitGet(url + "?" + paramContent);
            result = URLDecoder.decode(result, "utf-8");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    Element accEle = root.element("acc");

                    List mobileList = accEle.elements("mobile");
                    if (mobileList != null && mobileList.size() > 0) {
                        phoneInfo = new PhoneInfo();
                        Iterator it = mobileList.iterator();
                        int i = 1;
                        while (it.hasNext()) {
                            Element mobileEle = (Element) it.next();
                            String mobileValue = mobileEle.attributeValue("value").trim();
                            if (i == 1) {
                                phoneInfo.setIsptype(mobileValue);
                            }
                            else if (i == 2) {
                                phoneInfo.setProvincename(mobileValue);
                            }
                            else if (i == 3) {
                                phoneInfo.setCitycode(mobileValue);
                            }
                            else if (i == 4) {
                                phoneInfo.setDetail(mobileValue);
                            }
                            i++;
                        }
                        return phoneInfo;
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return phoneInfo;
    }

    /**
     * 话费充值产品查询
     * 
     * @return
     */
    public List<PhoneProductInfo> productQuery() {
        List<PhoneProductInfo> ppiList = new ArrayList<PhoneProductInfo>();
        try {
            String url = this.phoneRechargeAddress + "prodquery/directProduct.do";

            String verifystring = "agentid=" + phoneAgentid + "&source=esales&merchantKey=" + phoneMerchantKey;
            String paramContent = "agentid=" + phoneAgentid;
            paramContent += "&source=esales";
            paramContent += "&verifystring=" + OneNineEPayRecharge.getKeyedDigest(verifystring, "");

            String result = SendPostandGet.submitGet(url + "?" + paramContent);
            result = URLDecoder.decode(result, "utf-8");
            //            WriteLog.write("19PAY_产品", result);
            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    List productsList = root.elements("products");
                    if (productsList != null && productsList.size() > 0) {
                        Iterator it = productsList.iterator();
                        while (it.hasNext()) {
                            Element products = (Element) it.next();
                            List productList = products.elements("product");
                            if (productList != null && productList.size() > 0) {
                                Iterator proIt = productList.iterator();
                                PhoneProductInfo ppi = new PhoneProductInfo();
                                int i = 1;
                                while (proIt.hasNext()) {
                                    Element product = (Element) proIt.next();
                                    String productValue = product.attributeValue("value").trim();

                                    if (i == 1) {
                                        ppi.setProdId(productValue);
                                    }
                                    else if (i == 2) {
                                        ppi.setProdContent(productValue);
                                    }
                                    else if (i == 3) {
                                        ppi.setProdPrice(productValue);
                                    }
                                    else if (i == 4) {
                                        ppi.setProdIsptype(productValue);
                                    }
                                    else if (i == 5) {
                                        ppi.setProdDelaytimes(productValue);
                                    }
                                    else if (i == 6) {
                                        ppi.setProdProvinceid(productValue);
                                    }
                                    else if (i == 7) {
                                        ppi.setProdType(productValue);
                                    }

                                    if (ppi.getProdType() != null && ppi.getProdType().equals("移动电话")) {
                                        ppiList.add(ppi);
                                    }
                                    i++;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return ppiList;
    }

    /**
     * 话费直冲
     * 
     * @param prodid  产品id
     * @param orderid   我们系统生成的订单号(注：该订单号由代理商商城系统生成。orderid唯一确定一条订单。)
     * @param mobilenum  充值号码
     * http://www.yeebooking.com/cn_interface/BillOrderState
     * @return  json格式:{"prodidValue":"","orderidValue":"","tranidValue":"","resultnoValue":"","markValue":"","verifystringValue":""}
     */
    public String directFill(String prodid, String orderid, String mobilenum) {
        try {
            String url = this.phoneRechargeAddress + "directfill/directFill.do";
            String strbackurl = phonenotifyurl;
            String verifystring = "prodid=" + prodid + "&agentid=" + phoneAgentid + "&backurl=" + strbackurl
                    + "&returntype=2&orderid=" + orderid + "&mobilenum=" + mobilenum
                    + "&source=esales&mark=&merchantKey=" + phoneMerchantKey;
            String paramContent = "prodid=" + prodid;
            paramContent += "&agentid=" + phoneAgentid;
            paramContent += "&backurl=" + strbackurl;
            paramContent += "&returntype=2";
            paramContent += "&orderid=" + orderid;
            paramContent += "&mobilenum=" + mobilenum;
            paramContent += "&source=esales";
            paramContent += "&mark=";
            paramContent += "&verifystring=" + OneNineEPayRecharge.getKeyedDigest(verifystring, "");

            WriteLog.write("19PAY支付充值接口日志", "（话费直冲接口）传入接口的数据[" + url + "?" + paramContent + "]");
            WriteLog.write("19PAY支付充值接口日志", "数据记录[产品id：" + prodid + "---本地系统订单号：" + orderid + "---充值号码：" + mobilenum
                    + "]");
            String result = SendPostandGet.submitGet(url + "?" + paramContent);
            result = URLDecoder.decode(result, "utf-8");
            WriteLog.write("19PAY支付充值接口日志", "（话费直冲接口）从接口传出的数据[" + result + "]");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    Element itemsEle = root.element("items");

                    List itemList = itemsEle.elements("item");
                    if (itemList != null && itemList.size() > 0) {
                        String resultJson = "{";

                        String prodidValue = "";//话费充值产品编号
                        String orderidValue = "";//代理商商城订单
                        String tranidValue = "";//直冲接口平台订单号
                        String resultnoValue = "";//直冲结果编码
                        String markValue = "";//预留字段
                        String verifystringValue = "";//验证摘要串

                        Iterator it = itemList.iterator();
                        int i = 1;
                        while (it.hasNext()) {
                            Element itemEle = (Element) it.next();
                            String itemValue = itemEle.attributeValue("value").trim();
                            if (i == 1) {
                                prodidValue = itemValue;
                            }
                            else if (i == 2) {
                                orderidValue = itemValue;
                            }
                            else if (i == 3) {
                                tranidValue = itemValue;
                            }
                            else if (i == 4) {
                                resultnoValue = itemValue;
                            }
                            else if (i == 5) {
                                markValue = itemValue;
                            }
                            else if (i == 6) {
                                verifystringValue = itemValue;
                            }
                            i++;
                        }

                        resultJson += "\"prodidValue\":\"" + prodidValue + "\",";
                        resultJson += "\"orderidValue\":\"" + orderidValue + "\",";
                        resultJson += "\"tranidValue\":\"" + tranidValue + "\",";
                        resultJson += "\"resultnoValue\":\"" + resultnoValue + "\",";
                        resultJson += "\"markValue\":\"" + markValue + "\",";
                        resultJson += "\"verifystringValue\":\"" + verifystringValue + "\"";

                        resultJson += "}";
                        return resultJson;
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    /**
     * 话费订单查询
     * 
     * @param orderid  我们系统生成的订单号(注：该订单号由代理商商城系统生成。orderid唯一确定一条订单。)
     * 
     * @return  json格式:{"orderidValue":"","resultnoValue":"","finishmoneyValue":"","verifystringValue":""}
     */
    public String orderQuery(String orderid) {
        try {
            String url = this.phoneRechargeAddress + "orderquery/directSearch.do";

            String verifystring = "agentid=" + phoneAgentid + "&backurl=&returntype=2&orderid=" + orderid
                    + "&source=esales&merchantKey=" + phoneMerchantKey;
            String paramContent = "agentid=" + phoneAgentid;
            paramContent += "&backurl=";
            paramContent += "&returntype=2";
            paramContent += "&orderid=" + orderid;
            paramContent += "&source=esales";
            paramContent += "&verifystring=" + OneNineEPayRecharge.getKeyedDigest(verifystring, "");

            String result = SendPostandGet.submitGet(url + "?" + paramContent);
            result = URLDecoder.decode(result, "utf-8");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    Element itemsEle = root.element("items");

                    List itemList = itemsEle.elements("item");
                    if (itemList != null && itemList.size() > 0) {
                        String resultJson = "{";

                        String orderidValue = "";//代理商商城订单
                        String resultnoValue = "";//充值结果
                        String finishmoneyValue = "";//充值成功金额
                        String verifystringValue = "";//验证摘要串

                        Iterator it = itemList.iterator();
                        int i = 1;
                        while (it.hasNext()) {
                            Element itemEle = (Element) it.next();
                            String itemValue = itemEle.attributeValue("value").trim();
                            if (i == 1) {
                                orderidValue = itemValue;
                            }
                            else if (i == 2) {
                                resultnoValue = itemValue;
                            }
                            else if (i == 3) {
                                finishmoneyValue = itemValue;
                            }
                            else if (i == 4) {
                                verifystringValue = itemValue;
                            }
                            i++;
                        }

                        resultJson += "\"orderidValue\":\"" + orderidValue + "\",";
                        resultJson += "\"resultnoValue\":\"" + resultnoValue + "\",";
                        resultJson += "\"finishmoneyValue\":\"" + finishmoneyValue + "\",";
                        resultJson += "\"verifystringValue\":\"" + verifystringValue + "\"";

                        resultJson += "}";
                        return resultJson;
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    /**
     * 可充值游戏查询接口（注：只获取Q币相关数据）
     * 
     * @return
     */
    public String canRechargeGameQuery() {
        try {
            //sign=MD5（merchantKey +MD5（A=1&B=2&C=&key=merchantKey）+merchantKey） 参数都按照名称字符排序
            String sign = OneNineEPayRecharge
                    .getKeyedDigest(
                            gameKey
                                    + OneNineEPayRecharge.getKeyedDigest("commandid=gamequery&mark=&merchantid="
                                            + gameMerchantid + "&protocolid=normal&version=1&key=" + gameKey, "")
                                    + gameKey, "");
            String paramContent = "commandid=gamequery";
            paramContent += "&protocolid=normal";
            paramContent += "&merchantid=" + gameMerchantid;
            paramContent += "&version=1";
            paramContent += "&sign=" + sign;
            paramContent += "&mark=";

            String result = SendPostandGet.submitGet(gameRechargeAddress + "?" + paramContent);
            result = URLDecoder.decode(result, "GBK");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    result = result.substring(0, result.lastIndexOf(">") + 1);

                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    if (root.elementText("code") != null && root.elementText("code").trim().equals("0")) {
                        Element gameinfolistEle = root.element("gameinfolist");

                        List gameinfoList = gameinfolistEle.elements("gameinfo");
                        if (gameinfoList != null && gameinfoList.size() > 0) {
                            Iterator it = gameinfoList.iterator();
                            while (it.hasNext()) {
                                Element gameinfoEle = (Element) it.next();
                                String gamename = gameinfoEle.elementText("gamename");//游戏名称
                                if (gamename != null && gamename.equals("Q币")) {//只需获取Q币的，其他的游戏不需要获取
                                    String gameid = gameinfoEle.elementText("gameid");//游戏编号
                                    if (gameid != null && !gameid.equals("")) {
                                        return gameid;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    /**
     * 游戏产品查询接口（注：只获取Q币相关数据）
     * 
     * @param gameid  游戏编号
     * @return
     */
    public List<GameProduct> gameProductQuery(String gameid) {
        List<GameProduct> gameProductList = new ArrayList<GameProduct>();
        try {
            //sign=MD5（merchantKey +MD5（A=1&B=2&C=&key=merchantKey）+merchantKey） 参数都按照名称字符排序
            String sign = OneNineEPayRecharge.getKeyedDigest(
                    gameKey
                            + OneNineEPayRecharge.getKeyedDigest("commandid=prodquery&gameid=" + gameid
                                    + "&mark=&merchantid=" + gameMerchantid + "&protocolid=normal&version=1&key="
                                    + gameKey, "") + gameKey, "");
            String paramContent = "commandid=prodquery";
            paramContent += "&protocolid=normal";
            paramContent += "&merchantid=" + gameMerchantid;
            paramContent += "&version=1";
            paramContent += "&sign=" + sign;
            paramContent += "&gameid=" + gameid;
            paramContent += "&mark=";

            String result = SendPostandGet.submitGet(gameRechargeAddress + "?" + paramContent);
            result = URLDecoder.decode(result, "GBK");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    result = result.substring(0, result.lastIndexOf(">") + 1);

                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    if (root.elementText("code") != null && root.elementText("code").trim().equals("0")) {
                        Element gameproductlistEle = root.element("gameproductlist");

                        List gameproductList = gameproductlistEle.elements("gameproduct");
                        if (gameproductList != null && gameproductList.size() > 0) {
                            Iterator it = gameproductList.iterator();
                            while (it.hasNext()) {
                                GameProduct gameproduct = new GameProduct();
                                Element gameproductEle = (Element) it.next();
                                gameproduct.setOnlineid(gameproductEle.elementText("onlineid"));
                                gameproduct.setOnlinename(gameproductEle.elementText("onlinename"));
                                gameproduct.setGameid(gameproductEle.elementText("gameid"));
                                gameproduct.setGamename(gameproductEle.elementText("gamename"));
                                gameproduct.setParvalue(gameproductEle.elementText("parvalue"));
                                gameproduct.setSaleprice(gameproductEle.elementText("saleprice"));
                                gameProductList.add(gameproduct);
                            }
                        }
                    }
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return gameProductList;
    }

    /**
     * 直接充值接口（注：只对Q币进行充值）
     * 
     * @param gameid 游戏编号
     * @param parvalue 游戏面值 格式：10.0
     * @param orderid  本地系统订单号
     * @param chargeaccount  用户账号
     * @param fillnum  购买数量，正整数
     * @param clientip 用户ip
     * @return
     */
    public String directRecharge(String gameid, String parvalue, String orderid, String chargeaccount, int fillnum,
            String clientip) {
        try {
            //sign=MD5（merchantKey +MD5（A=1&B=2&C=&key=merchantKey）+merchantKey） 参数都按照名称字符排序
            String sign = OneNineEPayRecharge.getKeyedDigest(
                    gameKey
                            + OneNineEPayRecharge.getKeyedDigest("accounttype=0&areaid=&chargeaccount=" + chargeaccount
                                    + "&chargetype=-1&commandid=directfill&fillnum=" + fillnum + "&gameid=" + gameid
                                    + "&mark=&merchantid=" + gameMerchantid + "&orderid=" + orderid + "&parvalue="
                                    + parvalue + "&paymethod=0&protocolid=normal&roleid=&rolename=&serverid=&userip="
                                    + clientip + "&version=1&key=" + gameKey, "") + gameKey, "");
            String paramContent = "commandid=directfill";
            paramContent += "&protocolid=normal";
            paramContent += "&merchantid=" + gameMerchantid;
            paramContent += "&version=1";
            paramContent += "&sign=" + sign;
            paramContent += "&gameid=" + gameid;
            paramContent += "&parvalue=" + parvalue;
            paramContent += "&orderid=" + orderid;
            paramContent += "&chargeaccount=" + chargeaccount;
            paramContent += "&accounttype=0";
            paramContent += "&roleid=";
            paramContent += "&rolename=";
            paramContent += "&fillnum=" + fillnum;
            paramContent += "&paymethod=0";
            paramContent += "&userip=" + clientip;
            paramContent += "&areaid=";
            paramContent += "&serverid=";
            paramContent += "&chargetype=-1";
            paramContent += "&mark=";

            WriteLog.write("19PAY支付充值接口日志", "（Q币充值）传入接口的数据[" + gameRechargeAddress + "?" + paramContent + "]");
            WriteLog.write("19PAY支付充值接口日志", "数据记录[游戏编号:" + gameid + "---充值面值：" + parvalue + "---本地订单号:" + orderid
                    + "---QQ账号:" + chargeaccount + "---购买数量:" + fillnum + "]");
            String result = SendPostandGet.submitGet(gameRechargeAddress + "?" + paramContent);
            result = URLDecoder.decode(result, "GBK");
            WriteLog.write("19PAY支付充值接口日志", "（Q币充值）从接口传出的数据[" + result + "]");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    result = result.substring(0, result.lastIndexOf(">") + 1);

                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    if (root.elementText("code") != null && root.elementText("code").trim().equals("0")) {
                        String jxorderid = root.elementText("jxorderid");//高阳订单号
                        if (jxorderid != null && !jxorderid.trim().equals("")) {
                            return jxorderid;
                        }
                    }
                }
            }

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    /**
     * Q币订单结果查询
     * 
     * @param orderid 本地系统订单号
     * 
     * @return  json格式：{"orderstatus":"","fillmoney":"","finishmoney":""}
     */
    public String orderResultQuery(String orderid) {
        try {
            //sign=MD5（merchantKey +MD5（A=1&B=2&C=&key=merchantKey）+merchantKey） 参数都按照名称字符排序
            String sign = OneNineEPayRecharge.getKeyedDigest(
                    gameKey
                            + OneNineEPayRecharge.getKeyedDigest("commandid=orderquery&mark=&merchantid="
                                    + gameMerchantid + "&orderid=" + orderid + "&protocolid=normal&version=1&key="
                                    + gameKey, "") + gameKey, "");
            String paramContent = "commandid=orderquery";
            paramContent += "&protocolid=normal";
            paramContent += "&merchantid=" + gameMerchantid;
            paramContent += "&version=1";
            paramContent += "&sign=" + sign;
            paramContent += "&orderid=" + orderid;
            paramContent += "&mark=";

            String result = SendPostandGet.submitGet(gameRechargeAddress + "?" + paramContent);
            result = URLDecoder.decode(result, "GBK");

            if (result != null && !result.trim().equals("")) {
                if (result.contains("xml")) {
                    result = result.substring(0, result.lastIndexOf(">") + 1);

                    Document document = DocumentHelper.parseText(result.trim());

                    Element root = document.getRootElement();

                    String resultJson = "{";

                    String orderstatus = "";
                    String fillmoney = "";
                    String finishmoney = "";

                    if (root.elementText("code") != null && root.elementText("code").trim().equals("0")) {
                        orderstatus = root.elementText("orderstatus");//发货结果(0充值成功 1充值失败 2充值部分成功 3充值处理中)
                        fillmoney = root.elementText("fillmoney");//充值请求总面值
                        finishmoney = root.elementText("finishmoney");//充值完成总面值
                    }

                    resultJson += "\"orderstatus\":\"" + orderstatus + "\",";
                    resultJson += "\"fillmoney\":\"" + fillmoney + "\",";
                    resultJson += "\"finishmoney\":\"" + finishmoney + "\"";

                    resultJson += "}";
                    return resultJson;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return "FAIL";
    }

    /**
     * 生成验证摘要串
     * 
     * @param strSrc  需要加密的字符串
     * @param key  key请填空字符串
     * @return
     */
    public static String getKeyedDigest(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));

            String result = "";
            byte[] temp;
            temp = md5.digest(key.getBytes("UTF8"));
            for (int i = 0; i < temp.length; i++) {
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }

            return result;

        }
        catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPhoneAgentid() {
        return phoneAgentid;
    }

    public void setPhoneAgentid(String phoneAgentid) {
        this.phoneAgentid = phoneAgentid;
    }

    public String getPhoneMerchantKey() {
        return phoneMerchantKey;
    }

    public void setPhoneMerchantKey(String phoneMerchantKey) {
        this.phoneMerchantKey = phoneMerchantKey;
    }

    public String getPhoneRechargeAddress() {
        return phoneRechargeAddress;
    }

    public void setPhoneRechargeAddress(String phoneRechargeAddress) {
        this.phoneRechargeAddress = phoneRechargeAddress;
    }

    public String getGameMerchantid() {
        return gameMerchantid;
    }

    public void setGameMerchantid(String gameMerchantid) {
        this.gameMerchantid = gameMerchantid;
    }

    public String getGameKey() {
        return gameKey;
    }

    public void setGameKey(String gameKey) {
        this.gameKey = gameKey;
    }

    public String getGameRechargeAddress() {
        return gameRechargeAddress;
    }

    public void setGameRechargeAddress(String gameRechargeAddress) {
        this.gameRechargeAddress = gameRechargeAddress;
    }

    public String getPhonenotifyurl() {
        return phonenotifyurl;
    }

    public void setPhonenotifyurl(String phonenotifyurl) {
        this.phonenotifyurl = phonenotifyurl;
    }

}