package com.ccservice.b2b2c.atom.hthy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;

/**
 * 捷旅订单状态接口
 * 
 * @author sefvang
 * 
 */
public class HTHYHotelStateUpdateInterface extends HttpServlet {

	private static final long serialVersionUID = -6801078082790552652L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = null;
		response.setCharacterEncoding("UTF-8");

		String result = "";
		try {
			out = response.getWriter();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					request.getInputStream(), "UTF-8"));
			String line = "";
			StringBuffer buffer = new StringBuffer(1024);
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			// 修改订单状态的
			if (buffer != null) {
				Document document;
				SAXBuilder sb = new SAXBuilder();
				document = sb.build(new StringReader(buffer.toString()));
				Element root = document.getRootElement();
				String stomerordercd = root.getChildText("stomerordercd");
				String authno = root.getChildText("authno");
				String businesstype = root.getChildText("businesstype");
				String orderid = root.getChildText("orderid");
				String ordercd = root.getChildText("ordercd");
				String customerordercd = root.getChildText("customerordercd");
				String orderstatus = root.getChildText("orderstatus");

			  /*if ("11".equals(orderstatus)) {
					orderstatus = "草稿单";
				} else if ("12".equals(orderstatus)) {
					orderstatus = "待处理";
				} else if ("13".equals(orderstatus)) {
					orderstatus = "待确认";
				} else if ("14".equals(orderstatus)) {
					orderstatus = "已确认";
				} else if ("18".equals(orderstatus)) {
					orderstatus = "已撤单";
				}*/
				Hotelorder hod=new Hotelorder();
				hod.setState(Integer.parseInt(orderstatus));
				//更新状态
				Server.getInstance().getHotelService().updateHotelorderIgnoreNull(hod);
				System.out.println(orderstatus);
				String hotelconfirmfaxsent = root
						.getChildText("hotelconfirmfaxsent");
				String hotelconfirmfax = root.getChildText("hotelconfirmfax");
				String customerconfirmnotes = root
						.getChildText("customerconfirmnotes");
				String paystatus = root.getChildText("paystatus");
				String receivestatus = root.getChildText("receivestatus");

				result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><order><result>1</result><customercd>sz2747</customercd><authno>abc,jltour</authno><orderid>"
						+ orderid
						+ "</orderid><ordercd>"
						+ ordercd
						+ "</ordercd><customerordercd>"
						+ customerordercd
						+ "</customerordercd><businesstype>orderstatus</businesstype><error/></order>";
			}

			out.print(result);
		} catch (Exception e1) {
			e1.printStackTrace();
			out.print(result);
		} finally {
			out.close();
		}

	}

}
