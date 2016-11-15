package com.insurance;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;

import org.apache.axis2.AxisFault;

import client.PolicyServiceShareStub;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.eaccount.Eaccount;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.util.Insurances;

public class JinRiInsurance implements IInsuranceBook {
//	WriteLog writeLog = new WriteLog();

	@Override
	public DataHandler PolicyReprint(Insurorder order) throws Exception {
		return null;
	}

	@Override
	public List newOrderAplylist(String[] jyNo, List list) {
		List<String> policynos = new ArrayList<String>();
		JinRiBook jinribook = new JinRiBook();
		for (int i = 0; i < list.size(); i++) {
			Insuruser user = (Insuruser) list.get(i);
			try {
				PolicyServiceShareStub stub = new PolicyServiceShareStub();
				PolicyServiceShareStub.PolicyIssuing policyIssuing = new PolicyServiceShareStub.PolicyIssuing();
				policyIssuing.setUserNm(jinribook.getJRUSER());
				policyIssuing.setPswd(jinribook.getJRPassword());
				policyIssuing.setKey(jinribook.getJRKey());
				policyIssuing.setPdtCd(jinribook.getPdtCd());
				String planeOrderNo = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(new Date());
				policyIssuing.setPlaneOrderNo(planeOrderNo);
				String info = user.getName() + ",";
				String sexString = "女,";
				if (user.getCodetype() == 3) {
					info += "护照,";
				} else if (user.getCodetype() == 1) {
					info += "身份证,";
					if(getsex(user.getCode())){
						sexString="男,";
					}
				} else if (user.getCodetype() == 4) {
					info += "港澳通行证,";
				} else if (user.getCodetype() == 5) {
					info += "台湾通行证,";
				} else if (user.getCodetype() == 6) {
					info += "台胞证,";
				} else if (user.getCodetype() == 7) {
					info += "回乡证,";
				} else if (user.getCodetype() == 8) {
					info += "军官证,";
				} else{
					info += "其他,";
				}
				info += user.getCode() + ",";
				info += user.getBirthday().toString().substring(0, 10) + ",";
				info += sexString;
				info += user.getBegintime().toString().substring(0, 10) + ",";
				info += user.getFlyno() + ",";
				info += user.getMobile() + ",";
				info += user.getEmail();
				WriteLog.write("今日保险", "下单前信息:" + jinribook.getPdtCd() + ":"
						+ jinribook.getJRUSER() + ":"
						+ jinribook.getJRPassword() + ":"
						+ jinribook.getJRKey() + ":" + planeOrderNo + ":info:"
						+ info);
				policyIssuing.setInfo(info);
				PolicyServiceShareStub.PolicyIssuingResponse res = stub.policyIssuing(policyIssuing);
				// 保单号及投保相应信息, 如 2804019729818|投保成功
//				String result = "";
				String result = res.getPolicyIssuingResult();
				WriteLog.write("今日保险", "下单后返回的信息:" + result);
				if (result.indexOf('|') > 0) {
					String[] temps = result.split("[|]");
					if (temps[1].indexOf("投保成功") >= 0) {
						policynos.add(temps[0]);
					}
				}
			} catch (AxisFault e) {
				WriteLog.write("今日保险", "下单后返回的信息:" + e.getMessage());
				e.printStackTrace();
			} catch (RemoteException e) {
				WriteLog.write("今日保险", "下单后返回的信息:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return policynos;
	}

	/**
	 * 根据身份证号判断是男是女
	 * @param idcode
	 * @return男true女false
	 */
	public boolean getsex(String idcode){
		return (int)idcode.charAt(16)%2==1;
	}
	
	public static void main(String[] args) {
//		JinRiInsurance jinRi = new JinRiInsurance();
//		jinRi.policyQuery("");
		System.out.println((int)"412823198909298017".charAt(16)%2==1);
	}

	/**
	 * 退保接口
	 * 
	 * @param policyNo
	 *            保单号
	 */
	public String policyWithdraw(String policyNo) {
		try {
			JinRiBook jinribook = new JinRiBook();
			PolicyServiceShareStub stub = new PolicyServiceShareStub();
			PolicyServiceShareStub.PolicyWithdraw policyWithdraw = new PolicyServiceShareStub.PolicyWithdraw();
			policyWithdraw.setUserNm(jinribook.getJRUSER());
			policyWithdraw.setKey(jinribook.getJRKey());
			policyWithdraw.setPswd(jinribook.getJRPassword());
			policyWithdraw.setPdtCd(jinribook.getPdtCd());
			policyWithdraw.setPolicyNo(policyNo);
			PolicyServiceShareStub.PolicyWithdrawResponse res = stub
					.policyWithdraw(policyWithdraw);
			String result = res.getPolicyWithdrawResult();
			WriteLog.write("今日保险", "退保:" + policyNo + ":" + result);
			if("退保成功".equals(result)){
				return "1";
			}else{
				return "0";
			}
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return "0";
	}

	/**
	 * 查询接口
	 */
	public void policyQuery(String info) {
		try {
			//李彬,身份证,411324198302084256,1983-02-08,男,2013-01-22,CZ3455,13509663596,szxlyx168@163.com
			info = "李彬,身份证,411324198302084256,1983-02-08,男,2013-01-22,CZ3455,13509663596,szxlyx168@163.com";
			PolicyServiceShareStub stub = new PolicyServiceShareStub();
			PolicyServiceShareStub.PolicyQuery policyQuery = new PolicyServiceShareStub.PolicyQuery();
			policyQuery.setUserNm("szxlyx");
			policyQuery.setPswd("81776777");
			policyQuery.setKey("8s2powj3jd");
			policyQuery.setPdtCd("6");
			policyQuery.setPlaneOrderNo("20130121160303");
			policyQuery.setInfo(info);
			PolicyServiceShareStub.PolicyQueryResponse resp = stub
					.policyQuery(policyQuery);
			String text = resp.getPolicyQueryResult().getExtraElement()
					.getText();
			
			System.out.println(text);
		} catch (AxisFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Insurances> orderAplylist(String jyNo, Customeruser user,
			List list, String begintime, String[] fltno) throws Exception {
		return null;
	}

	private class JinRiBook {
		private String JRURL = "";// 今日url
		private String JRUSER = "";// 今日账号
		private String JRKey = "";// 今日账号下级账号
		private String staus = "";// 接口是否启用 1,启用 0,禁用
		private String JRXiausername = "";
		private String JROFFICEID = "";
		private String JRPassword = "";
		// 8平安养老B - 中国平安行-交通意外伤害保险4.5
		// 6泰康新生活交通工具意外伤害保险(6)5.6
		private String PdtCd = "6";

		public JinRiBook() {
			List<Eaccount> listEaccountJinri = Server
					.getInstance()
					.getSystemService()
					.findAllEaccount(
							" where 1=1 and " + Eaccount.COL_name + " ='今日天下通'",
							" ORDER BY ID ", -1, 0);
			if (listEaccountJinri.size() > 0) {
				JRURL = listEaccountJinri.get(0).getUrl();
				JRUSER = listEaccountJinri.get(0).getUsername();
				JRKey = listEaccountJinri.get(0).getPwd();
				JRXiausername = listEaccountJinri.get(0).getXiausername();
				JRPassword = listEaccountJinri.get(0).getPassword();
				JROFFICEID = listEaccountJinri.get(0).getEdesc();
				PdtCd = listEaccountJinri.get(0).getNourl();
				staus = "1";
			} else {
				staus = "0";
				System.out.println("--------今日被禁用-----------------");
			}
		}

		public String getJRPassword() {
			return JRPassword;
		}

		public void setJRPassword(String password) {
			JRPassword = password;
		}

		public String getJRURL() {
			return JRURL;
		}

		public void setJRURL(String jrurl) {
			JRURL = jrurl;
		}

		public String getJRUSER() {
			return JRUSER;
		}

		public void setJRUSER(String jruser) {
			JRUSER = jruser;
		}

		public String getJRKey() {
			return JRKey;
		}

		public void setJRKey(String key) {
			JRKey = key;
		}

		public String getStaus() {
			return staus;
		}

		public void setStaus(String staus) {
			this.staus = staus;
		}

		public String getJRXiausername() {
			return JRXiausername;
		}

		public void setJRXiausername(String xiausername) {
			JRXiausername = xiausername;
		}

		public String getJROFFICEID() {
			return JROFFICEID;
		}

		public void setJROFFICEID(String jrofficeid) {
			JROFFICEID = jrofficeid;
		}

		public String getPdtCd() {
			return PdtCd;
		}

		public void setPdtCd(String pdtCd) {
			PdtCd = pdtCd;
		}

	}

	@Override
	public String cancelOrderAplylist(Insuruser insur) {
		return policyWithdraw(insur.getPolicyno());
	}

	@Override
	public List saveTrainOrderAplylist(String[] jyNo, List list, int type) {
		// TODO Auto-generated method stub
		return null;
	}
}