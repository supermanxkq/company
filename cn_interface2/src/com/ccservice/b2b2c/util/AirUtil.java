package com.ccservice.b2b2c.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;

/**
 * 此类为机票业务公共类
 * @author dd
 *
 */
public class AirUtil {
    public static String getidtype(int i) {
        String[] idtypes = new String[] { "其他", "身份证", "其他", "护照", "港澳通行证", "台湾通行证", "台胞证", "回乡证", "军官证", "其他" };
        if (idtypes.length - 1 < i) {
            return "其他";
        }
        else {
            return idtypes[i];
        }
    }

    public static String getBrowserIp(HttpServletRequest request) {
        String ipString = "";
        if (request.getHeader("X-real-ip") == null) {
            ipString = request.getRemoteAddr();
        }
        else {
            ipString = request.getHeader("X-real-ip");
        }
        return ipString;
    }

    /**
     * 根据rt信息获取航司大编
     * 
     * @param rt
     * @return
     */
    public static String getBigPnrbyRT(String rt) {
        String result = "";
        try {
            int num = rt.indexOf("CA/");
            result = rt.substring(num + 3, num + 9);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据pat信息获取格式化后的价格
     * 0000,-1Y,1010.00,50.00,110.00,1170.00|YOW,900.00,50.00,110.00,1060.00|Y,1010.00,50.00,110.00,1170.00
     * @param patinfo
     * @return
     */
    public static String getFormatpat(String patinfo) {
        String result = "";
        try {
            Pattern patitem = Pattern.compile("\\s{1,}");
            String[] pats = patinfo.split("SFC:");
            int flag = 0;
            for (int j = 0; j < pats.length; j++) {
                if (pats[j].indexOf("FARE:") >= 0) {
                    String[] strpatItem = patitem.split(pats[j]);
                    for (int i = 0; i < strpatItem.length; i++) {
                        if (strpatItem[i].trim().indexOf("FARE:") >= 0) {
                            if (flag > 0) {
                                result += "|";
                            }
                            result += strpatItem[i - 1].trim() + ",";
                            result += strpatItem[i].trim().replace("FARE:CNY", "") + ",";
                            result += strpatItem[i + 1].trim().replace("TAX:CNY", "") + ",";
                            result += strpatItem[i + 2].trim().replace("YQ:CNY", "") + ",";
                            result += strpatItem[i + 3].trim().replace("TOTAL:", "");
                            flag++;
                        }
                        else {
                            continue;
                        }
                    }
                }
                else {
                    continue;
                }
            }
            if (flag > 0) {
                result = "0000," + result;
            }
            result = result.replaceAll("TAX:TEXEMPTCN", "0");
        }
        catch (Exception e) {
        }
        return result;
    }

    /**
     * 检测是否是英文名字
     * @param name
     * @return
     */
    public static boolean cheakNameisAllEN(String name) {
        Pattern pattern = Pattern.compile("[0-9].([a-z]{1,100})/([a-z]{1,100})", Pattern.CASE_INSENSITIVE);//不识别大小写
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    /**
     * @author 陈栋 2012-7-4 17:57:36 检查PNR的RT信息中的姓名是否正确
     * @param pnr
     * @param pnrRT
     * @return 错误的名字(前面是字母后面是汉字的名字)
     */
    public static String checkPNR(String pnr, String pnrRT) {
        String nameString = "";
        // 根据PNR截取pnr的RT
        String[] patas = pnrRT.split(pnr);
        // 名字信息
        String[] names = patas[0].split("\\s");
        boolean flag = true;
        for (int i = 0; i < names.length; i++) {
            if (names[i].length() > 2 && !cheakNameisAllEN(names[i].trim())) {
                // 当名字的第一个字符是字母的时候进入里面
                char temp = names[i].substring(2, names[i].length()).charAt(0);
                //名字的第一个字符是否是英文
                if (regx(temp)) {
                    // 循环每个乘客的姓名这里从第三个开始
                    for (int j = 2; j < names[i].length(); j++) {
                        // 如果第一个是字母但是后面有不是字母的时候匹配
                        if (!regx(names[i].charAt(j))) {
                            nameString += "<br>" + names[i].substring(2, names[i].length());
                            flag = false;
                            break;
                        }
                    }
                }
            }
        }
        return nameString;
    }

    public static boolean regx(char c) {
        String reg = "^[a-zA-Z]$";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(c + "");
        if (m.matches()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 半角转全角
     * @param input String.
     * @return 全角字符串.
     */
    public static String ToSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            }
            else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            }
            else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

    /**
     * 计算每个仓位的折扣
     * 
     * @param listCabinAll
     * @return
     * @time 2015年6月9日 下午7:20:26
     * @author chendong
     * @param yprice 
     */
    public static List<CarbinInfo> reSetDiscount(List<CarbinInfo> listCabinAll, float yprice) {
        //        for (int i = 0; i < listCabinAll.size(); i++) {
        //            CarbinInfo carbininfo = listCabinAll.get(i);
        //            Float discount = 0F;
        //            discount = (carbininfo.getPrice() / yprice) * 100;
        //            carbininfo.setDiscount(discount);
        //        }

        for (int m = 0; m < listCabinAll.size(); m++) {
            if (listCabinAll.get(m).getPrice() != 0 && yprice > 0) {
                Float dis = listCabinAll.get(m).getPrice() / yprice;
                dis = Float.parseFloat(formatMoney(dis));
                // Float st=dis*100;
                BigDecimal big = new BigDecimal(dis);
                double f1 = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                listCabinAll.get(m).setDiscount(Float.parseFloat(f1 * 100 + ""));
                if (dis <= 0.4f) {// 特价
                    listCabinAll.get(m).setSpecial(true);
                }
            }
        }
        return listCabinAll;
    }

    /**
     * 将money格式化为类似于2,243,234.00的格式
     * 
     * @param money
     * @return
     */
    static DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    public static String formatMoney(Float money) {
        format.applyPattern("#,##0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Float.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    /**
     * 对所有测仓位进行排序
     * 
     * @param listCabinAll
     * @return
     * @time 2015年6月9日 下午7:20:13
     * @author chendong
     */
    public static List<CarbinInfo> sortListCabinAll(List<CarbinInfo> listCabinAll) {
        Collections.sort(listCabinAll, new Comparator<CarbinInfo>() {
            @Override
            public int compare(CarbinInfo o1, CarbinInfo o2) {
                if (o1.getPrice() == null || o2.getPrice() == null) {
                    return 1;
                }
                else {
                    // TODO Discount排序
                    if (o1.getPrice() > o2.getPrice()) {
                        return 1;
                    }
                    else if (o1.getPrice() < o2.getPrice()) {
                        return -1;

                    }
                }
                return 0;
            }
        });
        return listCabinAll;
    }

    public static CarbinInfo getlowCabinInfo(List<CarbinInfo> listCabinAll) {
        CarbinInfo lowCabinInfo = new CarbinInfo();
        int cabinIndex = 0;
        for (int k = 0; k < listCabinAll.size(); k++) {
            if (listCabinAll.get(k).getPrice() > 0) {
                cabinIndex = k;
                break;
            }
        }
        if (listCabinAll.size() > 0 && listCabinAll.size() >= cabinIndex) {
            CarbinInfo tempCabinInfo = (CarbinInfo) listCabinAll.get(cabinIndex);
            if (tempCabinInfo.getCabin() != null) {
                lowCabinInfo.setCabin(tempCabinInfo.getCabin());
            }
            if (tempCabinInfo.getRatevalue() != null) {
                lowCabinInfo.setRatevalue(tempCabinInfo.getRatevalue());
            }
            if (tempCabinInfo.getCabinRemark() != null) {
                lowCabinInfo.setCabinRemark(tempCabinInfo.getCabinRemark());
            }
            else {
                lowCabinInfo.setCabinRemark("");
            }
            if (tempCabinInfo.getCabinRules() != null) {
                lowCabinInfo.setCabinRules(tempCabinInfo.getCabinRules());
            }
            else {
                lowCabinInfo.setCabinRules("");
            }
            if (tempCabinInfo.getDiscount() != null) {
                lowCabinInfo.setDiscount(tempCabinInfo.getDiscount());
            }
            if (tempCabinInfo.getLevel() != null) {
                lowCabinInfo.setLevel(tempCabinInfo.getLevel());
            }
            else {
                lowCabinInfo.setLevel(1);
            }
            if (tempCabinInfo.getPrice() != null) {
                lowCabinInfo.setPrice(tempCabinInfo.getPrice());
            }
            else {
                lowCabinInfo.setPrice(0f);
            }
            if (tempCabinInfo.getSeatNum() != null) {
                lowCabinInfo.setSeatNum(tempCabinInfo.getSeatNum());
            }
            else {
                lowCabinInfo.setSeatNum("0");
            }
            if (tempCabinInfo.getCabintypename() != null) {
                lowCabinInfo.setCabintypename(tempCabinInfo.getCabintypename());
            }
            else {
                lowCabinInfo.setCabintypename("");
            }
            if (tempCabinInfo.isSpecial()) {
                lowCabinInfo.setSpecial(true);
            }
            else {
                lowCabinInfo.setSpecial(false);
            }
        }
        else {
        }
        return lowCabinInfo;
    }

    public static void main(String[] args) {
        String ss = "１９９７";
        System.out.println(ss);
        System.out.println(ToDBC(ss));
    }
}
