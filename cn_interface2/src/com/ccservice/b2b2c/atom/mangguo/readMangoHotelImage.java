package com.ccservice.b2b2c.atom.mangguo;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.ccservice.b2b2c.base.service.IHotelService;

public class readMangoHotelImage {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/lthk_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
		for(int count=17840;count<=20384;count++)
		{
			Hotel hotel=servier.findHotel(count);
			System.out.println("count:"+count);
			if(hotel.getCheckdesc()!=null&&hotel.getCheckdesc().length()>4)
			{
			String images=hotel.getCheckdesc().substring(4);
			String[] imagesarry=images.split(",");
			File file=new File("D:\\hotelimage\\2011-05-23\\"+hotel.getId());
			file.mkdirs();
			for(int i=0;i<imagesarry.length;i++)
			{
				try
				{
				download("http://himg.mangocity.com/img/upload/Images/"+imagesarry[i], "D:\\hotelimage\\2011-05-23\\"+hotel.getId()+"\\"+imagesarry[i]);
				Hotelimage hotelimage=new Hotelimage();
				hotelimage.setHotelid(hotel.getId());
				hotelimage.setLanguage(0);
				hotelimage.setType(1);
				hotelimage.setPath("/2011-05-23/"+hotel.getId()+"/"+imagesarry[i].replace(" ", ""));
				servier.createHotelimage(hotelimage);
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println(imagesarry[i]);
				}
			}
			}
		}
  }
  /**  
   * 下载文件到本地  
   *   
   * @param urlString  
   *          被下载的文件地址  
   * @param filename  
   *          本地文件名  
   * @throws Exception  
   *           各种异常  
   */  
  public static void download(String urlString, String filename) throws Exception {    
    // 构造URL    
	urlString=urlString.replace(" ", "%20");
	filename=filename.replace(" ", "");
    URL url = new URL(urlString);    
    // 打开连接    
    URLConnection con = url.openConnection();   
    // 输入流    
    java.io.InputStream is = con.getInputStream();   
  
    // 1K的数据缓冲    
    byte[] bs = new byte[1024];    
    // 读取到的数据长度    
    int len;    
    // 输出的文件流    
    java.io.OutputStream os = new FileOutputStream(filename);    
    // 开始读取    
    while ((len = is.read(bs)) != -1) {    
      os.write(bs, 0, len);    
   }   
    // 完毕，关闭所有链接    
    os.close();   
    is.close();   
  }    
}  