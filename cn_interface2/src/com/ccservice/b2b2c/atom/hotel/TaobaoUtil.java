package com.ccservice.b2b2c.atom.hotel;

import java.io.*;
import java.net.*;
import java.util.List;
import java.sql.Timestamp;

import net.sf.json.JSONObject;

import com.taobao.api.*;
import com.ccservice.huamin.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.taobaoagent.TaoBaoAgent;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.customeragent.Customeragent;

public class TaobaoUtil {
    /**
     * 获取淘宝客户端
     */
    public static TaobaoClient getClient() throws Exception {
        String url = Server.getInstance().getTaoBaoServerUrl();
        String key = Server.getInstance().getTaoBaoAppKey();
        String secret = Server.getInstance().getTaoBaoAppSecret();
        TaobaoClient client = null;
        try {
            client = new DefaultTaobaoClient(url, key, secret, Constants.FORMAT_JSON);
        }
        catch (Exception e) {
        }
        if (client == null) {
            throw new Exception("获取淘宝客户端失败!");
        }
        return client;
    }

    /**
     * 淘宝返回数据
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static TaobaoResponse getResponse(TaobaoClient client, TaobaoRequest req, String sessionkey)
            throws Exception {
        //淘宝API频率限制：目前允许最大20QPS，总量500W，建议用户每请求一次休息60ms再请求下一次，以防止请求超限
        Thread.sleep(100);
        if (ElongHotelInterfaceUtil.StringIsNull(sessionkey)) {
            throw new Exception("SessionKey为空!");
        }
        TaobaoResponse response = null;
        try {
            response = client.execute(req, sessionkey);
        }
        catch (Exception e) {
        }
        finally {
            //每天流量统计
            try {
                //查询
                String sql = "where C_SESSIONKEY = '" + sessionkey + "'";
                List<TaoBaoAgent> list = Server.getInstance().getHotelService().findAllTaoBaoAgent(sql, "", 1, 0);
                TaoBaoAgent taoBaoAgent = list.get(0);
                long liuliang = taoBaoAgent.getLiuliang() == null ? 0 : taoBaoAgent.getLiuliang().longValue();
                liuliang++;
                //更新
                sql = "update T_TAOBAOAGENT set C_LIULIANG = " + liuliang + " where ID = " + taoBaoAgent.getId();
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            }
            catch (Exception e) {
            }
        }
        if (response == null) {
            throw new Exception("淘宝返回数据为空!");
        }
        return response;
    }

    /**
     * 淘宝图片
     */
    public static FileItem getFileItem(String imgpath) throws Exception {
        FileItem fileItem = null;
        try {
            String filename = imgpath.substring(imgpath.lastIndexOf("/") + 1);
            byte[] content = getImageFromURL(imgpath);
            if (content == null || content.length == 0) {
                throw new Exception("无图片，不能发布!");
            }
            fileItem = new FileItem(filename, content);
        }
        catch (Exception e) {
        }
        if (fileItem == null) {
            return getFileItem("http://121.197.13.153:8067/hotelimage/chain/noimage.jpg");
        }
        return fileItem;
    }

    private static byte[] getImageFromURL(String imgpath) {
        byte[] data = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(imgpath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            is = conn.getInputStream();
            data = readInputStream(is);
        }
        catch (Exception e) {
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (Exception e) {
            }
            conn.disconnect();
        }
        return data;
    }

    private static byte[] readInputStream(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int length = -1;
        try {
            while ((length = is.read(buf)) != -1) {
                baos.write(buf, 0, length);
            }
            baos.flush();
        }
        catch (Exception e) {
        }
        byte[] data = baos.toByteArray();
        try {
            is.close();
            baos.close();
        }
        catch (Exception e) {
        }
        return data;
    }

    //淘宝Sessionkey
    @SuppressWarnings("unchecked")
    public static String saveSessionKey(String code) throws Exception {
        if (ElongHotelInterfaceUtil.StringIsNull(code)) {
            throw new Exception("Code为空。");
        }
        //跳转路径
        String SuccessRedirectUrl = Server.getInstance().getSuccessRedirectUrl().trim();
        //通过code获取Sessionkey和用户名
        String url = "https://oauth.taobao.com/token";
        String param = "client_id=" + Server.getInstance().getTaoBaoAppKey() + "&";
        param += "client_secret=" + Server.getInstance().getTaoBaoAppSecret() + "&";
        param += "grant_type=authorization_code&";
        param += "code=" + code + "&";
        param += "redirect_uri=" + Server.getInstance().getTaoBaoCallBackUrl();
        //Https Post
        byte[] bytes = HttpsUtil.post(url, param, "utf-8");
        String json = new String(bytes);
        //解析JSON
        JSONObject obj = JSONObject.fromObject(json);
        String access_token = obj.getString("access_token");//Sessionkey
        String taobao_user_nick = obj.getString("taobao_user_nick");//淘宝用户昵称
        long expires_in = obj.getLong("expires_in");//access_token有效时长，单位秒
        if (ElongHotelInterfaceUtil.StringIsNull(access_token)) {
            throw new Exception("获取SessionKey失败。");
        }
        if (ElongHotelInterfaceUtil.StringIsNull(taobao_user_nick)) {
            throw new Exception("获取淘宝用户昵称失败。");
        }
        taobao_user_nick = URLDecoder.decode(taobao_user_nick, "utf-8");
        WriteLog.write("淘宝授权", "淘宝用户[" + taobao_user_nick + "]通过授权，JSON：" + json);
        //保存Sessionkey
        String sql = "where C_TAOBAOUSERNAME = '" + taobao_user_nick.trim() + "'";
        List<TaoBaoAgent> list = Server.getInstance().getHotelService().findAllTaoBaoAgent(sql, "", -1, 0);
        String ret = "<script type=\"text/javascript\">";
        if (list != null && list.size() == 1) {
            TaoBaoAgent agent = list.get(0);
            //代理
            Customeragent tempagent = Server.getInstance().getMemberService()
                    .findCustomeragent(agent.getAgentid().longValue());
            if (tempagent == null
                    || (tempagent.getAgentisenable() != null && tempagent.getAgentisenable().intValue() == 0)) {
                throw new Exception("用户已被禁用。");
            }
            agent.setSessionkey(access_token);
            agent.setSessionkeyactivetime(expires_in);
            agent.setSessionkeymodifytime(new Timestamp(System.currentTimeMillis()));
            Server.getInstance().getHotelService().updateTaoBaoAgentIgnoreNull(agent);
            //成功
            int days = (int) (expires_in / 3600 / 24);
            ret += "alert('尊敬的[ " + tempagent.getAgentcompanyname() + " ]，您好，授权成功，SessionKey已更新，有效时长为" + days + "天。');";
            try {
                //自动登陆
                String userSql = "where C_MEMBERTYPE = -1 and C_AGENTID = " + tempagent.getId();
                List<Customeruser> userlist = Server.getInstance().getMemberService()
                        .findAllCustomeruser(userSql, "", -1, 0);
                if (userlist == null || userlist.size() == 0) {
                    throw new Exception("查询用户失败。");
                }
                else {
                    Customeruser user = userlist.get(0);
                    long userid = user.getId();
                    String username = user.getLoginname();
                    String password = user.getLogpassword();
                    long timestamp = System.currentTimeMillis() / 1000 / 3;
                    String sign = ElongHotelInterfaceUtil.MD5(userid + username + password + user.getAgentid()
                            + timestamp);
                    ret += "var html=\"<form action='" + SuccessRedirectUrl;
                    if (!SuccessRedirectUrl.endsWith("/")) {
                        ret += "/";
                    }
                    ret += "login!tologin.action' name='loginform' method='post'>";
                    ret += "<input name='f' type='hidden' value='true'>";
                    ret += "<input name='u' type='hidden' value='" + userid + "'>";
                    ret += "<input name='s' type='hidden' value='" + sign + "'>";
                    ret += "<input name='t' type='hidden' value='" + timestamp + "'>";
                    ret += "</form>\";document.getElementById('BodyEle').innerHTML=html;";
                    ret += "document.loginform.submit();";
                }
            }
            catch (Exception e) {
                ret += "window.location='" + SuccessRedirectUrl + "';";
            }
        }
        else {
            ret += "alert('尊敬的[ " + taobao_user_nick + " ]，您好，授权成功，请联系卖家为您提供登陆账号进行后续操作。');";
            ret += "window.location='" + SuccessRedirectUrl + "';";
        }
        ret += "</script>";
        return ret;
    }
}
