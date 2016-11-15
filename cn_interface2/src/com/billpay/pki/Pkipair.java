package com.billpay.pki;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class Pkipair {
    public static void main(String[] args) throws Exception {
        String desc = "机票款";
        String signStr = new Pkipair().signMsg(desc, "46");
        System.out.println(signStr);

    }

    public String signMsg(String signMsg, String agentID) {
        String base64 = "";
        try {
            // 密钥仓库
            KeyStore ks = KeyStore.getInstance("PKCS12");//固定值；
            // 读取密钥仓库
            FileInputStream ksfis = new FileInputStream("c:/cer/" + agentID + "bill-rsa.pfx");
            BufferedInputStream ksbufin = new BufferedInputStream(ksfis);

            char[] keyPwd = "123456".toCharArray();//5n0wbIrdMe1:密码，生成文件时的密码。为避免重复修改，请生成时使用此密码。
            ks.load(ksbufin, keyPwd);
            // 从密钥仓库得到私钥
            PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);//固定值。
            Signature signature = Signature.getInstance("SHA1withRSA");//固定值/
            signature.initSign(priK);
            signature.update(signMsg.trim().getBytes("UTF-8"));
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            base64 = encoder.encode(signature.sign());

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("文件找不到");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        //System.out.println("test = "+base64);
        return base64;
    }

    public boolean enCodeByCer(String val, String msg, String agentId) {
        boolean flag = false;
        try {
            //获得文件
            InputStream inStream = new FileInputStream("c:/cer/99bill" + agentId + ".cer");
            //InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(" \\demo\\99bill[1].cert.rsa.20140728.cer");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
            //获得公钥
            PublicKey pk = cert.getPublicKey();
            //签名
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initVerify(pk);
            signature.update(val.getBytes());
            //解码
            sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
            flag = signature.verify(decoder.decodeBuffer(msg));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件找不到");
        }
        return flag;
    }

}
