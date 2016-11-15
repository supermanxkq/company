package com.ccservice.b2b2c.atom.servlet.MeiTuanChange;

import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.interfacetype.TrainInterfaceType;
import com.ccservice.b2b2c.atom.service12306.Account12306Util;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.service12306.RepServerUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainAccountSrcUtil;
import com.ccservice.b2b2c.atom.servlet.account.method.TrainRefreshAccountCookieMethod;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * <p>
 * 美团火车票取消
 * </P>
 * 
 * @author zhangqifei
 * @time 2016年9月19日 下午3:17:09
 */
public class MeiTuanCancelTrain {

	public String operate(String orderid, int random) {
		JSONObject retobj = new JSONObject();
		// 存在空值
		if (ElongHotelInterfaceUtil.StringIsNull(orderid)) {
			retobj.put("success", false);
			retobj.put("code", "107");
			retobj.put("msg", "业务参数缺失");
			return retobj.toJSONString();
		}
		Trainform trainform = new Trainform();
		trainform.setQunarordernumber(orderid);
		List<Trainorder> orders = Server.getInstance().getTrainService()
				.findAllTrainorder(trainform, null);
		Trainorder order = new Trainorder();
		// 订单不存在
		if (orders == null || orders.size() == 0) {
			retobj.put("success", false);
			retobj.put("code", "402");
			retobj.put("msg", "订单不存在");
			return retobj.toJSONString();
		} else if (orders.size() > 0) {
			order = orders.get(0);
		}
		if (order.getId() == 0) {
			retobj.put("success", false);
			retobj.put("code", "402");
			retobj.put("msg", "订单不存在");
			return retobj.toJSONString();
		}
		boolean success = false;
		String msg = "";
		String code = "";
		// 订单状态
		int status = order.getOrderstatus();
		int state12306 = order.getState12306();//读取12306状态
		boolean cancelTrue = false;// 是否在12306成功取消
		boolean accountNoLogin = false;// 账号未登录
		WriteLog.write("美团火车票接口_4.7取消火车票订单-test",random + ">>>>>" + order.getId() + ":order:" + JSONObject.toJSONString(order));
		
		//判断订单状态
		if ((status > 1 && status < 8) || (state12306 >= 5 && state12306 <= 8)) {
			retobj.put("success", false);
			retobj.put("code", "112");
			retobj.put("msg", "该订单状态下,不能取消");
			msg = "该订单状态下,不能取消";
			code = "112";
			// return retobj.toJSONString();
		} else if (status == 1) {
			String pingtaiStr = PropertyUtil.getValue("default_pingtaiStr","Train.properties");
			if ("mt".equals(pingtaiStr)) {
				if (state12306 == 1) {
					//更改订单状态
					String sql = "UPDATE T_TRAINORDER SET C_ORDERSTATUS=8 WHERE ID="+ order.getId();
					int res = Server.getInstance().getSystemService().excuteGiftBySql(sql);
					WriteLog.write("美团取消订单-test", "订单号:" + order.getId()+ ":订单未与12306产生交互美团发起取消:修改DB:" + res + ":" + sql);
					retobj.put("success", true);
					retobj.put("code", "100");
					retobj.put("msg", "取消订单成功");
					return retobj.toJSONString();
				} else if (state12306 == 2 || state12306 == 3
						|| state12306 == 18) {
					try {
						Server.getInstance().getSystemService().findMapResultByProcedure(" [sp_TrainorderCanceling_Insert] @OrderId="+ order.getId());
						WriteLog.write("美团取消请求-test", "请求单号---->" + orderid+ "--->取消结果入库");
						// if (insertresult == 1) {
						retobj.put("success", true);
						retobj.put("code", "502");
						retobj.put("msg", "取消请求已接收,占座中");
						//占座中的话 先不回调
						retobj.put("isDisCallBack", true);
						Trainorderrc rz = new Trainorderrc();
						rz.setYwtype(1);
						rz.setCreateuser("系统接口");
						rz.setOrderid(order.getId());
						rz.setStatus(Trainorder.CANCLED);
						rz.setContent("接口申请取消订单,取消请求已接收,占座中。");
						Server.getInstance().getTrainService().createTrainorderrc(rz);
						return retobj.toString();
						// }
					} catch (Exception e) {
						e.printStackTrace();
						WriteLog.write("同程取消请求_ERROR", "请求单号---->" + orderid
								+ "--->取消结果入库失败");
						ExceptionUtil.writelogByException("同程取消请求_ERROR", e);
						retobj.put("success", true);
						retobj.put("code", "999");
						retobj.put("msg", "取消订单失败>>>执行错误");
						return retobj.toString();
					}
				}
			}
			Map map = getTrainorderstatus(order.getId());

			// 12306订单号为空
			// String extnumber = order.getExtnumber();
			String extnumber = gettrainorderinfodatabyMapkey(map, "C_EXTNUMBER");
			String ordertype = gettrainorderinfodatabyMapkey(map, "ordertype");
			// 下单账户
			// String createAccount = order.getSupplyaccount();
			String createAccount = gettrainorderinfodatabyMapkey(map,"C_SUPPLYACCOUNT");
			// 日志
			WriteLog.write("美团火车票接口_4.7取消火车票订单-test",random + ">>>>>" + order.getId() + ">>>>>" + extnumber+ ">>>>>" + createAccount + ">>>>>>>ordertype:"+ ordertype);
			// 账户存在、电子单号存在
			if ((!ElongHotelInterfaceUtil.StringIsNull(createAccount)
					|| "3".equals(ordertype) || "4".equals(ordertype))
					&& !ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
				// 保存
				// saveThirdAccountInfo(order.getId(), json);
				WriteLog.write("美团火车票接口_4.7取消火车票订单",
						"orderid:" + order.getQunarOrdernumber());
				// 查询账户
				order.setSupplyaccount(createAccount);
				if ("3".equals(ordertype) || "4".equals(ordertype)) {
					order.setOrdertype(Integer.valueOf(ordertype));
				}
				Customeruser user = getCustomeruserBy12306Account(order,
						random, true);
				// 账号名不存在、密码错误等，不登录重试，针对第三方传账号和密码，防止重试锁账号等
				if (user != null && user.isDontRetryLogin()) {
					retobj.put("success", false);
					retobj.put("code", "999");
					retobj.put("msg", "取消订单失败>>>" + user.getNationality());
					return retobj.toJSONString();
				}
				// 记录日志
				WriteLog.write("美团火车票接口_4.7取消火车票订单-test", random + ">>>>>"
						+ order.getId() + ">>>>>Cookie>>>>>"
						+ (user == null ? "" : user.getCardnunber()));
				// 未获取到账号或Cookie为空
				if (user == null || ElongHotelInterfaceUtil.StringIsNull(user
								.getCardnunber())) {
					// 账号系统，以未登录释放账号
					if (user != null && user.isFromAccountSystem()) {
						freeCustomeruser(user, AccountSystem.FreeNoLogin,
								AccountSystem.OneFree,
								AccountSystem.ZeroCancel,
								AccountSystem.NullDepartTime);
					}
					retobj.put("success", false);
					retobj.put("code", "999");
					retobj.put("msg", "取消订单失败");
					return retobj.toJSONString();
				}
				// 请求12306
				String url = "";
				String result = "";
				boolean isPhone = isPhoneCancelTrainOrder();

				if (isPhone) {
					try {
						DataTable dataTable = DBHelperAccount
								.GetDataTable("exec [sp_Customeruser_Phone_Select] @Id="
										+ user.getId());
						String sessionId = "";
						String __wl_deviceCtxSession = "";
						String cookie = "";
						String deviceno = "";
						for (DataRow dataRow : dataTable.GetRow()) {
							cookie = dataRow.GetColumnString("C_CARDNUNBER");
							sessionId = dataRow.GetColumnString("C_SESSIONID");
							__wl_deviceCtxSession = dataRow
									.GetColumnString("C_WLDEVICECTXSESSION");
							deviceno = dataRow.GetColumnString("C_DEVICENO");
						}
						if (!"".equals(cookie)) {
							user.setCardnunber(cookie);
						}
						user.setSessionid(sessionId);
						user.setDeviceno(deviceno);
						user.setWldevicectxsession(__wl_deviceCtxSession);
						WriteLog.write("取消订单PHONE_去DB中查询手机端数据-test",
								user.getLoginname() + "--->" + user.getId()
										+ "--->" + cookie + "--->" + sessionId
										+ "--->" + __wl_deviceCtxSession);
					} catch (Exception e) {
						ExceptionUtil.writelogByException(
								"取消订单PHONE_ERROR_去DB中查询手机端数据", e);
					}

				}

				try {
					url = RepServerUtil.getRepServer(user, false).getUrl();
					String param = "";
					if (!isPhone) {
						param = "datatypeflag=10&cookie="
								+ user.getCardnunber() + "&extnumber="
								+ extnumber + "&trainorderid=" + order.getId();
					} else {
						param = "datatypeflag=1010&cookie="
								+ user.getCardnunber() + "&extnumber="
								+ extnumber + "&accountPhone="
								+ CommonAccountPhone(user);
					}
					result = SendPostandGet.submitPost(url, param, "UTF-8")
							.toString();
					if (result.contains("取消订单成功") || "无未支付订单".equals(result)) {
						cancelTrue = true;
					} else if (Account12306Util.accountNoLogin(result, user)) {
						accountNoLogin = true;
					}
				} catch (Exception e) {
					result += ">>>>>Exception>>>>>" + e.getMessage();
				}
				WriteLog.write("美团火车票接口_4.7取消火车票订单-test", random + ">>>>>"
						+ order.getId() + ">>>>>REP服务器地址>>>>>" + url
						+ ">>>>>REP返回>>>>>" + result);
				// 释放账号
				if (cancelTrue) {
					success = true;
					code = "100";
					msg = "取消订单成功";
					freeCustomeruser(user, AccountSystem.FreeNoCare,
							AccountSystem.TwoFree, AccountSystem.OneCancel,
							AccountSystem.NullDepartTime);
				} else {
					if (user.isFromAccountSystem()) {
						freeCustomeruser(user,
								accountNoLogin ? AccountSystem.FreeNoLogin
										: AccountSystem.FreeNoCare,
								AccountSystem.OneFree,
								AccountSystem.ZeroCancel,
								AccountSystem.NullDepartTime);
					}
					retobj.put("success", false);
					retobj.put("code", "999");
					retobj.put("msg", "取消订单失败");
					return retobj.toJSONString();
				}
			}
		}
		return retobj.toJSONString();
	}

	/**
	 * 请求REP账号手机端数据
	 */
	public String CommonAccountPhone(Customeruser user) {
		try {
			JSONObject obj = new JSONObject();
			// 账号相关
			obj.put("account", user.getLoginname());
			obj.put("password", user.getLogpassword());
			obj.put("Cookie", user.getCardnunber());
			obj.put("WL-Instance-Id", user.getSessionid());
			obj.put("__wl_deviceCtxSession", user.getWldevicectxsession());
			obj.put("baseDTO.device_no", user.getDeviceno());
			obj.put("startTime", System.currentTimeMillis());
			return URLEncoder.encode(obj.toString(), "UTF-8");
		} catch (Exception e) {
			ExceptionUtil
					.writelogByException("取消订单PHONE_ERROR_请求REP账号手机端数据", e);
			return "";
		}
	}

	/**
	 * 根据订单id获取 一些信息
	 * 
	 * @param trainorderid
	 * @return
	 * @time 2015年1月22日 下午1:05:36
	 * @author chendong
	 */
	@SuppressWarnings("rawtypes")
	public Map getTrainorderstatus(Long trainorderid) {
		Map map = new HashMap();
		String sql = "SELECT C_ORDERSTATUS,C_CONTACTUSER,C_QUNARORDERNUMBER,C_ORDERNUMBER,"
				+ "C_TOTALPRICE,C_STATE12306,C_EXTNUMBER,C_SUPPLYACCOUNT from T_TRAINORDER where ID="
				+ trainorderid;
		List list = Server.getInstance().getSystemService()
				.findMapResultBySql(sql, null);
		if (list.size() > 0) {
			map = (Map) list.get(0);
		}
		return map;
	}

	/**
	 * 根据查到的map信息获取value
	 * 
	 * @param key
	 * @time 2015年1月22日 下午1:08:54
	 * @author chendong
	 */
	@SuppressWarnings("rawtypes")
	public String gettrainorderinfodatabyMapkey(Map map, String key) {
		String value = "";
		if (map.get(key) != null) {
			try {
				value = map.get(key).toString();
			} catch (Exception e) {
			}
		}
		return value;
	}

	/**
	 * 根据12306账号的用户名获取获取CustomerUser
	 * 
	 * @param refreshCookie
	 *            是否刷新Cookie，客人Cookie方式有效
	 */
	public Customeruser getCustomeruserBy12306Account(Trainorder order,
			int random, boolean refreshCookie) {
		// 客人账号名和密码
		if (order.getOrdertype() == 3) {
			// 取客人数据
			Customeruser temp = TrainAccountSrcUtil
					.getTrainAccountSrcById(order.getId());
			// 走账号系统
			return GetCustomerAccount(temp.getLoginname(),
					temp.getLogpassword());
		}
		// 客人账号的Cookie
		else if (order.getOrdertype() == 4) {
			// 刷新Cookie
			if (refreshCookie) {
				refreshCookieFromInterface(order);
			}
			// 取客人数据
			Customeruser temp = TrainAccountSrcUtil
					.getTrainAccountSrcById(order.getId());
			// 虚拟一个账号
			return GetCustomerAccountByCookieWay(temp);
		}
		// 订单存储
		String loginname = order.getSupplyaccount();
		// 12306用户名
		loginname = loginname.split("/")[0];
		// 走账号系统
		if (GoAccountSystem()) {
			return GetUserFromAccountSystem(AccountSystem.LoginNameAccount,
					loginname, !AccountSystem.waitWhenNoAccount,
					AccountSystem.NullMap);
		}
		Customeruser cust = new Customeruser();
		return cust;
	}

	/**
	 * 账号系统系统获取账号
	 * 
	 * @author WH
	 * @param type
	 *            1:获取下单账号；2:获取身份验证账号；3:账号名获取
	 * @param name
	 *            12306账号名，type为3时有效，其他type可为空
	 * @param waitWhenNoAccount
	 *            无账号的时候是否等待，type为1、2时有效
	 * @param backup
	 *            备用字段
	 */
	public Customeruser GetUserFromAccountSystem(int type, String name,
			boolean waitWhenNoAccount, Map<String, String> backup) {
		return Account12306Util.get12306Account(type, name, waitWhenNoAccount,
				backup);
	}

	/**
	 * 走账号系统
	 * 
	 * @author WH
	 */
	public boolean GoAccountSystem() {
		return true;
		// String AccountOpen = getSysconfigString("12306AccountOpen");
		// return "1".equals(AccountOpen) || "-1".equals(AccountOpen);
	}

	/**
	 * 客人Cookie方式
	 */
	private Customeruser GetCustomerAccountByCookieWay(Customeruser temp) {
		Customeruser user = new Customeruser();
		// 虚拟值
		user.setId(1);
		user.setState(1);
		user.setIsenable(1);
		user.setMemberemail("");
		user.setNationality("");
		user.setLoginname("Cookie");
		user.setLogpassword("Cookie");
		user.setCustomerAccount(true);
		user.setFromAccountSystem(true);
		user.setCardnunber(temp.getCardnunber());
		user.setPostalcode(temp.getPostalcode());
		return user;
	}

	/**
	 * 获取客户账号Cookie
	 * 
	 * @author WH
	 * @param name
	 *            账号名称
	 * @param password
	 *            账号密码
	 */
	public Customeruser GetCustomerAccount(String name, String password) {
		if (ElongHotelInterfaceUtil.StringIsNull(name)
				|| ElongHotelInterfaceUtil.StringIsNull(password)) {
			return new Customeruser();
		} else {
			Map<String, String> backup = new HashMap<String, String>();
			backup.put("password", password);
			Customeruser user = Account12306Util.get12306Account(4, name, true,
					backup);
			user.setCustomerAccount(true);
			return user;
		}
	}

	/**
	 * Cookie无效时，调用接口刷新Cookie
	 * 
	 * @author WH
	 */
	@SuppressWarnings("rawtypes")
	private void refreshCookieFromInterface(Trainorder order) {
		try {
			if (ElongHotelInterfaceUtil.StringIsNull(order
					.getQunarOrdernumber()) || order.getAgentid() == 0) {
				String sql = "select C_QUNARORDERNUMBER, C_AGENTID, ISNULL(C_INTERFACETYPE, 0) C_INTERFACETYPE "
						+ "from T_TRAINORDER with(nolock) where ID = "
						+ order.getId();
				List list = Server.getInstance().getSystemService()
						.findMapResultBySql(sql, null);
				// 查询成功
				if (list.size() > 0) {
					Map map = (Map) list.get(0);
					order.setAgentid(Long.valueOf(map.get("C_AGENTID")
							.toString()));
					order.setQunarOrdernumber(map.get("C_QUNARORDERNUMBER")
							.toString());
					order.setInterfacetype(Integer.valueOf(map.get(
							"C_INTERFACETYPE").toString()));
				}
			}
			int interfaceType = order.getInterfacetype() == null ? 0 : order
					.getInterfacetype();
			interfaceType = interfaceType > 0 ? interfaceType
					: getOrderAttribution(order);
			// 淘宝
			if (interfaceType == TrainInterfaceMethod.TAOBAO) {
				// 参数
				JSONObject param = new JSONObject();
				param.put("orderid", order.getId());
				param.put("agentid", order.getAgentid());
				param.put("interfaceOrderNumber", order.getQunarOrdernumber());
				// 地址
				String url = PropertyUtil.getValue("fresh_cookie_taobao_url",
						"Train.properties");
				// 请求
				SendPostandGet.submitGet(url + "?jsonStr=" + param, "UTF-8");
			}
			// 其他
			else {
				// 参数
				JSONObject json = new JSONObject();
				json.put("orderid", order.getId());
				json.put("agentid", order.getAgentid());
				json.put("interfaceType", interfaceType);
				json.put("interfaceOrderNumber", order.getQunarOrdernumber());

				String logName = "TrainRefreshAccountCookieServlet";
				int random = new Random().nextInt(9000000) + 1000000;
				new TrainRefreshAccountCookieMethod().refreshCookie(logName,
						json, random);
			}
		} catch (Exception exception) {
			ExceptionUtil.writelogByException(
					"RefreshCookieFromInterface_Error", exception);
		}
	}

	/**
	 * 确定订单归属
	 */
	public int getOrderAttribution(Trainorder trainorder) {
		int interfacetype = 0;
		interfacetype = new TrainInterfaceType()
				.getTrainInterfaceType(trainorder.getId());
		return interfacetype;
	}

	/**
	 * 占用一个账号后的解锁， 支付完成等调用
	 * 
	 * 以下为账号系统备注
	 * 
	 * @param freeType
	 *            释放类型 1:NoCare；2:仅当天使用；3:发车时间后才可使用；4:分配给其他业务(暂未用)
	 * @param cancalCount
	 *            取消次数，用于取消时释放账号，其他业务必须传0
	 * @param departTime
	 *            发车时间，freeType为3时有效，其他请设为空
	 */
	public void freeCustomeruser(Customeruser user, int freeType,
			int freeCount, int cancalCount, Timestamp departTime) {
		if (user == null) {
			return;
		}
		// 账号系统释放账号
		if (user.isFromAccountSystem()) {
			FreeUserFromAccountSystem(user, freeType, freeCount, cancalCount,
					departTime);
		}
		// 原先系统释放账号
		else {
			try {
				String sql = "UPDATE T_CUSTOMERUSER SET C_ENNAME = '1' where id="
						+ user.getId();
				Server.getInstance().getSystemService()
						.excuteAdvertisementBySql(sql);
			} catch (Exception e) {
				// logger.error("freecustomeruser", e.fillInStackTrace());
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("customeruserObject", user);
				sendMQmessage("update_12306account_err",
						jsonobject.toJSONString());
			}
		}
	}

	/**
	 * 
	 * @param QUEUE_NAME
	 * @param message
	 * 
	 * @time 2015年1月27日 上午11:48:04
	 * @author chendong
	 */
	public void sendMQmessage(String QUEUE_NAME, String message) {
		String url = getSysconfigString("activeMQ_url");
		// String url = "tcp://192.168.0.5:61616";
		try {
			ActiveMQUtil.sendMessage(url, QUEUE_NAME, message);
		} catch (Exception e) {
			// logger.error("MQ_err", e.fillInStackTrace());
		}
	}

	/**
	 * 根据sysconfig的name获得value 内存中
	 * 
	 * @param name
	 * @return
	 */
	protected String getSysconfigString(String name) {
		String result = "-1";
		try {
			if (Server.getInstance().getDateHashMap().get(name) == null) {
				List<Sysconfig> sysoconfigs = Server
						.getInstance()
						.getSystemService()
						.findAllSysconfig("WHERE C_NAME='" + name + "'", "",
								-1, 0);
				if (sysoconfigs.size() > 0) {
					WriteLog.write("TongchengSupplyMethod_getcustomeruser", ""
							+ sysoconfigs.size());
					result = sysoconfigs.get(0).getValue() != null ? sysoconfigs
							.get(0).getValue() : "-1";
					Server.getInstance().getDateHashMap().put(name, result);
				}
			} else {
				result = Server.getInstance().getDateHashMap().get(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public boolean isPhoneCancelTrainOrder() {
		try {
			List list = Server
					.getInstance()
					.getSystemService()
					.findMapResultByProcedure(
							"sp_TrainOrderIsPhone_Select_CancelTrainOrder");
			if (list.size() > 0) {
				Map map = (Map) list.get(0);
				if ("1".equals(map.get("IsPhone").toString())) {
					return true;
				}
			}
		} catch (Exception e) {
			ExceptionUtil.writelogByException("PHONE_ERROR_isPhoneOrder", e);
		}
		return false;
	}

	/**
	 * 账号系统释放12306账号
	 * 
	 * @author WH
	 * @param user
	 *            12306账号
	 * @param freeType
	 *            释放类型 1:NoCare；2:仅当天使用；3:发车时间后才可使用；4:分配给其他业务(暂未用)；
	 *            其他详见AccountSystem类
	 * @param freeCount
	 *            释放次数，1或2次
	 * @param cancalCount
	 *            取消次数，用于取消时释放账号，其他业务必须传0
	 * @param departTime
	 *            发车时间，freeType为3时有效，其他请设为空
	 */
	public void FreeUserFromAccountSystem(Customeruser user, int freeType,
			int freeCount, int cancalCount, Timestamp departTime) {
		Account12306Util.free12306Account(user, freeType, freeCount,
				cancalCount, departTime, !AccountSystem.checkPassenger);
	}

}
