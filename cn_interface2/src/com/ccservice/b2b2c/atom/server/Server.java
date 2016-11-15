package com.ccservice.b2b2c.atom.server;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.IAtomService;
import com.ccservice.b2b2c.atom.service.IELongHotelService;
import com.ccservice.b2b2c.atom.service.IHMHotelService;
import com.ccservice.b2b2c.atom.service.IManGoHotelService;
import com.ccservice.b2b2c.atom.service.IYiHaiCarService;
import com.ccservice.b2b2c.atom.service12306.ITrain12306Service;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.eaccount.Eaccount;
import com.ccservice.b2b2c.base.service.IAirService;
import com.ccservice.b2b2c.base.service.IAppconfigService;
import com.ccservice.b2b2c.base.service.IB2BAirticketService;
import com.ccservice.b2b2c.base.service.IB2BSystemService;
import com.ccservice.b2b2c.base.service.IBusinessMateService;
import com.ccservice.b2b2c.base.service.ICarService;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.ccservice.b2b2c.base.service.IInterhotelService;
import com.ccservice.b2b2c.base.service.IInterticketService;
import com.ccservice.b2b2c.base.service.IMemberService;
import com.ccservice.b2b2c.base.service.ISearchFlightService;
import com.ccservice.b2b2c.base.service.IServicecenterService;
import com.ccservice.b2b2c.base.service.ISingleTripInService;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.b2b2c.base.service.ITicketSearchService;
import com.ccservice.b2b2c.base.service.ITrainService;
import com.ccservice.b2b2c.base.service.ITripService;
import com.ccservice.b2b2c.base.service.IVIPService;
import com.ccservice.b2b2c.base.service.IVisaService;
import com.ccservice.hotelorderinterface.IHotelOrderInterface;
import com.ccservice.service.IInterRateService;
import com.ccservice.service.IQTrainService;
import com.ccservice.service.IRateService;

public class Server {

    private static Server server = null;

    private HessianProxyFactory factory = new HessianProxyFactory();

    private String urlinterface;

    private String ccserviceurl;

    // private String cnserviceurl = 
    // "http://www.alhk999.com/cn_service/service/";
    private String cnserviceurl;

    private String url;

    //    private String url = "http://localhost:9001/cn_service/service/";

    //    private String url = "http://120.26.100.206:39210/cn_service/service/";

    //    private String url = "http://121.199.25.199:39001/cn_service/service/";

    //    private String urlAtom = "http://121.199.25.199:39116/cn_interface/service/";
    private String urlAtom;

    private String inter;

    private String inter_yeeurl;

    private String customercd;

    private String authno;

    private String name;

    private String pwd;

    private String ElongMiddleUrl;

    private String ElongUserName;

    private String ElongAppKey;

    private String ElongSecretKey;

    private String TaoBaoServerUrl;

    private String TaoBaoAppKey;

    private String TaoBaoAppSecret;

    private String TaoBaoCallBackUrl;

    private String SuccessRedirectUrl;

    private String Travelsky_Address;

    private String Travelsky_OfficeID;

    private String Travelsky_UserID;

    private String Travelsky_Password;

    private Map<String, String> dateHashMap = new HashMap<String, String>();

    private String JLUrl;

    private Eaccount SunshineInsuranceEaccount;

    // 虚拟值，从0开始，用于取打码REP服务器
    private long DamaRepIdx;

    // 虚拟值，从0开始，用于顺序取REP服务器
    private long TrainRepIdx;

    // REP服务器，用于12306下单
    private List<RepServerBean> RepServers = new ArrayList<RepServerBean>();

    private List<Customeruser> customeruser12306account = new ArrayList<Customeruser>();

    private List<String> test_String = new ArrayList<String>();

    private String MQurl;

    private String MQusername;

    // 存放通过agentid查看接口用户name和key的map
    private Map<String, Map<String, String>> keyMap = new HashMap<String, Map<String, String>>();

    private Server() {
    }

    public IVIPService getVIPService() {
        try {
            return (IVIPService) factory.create(IVIPService.class, url + IVIPService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static Server getInstance() {
        if (server == null) {
            server = new Server();
        }
        return server;
    }

    public ITrainService getTrainService() {
        try {
            return (ITrainService) factory.create(ITrainService.class, url + ITrainService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IAirService getAirService() {
        try {
            return (IAirService) factory.create(IAirService.class, url + IAirService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IAppconfigService getAppconfigService() {
        try {
            return (IAppconfigService) factory.create(IAppconfigService.class,
                    url + IAppconfigService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IHotelOrderInterface getHotelOrderInterface() {
        try {
            return (IHotelOrderInterface) factory.create(IHotelOrderInterface.class, inter_yeeurl
                    + IHotelOrderInterface.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public ITrain12306Service getTrain12306Service() {
        try {
            return (ITrain12306Service) factory.create(ITrain12306Service.class,
                    urlAtom + ITrain12306Service.class.getSimpleName());
        }
        catch (Exception e) {
            return null;
        }
    }

    public IB2BAirticketService getB2BAirticketService() {
        try {
            return (IB2BAirticketService) factory.create(IB2BAirticketService.class,
                    url + IB2BAirticketService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IB2BSystemService getB2BSystemService() {
        try {
            return (IB2BSystemService) factory.create(IB2BSystemService.class,
                    url + IB2BSystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    public IMemberService getMemberService() {
        try {
            return (IMemberService) factory.create(IMemberService.class, url + IMemberService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IInterhotelService getInterHotelService() {
        try {
            return (IInterhotelService) factory.create(IInterhotelService.class,
                    url + IInterhotelService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public ISystemService getSystemService() {
        try {
            return (ISystemService) factory.create(ISystemService.class, url + ISystemService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IRateService getRateService() {

        try {
            return (IRateService) factory.create(IRateService.class, inter + IRateService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IServicecenterService getServiceCenter() {
        try {
            return (IServicecenterService) factory.create(IServicecenterService.class, url
                    + IServicecenterService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public com.ccservice.b2b2c.atom.service.ITicketSearchService getTicketSearchService() {
        try {
            return (com.ccservice.b2b2c.atom.service.ITicketSearchService) factory.create(
                    com.ccservice.b2b2c.atom.service.ITicketSearchService.class, urlAtom
                            + com.ccservice.b2b2c.atom.service.ITicketSearchService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    // 艾特伯接口调用
    public ITicketSearchService getTicketSearchService2() {
        try {
            return (ITicketSearchService) factory.create(ITicketSearchService.class, ccserviceurl
                    + ITicketSearchService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    // 51Book接口调用
    public ISearchFlightService getSearchFiveoneFlightService() {
        try {
            return (ISearchFlightService) factory.create(ISearchFlightService.class, cnserviceurl
                    + ISearchFlightService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IAtomService getAtomService() {
        try {
            return (IAtomService) factory.create(IAtomService.class, urlAtom + IAtomService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IInterticketService getInterticketService() {
        try {
            return (IInterticketService) factory.create(IInterticketService.class,
                    url + IInterticketService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 艺龙酒店
     * 
     * @return
     */
    public IELongHotelService getIELongHotelService() {
        try {
            factory.setOverloadEnabled(true);
            return (IELongHotelService) factory.create(IELongHotelService.class,
                    url + IELongHotelService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 商旅手伴
     * 
     * @return
     */
    public IBusinessMateService getBusinessMateService() {
        try {
            return (IBusinessMateService) factory.create(IBusinessMateService.class,
                    url + IBusinessMateService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 华闽酒店
     * 
     * @return
     */
    public IHMHotelService getIHMHotelService() {
        try {
            factory.setOverloadEnabled(true);
            return (IHMHotelService) factory.create(IHMHotelService.class,
                    urlinterface + IHMHotelService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 国内酒店
     * 
     * @return
     */
    public IHotelService getHotelService() {
        try {
            return (IHotelService) factory.create(IHotelService.class, url + IHotelService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public ICarService getCarService() {
        try {
            return (ICarService) factory.create(ICarService.class, url + ICarService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public ITripService getTripService() {
        try {
            return (ITripService) factory.create(ITripService.class, url + ITripService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IVisaService getVisaService() {
        try {
            return (IVisaService) factory.create(IVisaService.class, url + IVisaService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public IManGoHotelService getManGoHotelService() {
        try {
            return (IManGoHotelService) factory.create(IManGoHotelService.class,
                    urlAtom + IManGoHotelService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IYiHaiCarService getYiHaiCarService() {
        try {
            return (IYiHaiCarService) factory.create(IYiHaiCarService.class,
                    urlAtom + IYiHaiCarService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IVIPService getSmschargeService() {
        try {
            return (IVIPService) factory.create(IVIPService.class, url + IVIPService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public ISingleTripInService getSingletripService() {
        try {
            return (ISingleTripInService) factory.create(ISingleTripInService.class,
                    url + ISingleTripInService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 行程单
     * 
     * @return
     */

    public ISingleTripInService getSingleTripInService() {
        try {
            return (ISingleTripInService) factory.create(ISingleTripInService.class,
                    url + ISingleTripInService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IInterRateService getIInterRateService() {

        try {
            return (IInterRateService) factory.create(IInterRateService.class,
                    inter + IInterRateService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public IQTrainService getIQTrainService() {

        try {
            return (IQTrainService) factory.create(IQTrainService.class, inter + IQTrainService.class.getSimpleName());
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public String getUrlAtom() {
        return urlAtom;
    }

    public void setUrlAtom(String urlAtom) {
        this.urlAtom = urlAtom;
    }

    public String getCcserviceurl() {
        return ccserviceurl;
    }

    public void setCcserviceurl(String ccserviceurl) {
        this.ccserviceurl = ccserviceurl;
    }

    public static Server getServer() {
        return server;
    }

    public static void setServer(Server server) {
        Server.server = server;
    }

    public HessianProxyFactory getFactory() {
        return factory;
    }

    public void setFactory(HessianProxyFactory factory) {
        this.factory = factory;
    }

    public String getInter() {
        return inter;
    }

    public void setInter(String inter) {
        this.inter = inter;
    }

    public String getCnserviceurl() {
        return cnserviceurl;
    }

    public void setCnserviceurl(String cnserviceurl) {
        this.cnserviceurl = cnserviceurl;
    }

    public String getElongUserName() {
        return ElongUserName;
    }

    public void setElongUserName(String elongUserName) {
        this.ElongUserName = elongUserName;
    }

    public String getElongAppKey() {
        return ElongAppKey;
    }

    public void setElongAppKey(String elongAppKey) {
        this.ElongAppKey = elongAppKey;
    }

    public String getElongSecretKey() {
        return ElongSecretKey;
    }

    public void setElongSecretKey(String elongSecretKey) {
        this.ElongSecretKey = elongSecretKey;
    }

    public String getElongMiddleUrl() {
        return ElongMiddleUrl;
    }

    public void setElongMiddleUrl(String elongMiddleUrl) {
        this.ElongMiddleUrl = elongMiddleUrl;
    }

    public Map<String, String> getDateHashMap() {
        if (dateHashMap == null || dateHashMap.size() < 0) {
            dateHashMap = new HashMap<String, String>();
        }
        return dateHashMap;
    }

    /**
     * 只读，用于展示
     */
    public Map<String, String> getReadOnlyDateHashMap() {
        Map<String, String> Result = new HashMap<String, String>();
        Result.putAll(getDateHashMap());
        return Result;
    }

    public void setDateHashMap(Map<String, String> dateHashMap) {
        this.dateHashMap = dateHashMap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInter_yeeurl() {
        return inter_yeeurl;
    }

    public void setInter_yeeurl(String inter_yeeurl) {
        this.inter_yeeurl = inter_yeeurl;
    }

    public String getCustomercd() {
        return customercd;
    }

    public void setCustomercd(String customercd) {
        this.customercd = customercd;
    }

    public String getAuthno() {
        return authno;
    }

    public void setAuthno(String authno) {
        this.authno = authno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getTaoBaoServerUrl() {
        return TaoBaoServerUrl;
    }

    public void setTaoBaoServerUrl(String taoBaoServerUrl) {
        this.TaoBaoServerUrl = taoBaoServerUrl;
    }

    public String getTaoBaoAppKey() {
        return TaoBaoAppKey;
    }

    public void setTaoBaoAppKey(String taoBaoAppKey) {
        this.TaoBaoAppKey = taoBaoAppKey;
    }

    public String getTaoBaoAppSecret() {
        return TaoBaoAppSecret;
    }

    public void setTaoBaoAppSecret(String taoBaoAppSecret) {
        this.TaoBaoAppSecret = taoBaoAppSecret;
    }

    public String getTaoBaoCallBackUrl() {
        return TaoBaoCallBackUrl;
    }

    public void setTaoBaoCallBackUrl(String taoBaoCallBackUrl) {
        this.TaoBaoCallBackUrl = taoBaoCallBackUrl;
    }

    public String getSuccessRedirectUrl() {
        return SuccessRedirectUrl;
    }

    public void setSuccessRedirectUrl(String successRedirectUrl) {
        this.SuccessRedirectUrl = successRedirectUrl;
    }

    public String getJLUrl() {
        return JLUrl;
    }

    public void setJLUrl(String jLUrl) {
        this.JLUrl = jLUrl;
    }

    public String getTravelsky_Address() {
        return Travelsky_Address;
    }

    public void setTravelsky_Address(String travelsky_Address) {
        this.Travelsky_Address = travelsky_Address;
    }

    public String getTravelsky_OfficeID() {
        return Travelsky_OfficeID;
    }

    public void setTravelsky_OfficeID(String travelsky_OfficeID) {
        this.Travelsky_OfficeID = travelsky_OfficeID;
    }

    public String getTravelsky_UserID() {
        return Travelsky_UserID;
    }

    public void setTravelsky_UserID(String travelsky_UserID) {
        this.Travelsky_UserID = travelsky_UserID;
    }

    public String getTravelsky_Password() {
        return Travelsky_Password;
    }

    public void setTravelsky_Password(String travelsky_Password) {
        this.Travelsky_Password = travelsky_Password;
    }

    public Eaccount getSunshineInsuranceEaccount() {
        return SunshineInsuranceEaccount;
    }

    public void setSunshineInsuranceEaccount(Eaccount sunshineInsuranceEaccount) {
        SunshineInsuranceEaccount = sunshineInsuranceEaccount;
    }

    public List<RepServerBean> getRepServers() {
        return RepServers;
    }

    public void setRepServers(List<RepServerBean> repServers) {
        this.RepServers = repServers;
    }

    public List<Customeruser> getCustomeruser12306account() {
        return customeruser12306account;
    }

    public void setCustomeruser12306account(List<Customeruser> customeruser12306account) {
        this.customeruser12306account = customeruser12306account;
    }

    public String getMQurl() {
        return MQurl;
    }

    public void setMQurl(String mQurl) {
        MQurl = mQurl;
    }

    public String getMQusername() {
        return MQusername;
    }

    public void setMQusername(String mQusername) {
        MQusername = mQusername;
    }

    public List<String> getTest_String() {
        return test_String;
    }

    public void setTest_String(List<String> test_String) {
        this.test_String = test_String;
    }

    public long getTrainRepIdx() {
        return TrainRepIdx;
    }

    public void setTrainRepIdx(long trainRepIdx) {
        this.TrainRepIdx = trainRepIdx;
    }

    public long getDamaRepIdx() {
        return DamaRepIdx;
    }

    public void setDamaRepIdx(long damaRepIdx) {
        this.DamaRepIdx = damaRepIdx;
    }

    public Map<String, Map<String, String>> getKeyMap() {
        return keyMap;
    }

    public void setKeyMap(Map<String, Map<String, String>> keyMap) {
        this.keyMap = keyMap;
    }

}
