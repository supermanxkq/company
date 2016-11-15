package com.uniproud.axis2.client;
/**
 * 转换文件为base64的字符串
 * GuestCompany uniproud
 * author shqv
 */
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.BASE64Encoder;

public class Base64 {
  public String getEncodedText(String filePath) {
    String text = null;
    InputStream in = null;
    try {
      in = new BufferedInputStream(new FileInputStream(filePath));
      byte[] buffer = new byte[in.available()];
      in.read(buffer);
      BASE64Encoder encoder = new BASE64Encoder();
      text = encoder.encode(buffer);
//      System.out.println(text.length());
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      if (in != null) {
        try {
          in.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return text;
  }
  /**
    * 把base64的字符串转换成文件
    * @param base64Str  转换成的base64编码的字符流
    * @param fileName   存放的文件名称
    * @return
    */
   public boolean tranfile(String base64Str,String fileName){
//       System.out.println("base64Str.length=="+base64Str.length());
       sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
       try{
          byte[] b = decoder.decodeBuffer(base64Str);
          ByteArrayInputStream in = new ByteArrayInputStream(b);
          byte[] buffer = new byte[b.length];
          FileOutputStream out = new FileOutputStream(fileName);
          int bytesum = 0;
          int byteread = 0;
          while((byteread = in.read(buffer)) != -1) {
              bytesum += byteread;
              out.write(buffer, 0, byteread);
          }
          out.close();
        }catch (IOException e) {
          e.printStackTrace();
          return false;
        }
       return true;
   }
}