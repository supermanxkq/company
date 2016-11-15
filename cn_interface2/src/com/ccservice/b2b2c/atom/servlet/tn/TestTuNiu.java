package com.ccservice.b2b2c.atom.servlet.tn;

import java.io.IOException;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;

public class TestTuNiu {
    public static void main(String[] args) {
        String url = "http://120.26.83.131:9022/cn_interface/trainAccount/contact/query";
        JSONObject json = new JSONObject();
        json.put("account", "tuniulvyou");
        json.put("sign", "61f25c8cd1bffd7d70705427ddde56ba");
        json.put("timestamp", "2015-11-17 15:12:06");
        json.put("data", "W3sidHJhaW5BY2NvdW50IjoiZGFud2VpZmVuZzEiLCJwYXNzIjoic3dmMTk5MDEwMTgifV0=");
        String a = SendPostandGet.submitPost(url, "json=" + json.toJSONString(), "utf-8").toString();
        System.out.println(a);
    }

}
