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
public class ValueFilterTCPassengerChangeValue implements ValueFilter {

    public static void main(String[] args) {
        String t = getNewJSONString("{\"accounts\":[{\"accountstatusid\":\"2\",\"accountstatusname\":\"可用\",\"passengers\":[{\"operationtypename\":\"新增\",\"operationtime\":\"20151109195719\",\"passengersename\":\"许道玉\",\"passporttypeseidname\":\"二代身份证\",\"passportseno\":\"362323196811086218\",\"passengertypename\":\"成人票\",\"passporttypeseid\":\"1\",\"operationtypeid\":1,\"passengertypeid\":\"1\"}],\"accountname\":\"0\"}]}");
        System.out.println("==");
        System.out.println(t);
    }

    /**
     * 把带中文的json都encode下
     * @time 2015年11月2日 下午12:41:46
     * @author chendong
     */
    public static String getNewJSONString(String JSONString) {
        JSONObject jsonObject = JSONObject.parseObject(JSONString);
        jsonObject = JSONObject.parseObject(jsonObject.toJSONString());
        ValueFilterTCPassengerChangeValue valueFilterTuniuChangeValue = new ValueFilterTCPassengerChangeValue();
        JSONArray jsonArray = (JSONArray) jsonObject.remove("accounts");

        JSONArray jsonArray_new = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject_t = jsonArray.getJSONObject(i);
            String reslut_t = JSONObject.toJSONString(jsonObject_t, valueFilterTuniuChangeValue);
            System.out.println(i + ":i:" + reslut_t);
            JSONArray jsonArray_passengers = (JSONArray) jsonObject_t.remove("passengers");
            String reslut_jsonObject_t = JSONObject.toJSONString(jsonObject_t, valueFilterTuniuChangeValue);
            System.out.println(reslut_jsonObject_t);
            jsonObject_t = JSONObject.parseObject(reslut_jsonObject_t);
            JSONArray jsonArray_passengers_new = new JSONArray();
            for (int j = 0; j < jsonArray_passengers.size(); j++) {
                JSONObject jsonObject_j = jsonArray_passengers.getJSONObject(j);
                String reslut_j = JSONObject.toJSONString(jsonObject_j, valueFilterTuniuChangeValue);
                System.out.println(j + ":j:" + reslut_j);
                jsonArray_passengers_new.add(JSONObject.parseObject(reslut_j));
            }
            System.out.println(i + ":i:" + jsonArray_passengers_new.toJSONString());
            jsonObject_t.put("passengers", jsonArray_passengers_new);
            jsonArray_new.add(jsonObject_t);
        }
        jsonObject.put("accounts", jsonArray_new);
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
            if ("accountstatusname".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            if ("operationtypename".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            if ("passengersename".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            if ("passporttypeseidname".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            if ("passengertypename".equals(name)) {
                return StringUtil.geturlencode(value.toString());
            }
            else {
                return value.toString();
            }
        }
    }

}
