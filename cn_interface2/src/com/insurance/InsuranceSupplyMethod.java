package com.insurance;

import com.ccservice.b2b2c.atom.component.PublicComponent;

public class InsuranceSupplyMethod extends PublicComponent {
    /**
     *判断是否消失18岁，如果小于返回true
     * @param time
     * @return
     */
    public boolean ischild(long time) {
        long weichengnian = 18 * 1000 * 60 * 60 * 24 * 365L;
        if (System.currentTimeMillis() - time > weichengnian) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     *  数据库证件类型转换成保险接口证件类型
     * @param timestamp
     * @return
     * @time 2014年9月9日 下午3:29:31
     * @author yinshubin
     */
    public String getcodetype(long codetype) {
        //        乘客证件类型：10: 身份证11: 户口薄12: 驾驶证13: 军官证14: 士兵证17: 港澳通行证18: 台湾通行证99: 其他51: 护照61: 港台同胞证
        String CardType = "99";
        if (codetype == 3) {//护照
            CardType = "51";
        }
        else if (codetype == 1) {//身份证
            CardType = "10";
        }
        else if (codetype == 4) {//港澳通行证
            CardType = "17";
        }
        else if (codetype == 5) {//台湾通行证
            CardType = "18";
        }
        else if (codetype == 6) {//台胞证
            CardType = "99";
        }
        else if (codetype == 7) {//回乡证
            CardType = "99";
        }
        else if (codetype == 8) {//军官证
            CardType = "13";
        }
        else {
            CardType = "99";
        }
        return CardType;
    }

    /**
     * 根据身份证号码获取性别
     * M男  F女
     * 
     * @param id
     * @return
     * @time 2015年8月27日 下午5:34:42
     * @author chendong
     */
    public String getSexById(String id) {
        //        String id = "510111199212018271";
        String sex = "M";
        try {
            String birth = id.substring(6, 14);
            sex = id.substring(16, 17);
            if (Integer.parseInt(sex) % 2 == 0) {
                sex = "F";
            }
            else {
                sex = "M";
            }
        }
        catch (Exception e) {
        }
        return sex;
    }
}
