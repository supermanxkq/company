/**
 * 
 */
package com.ccservice.b2b2c.atom.servlet.format.json;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alipay.client.util.StringUtil;

/**
 * 把带中文的json都encode下
 * 改签回调修改json的格式的工具类
 * 把需要encode的字段encode下
 * @time 2015年11月2日 上午11:54:23
 * @author chendong
 */
public class ValueFilterTuniuChangeValue implements ValueFilter {

    public static void main(String[] args) {
        System.out
                .println(getNewJSONString("{\"sign\":\"3b69b2009f1db056120c43afbd8b2a2d\",\"partnerid\":\"tuniu_test\",\"reqtoken\":\"45313\",\"oldticketchangeserial\":\"\",\"reqtime\":\"20151102212039\",\"newticketcxins\":[{\"new_ticket_no\":\"E0486526302050021\",\"old_ticket_no\":\"E0486526301050013\",\"cxin\":\"05车厢,021号\"},{\"new_ticket_no\":\"E0486526302050022\",\"old_ticket_no\":\"E0486526301050015\",\"cxin\":\"05车厢,022号\"}],\"method\":\"train_confirm_change\",\"orderid\":\"10817431T45295\",\"newticketchangeserial\":\"\",\"code\":\"100\",\"msg\":\"确认改签成功\",\"success\":true}"));
    }

    /**
     * 把带中文的json都encode下
     * @time 2015年11月2日 下午12:41:46
     * @author chendong
     */
    public static String getNewJSONString(String JSONString) {
        JSONObject jsonObject = JSONObject.parseObject(JSONString);
        jsonObject = JSONObject.parseObject(jsonObject.toJSONString());
        ValueFilterTuniuChangeValue valueFilterTuniuChangeValue = new ValueFilterTuniuChangeValue();
        JSONArray jsonArray = (JSONArray) jsonObject.remove("newtickets");
        int flag = 1;
        if (jsonArray == null) {
            jsonArray = (JSONArray) jsonObject.remove("newticketcxins");
            flag = 2;
        }
        String reslut2 = JSONObject.toJSONString(jsonArray, valueFilterTuniuChangeValue);
        JSONArray jsonArrayold = JSONArray.parseArray(reslut2);
        String reslut1 = JSONObject.toJSONString(jsonObject, valueFilterTuniuChangeValue);
        jsonObject = JSONObject.parseObject(reslut1);
        if (flag == 1) {
            jsonObject.put("newtickets", jsonArrayold);
        }
        else if (flag == 2) {
            jsonObject.put("newticketcxins", jsonArrayold);
        }
        return jsonObject.toJSONString();
    }

    /* (non-Javadoc)
     * @see com.alibaba.fastjson.serializer.NameFilter#process(java.lang.Object, java.lang.String, java.lang.Object)
     */
    @Override
    public String process(Object source, String name, Object value) {
        //        System.out.println(value.getClass().getSimpleName());
        //        System.out.println(name + ":" + value);

        if ("JSONArray".equals(value.getClass().getSimpleName())) {
            JSONArray jsonarray = (JSONArray) value;
            return jsonarray.toJSONString();
        }
        else {
            if ("from_station_name".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("to_station_name".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("msg".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("help_info".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("priceinfo".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("zwname".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("flagmsg".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else if ("cxin".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else {
                return value.toString();
            }
        }
    }

}
