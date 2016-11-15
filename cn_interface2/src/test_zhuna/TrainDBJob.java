package test_zhuna;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.traininterface.train_cancel_changeServlet;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.b2b2c.util.db.NewDBHelper;
import com.ccservice.elong.inter.PropertyUtil;

public class TrainDBJob implements Job {
    private static int endId = 0;

    private static int tableCount = 0;

    public static void main(String[] args) {
        while (true) {
            int count = getEndTimeByid();
            execute();
            if (tableCount == 0) {
                System.out.println("更新完毕");
                break;
            }
        }
    }

    public static void execute() {
        String sql = "select top 100 * from t_customeruser with(nolock) where c_type=4 and C_ISENABLE = 12  and id > "
                + endId + " order by id";
        DataTable dataTables = DBHelper.GetDataTable(sql, null);
        tableCount = dataTables.GetRow().size();
        for (int i = 0; i < dataTables.GetRow().size(); i++) {
            DataRow dataRow = dataTables.GetRow().get(i);
            String C_ID = dataRow.GetColumnString("ID");
            String C_CARDNUMBER = dataRow.GetColumnString("C_CARDNUMBER");
            String C_CARDPASSWORD = dataRow.GetColumnString("C_CARDPASSWORD");
            String C_LOGINNAME = dataRow.GetColumnString("C_LOGINNAME");
            String C_LOGPASSWORD = dataRow.GetColumnString("C_LOGPASSWORD");
            String C_MEMBERNAME = dataRow.GetColumnString("C_MEMBERNAME");
            String C_MEMBERSEX = dataRow.GetColumnString("C_MEMBERSEX");
            String C_MEMBEREMAIL = dataRow.GetColumnString("C_MEMBEREMAIL");
            String C_MOBILE = dataRow.GetColumnString("C_MOBILE");
            String C_STATE = dataRow.GetColumnString("C_STATE");
            String C_TYPE = dataRow.GetColumnString("C_TYPE");
            String C_ISADMIN = dataRow.GetColumnString("C_ISADMIN");
            String C_BIRTHDAY = dataRow.GetColumnString("C_BIRTHDAY");
            String C_LOCALCITY = dataRow.GetColumnString("C_LOCALCITY");
            String C_MEMBERFAX = dataRow.GetColumnString("C_MEMBERFAX");
            String C_MEMBERDESC = dataRow.GetColumnString("C_MEMBERDESC");
            String C_ISWEB = dataRow.GetColumnString("C_ISWEB");
            String C_MEMBERMOBILE = dataRow.GetColumnString("C_MEMBERMOBILE");
            String C_ISENABLE = dataRow.GetColumnString("C_ISENABLE");
            String C_MEMBERTYPE = dataRow.GetColumnString("C_MEMBERTYPE");
            String C_AGENTID = dataRow.GetColumnString("C_AGENTID");
            String C_MODIFYTIME = dataRow.GetColumnString("C_MODIFYTIME");
            String C_MODIFYUSER = dataRow.GetColumnString("C_MODIFYUSER");
            String C_CREATETIME = dataRow.GetColumnString("C_CREATETIME");
            String C_CREATEUSER = dataRow.GetColumnString("C_CREATEUSER");
            String C_DEPTID = dataRow.GetColumnString("C_DEPTID");
            String C_ISMANAGER = dataRow.GetColumnString("C_ISMANAGER");
            String C_CARDTYPE = dataRow.GetColumnString("C_CARDTYPE");
            String C_CARDNUNBER = dataRow.GetColumnString("C_CARDNUNBER");
            String C_WORKPHONE = dataRow.GetColumnString("C_WORKPHONE");
            String C_LINKOTHER = dataRow.GetColumnString("C_LINKOTHER");
            String C_DESCRIPTION = dataRow.GetColumnString("C_DESCRIPTION");
            String C_ENNAME = dataRow.GetColumnString("C_ENNAME");
            String C_ENTRYTIME = dataRow.GetColumnString("C_ENTRYTIME");
            String C_LIVINGCARDTYPE = dataRow.GetColumnString("C_LIVINGCARDTYPE");
            String C_LIVINGCARDNUM = dataRow.GetColumnString("C_LIVINGCARDNUM");
            String C_LIVINGPERIOD = dataRow.GetColumnString("C_LIVINGPERIOD");
            String C_WORKNUMBER = dataRow.GetColumnString("C_WORKNUMBER");
            String C_WORKPERIOD = dataRow.GetColumnString("C_WORKPERIOD");
            String C_CHINAADDRESS = dataRow.GetColumnString("C_CHINAADDRESS");
            String C_NATIONALITY = dataRow.GetColumnString("C_NATIONALITY");
            String C_TOTALSCORE = dataRow.GetColumnString("C_TOTALSCORE");
            String C_PROFITS = dataRow.GetColumnString("C_PROFITS");
            String C_POSTALCODE = dataRow.GetColumnString("C_POSTALCODE");
            String C_LEVEL = dataRow.GetColumnString("C_LEVEL");
            String C_TICKETNUM = dataRow.GetColumnString("C_TICKETNUM");
            String C_LIMITTICKETNUM = dataRow.GetColumnString("C_LIMITTICKETNUM");
            String C_INTERTICKETNUM = dataRow.GetColumnString("C_INTERTICKETNUM");
            String C_LOGINNUM = dataRow.GetColumnString("C_LOGINNUM");
            String C_LIMITINTERTICKETNUM = dataRow.GetColumnString("C_LIMITINTERTICKETNUM");
            String C_TICKETDISCOUNT = dataRow.GetColumnString("C_TICKETDISCOUNT");
            String C_INTERTICKETDISCOUNT = dataRow.GetColumnString("C_INTERTICKETDISCOUNT");
            String C_TRAINNUM = dataRow.GetColumnString("C_TRAINNUM");
            String C_LIMITTRAINNUM = dataRow.GetColumnString("C_LIMITTRAINNUM");
            String C_CANCELTOTAL = dataRow.GetColumnString("C_CANCELTOTAL");
            StringBuffer prosql = new StringBuffer("exec ");
            prosql.append("[sp_TrainCustomerUser] ");
            prosql.append("@id='" + C_ID + "',");
            prosql.append("@C_CARDNUMBER='" + C_CARDNUMBER + "',");
            prosql.append("@C_CARDPASSWORD='" + C_CARDPASSWORD + "',");
            prosql.append("@C_LOGINNAME='" + C_LOGINNAME + "',");
            prosql.append("@C_LOGPASSWORD='" + C_LOGPASSWORD + "',");
            prosql.append("@C_MEMBERNAME='" + C_MEMBERNAME + "',");
            prosql.append("@C_MEMBERSEX='" + C_MEMBERSEX + "',");
            prosql.append("@C_MEMBEREMAIL='" + C_MEMBEREMAIL + "',");
            prosql.append("@C_MOBILE='" + C_MOBILE + "',");
            prosql.append("@C_STATE=" + ("".equals(C_STATE) ? 0 : C_STATE) + ",");
            prosql.append("@C_TYPE=" + ("".equals(C_TYPE) ? 0 : C_TYPE) + ",");
            prosql.append("@C_ISADMIN=" + ("".equals(C_ISADMIN) ? 0 : C_ISADMIN) + ",");
            prosql.append("@C_BIRTHDAY='" + C_BIRTHDAY + "',");
            prosql.append("@C_LOCALCITY='" + C_LOCALCITY + "',");
            prosql.append("@C_MEMBERFAX='" + C_MEMBERFAX + "',");
            prosql.append("@C_MEMBERDESC='" + C_MEMBERDESC + "',");
            prosql.append("@C_ISWEB=" + ("".equals(C_ISWEB) ? 0 : C_ISWEB) + ",");
            prosql.append("@C_MEMBERMOBILE='" + C_MEMBERMOBILE + "',");
            prosql.append("@C_ISENABLE=" + ("".equals(C_ISENABLE) ? 0 : C_ISENABLE) + ",");
            prosql.append("@C_MEMBERTYPE=" + ("".equals(C_MEMBERTYPE) ? 0 : C_MEMBERTYPE) + ",");
            prosql.append("@C_AGENTID=" + ("".equals(C_AGENTID) ? 0 : C_AGENTID) + ",");
            prosql.append("@C_MODIFYTIME='" + C_MODIFYTIME + "',");
            prosql.append("@C_MODIFYUSER='" + C_MODIFYUSER + "',");
            prosql.append("@C_CREATETIME='" + C_CREATETIME + "',");
            prosql.append("@C_CREATEUSER='" + C_CREATEUSER + "',");
            prosql.append("@C_DEPTID=" + ("".equals(C_DEPTID) ? 0 : C_DEPTID) + ",");
            prosql.append("@C_ISMANAGER=" + ("".equals(C_ISMANAGER) ? 0 : C_ISMANAGER) + ",");
            prosql.append("@C_CARDTYPE=" + ("".equals(C_CARDTYPE) ? 0 : C_CARDTYPE) + ",");
            prosql.append("@C_CARDNUNBER='" + C_CARDNUNBER + "',");
            prosql.append("@C_WORKPHONE='" + C_WORKPHONE + "',");
            prosql.append("@C_LINKOTHER='" + C_LINKOTHER + "',");
            prosql.append("@C_DESCRIPTION='" + C_DESCRIPTION + "',");
            prosql.append("@C_ENNAME='" + C_ENNAME + "',");
            prosql.append("@C_ENTRYTIME='" + C_ENTRYTIME + "',");
            prosql.append("@C_LIVINGCARDTYPE='" + C_LIVINGCARDTYPE + "',");
            prosql.append("@C_LIVINGCARDNUM='" + C_LIVINGCARDNUM + "',");
            prosql.append("@C_LIVINGPERIOD='" + C_LIVINGPERIOD + "',");
            prosql.append("@C_WORKNUMBER='" + C_WORKNUMBER + "',");
            prosql.append("@C_WORKPERIOD='" + C_WORKPERIOD + "',");
            prosql.append("@C_CHINAADDRESS='" + C_CHINAADDRESS + "',");
            prosql.append("@C_NATIONALITY='" + C_NATIONALITY + "',");
            prosql.append("@C_TOTALSCORE='" + ("".equals(C_TOTALSCORE) ? 0 : C_TOTALSCORE) + "',");
            prosql.append("@C_PROFITS='" + ("".equals(C_PROFITS) ? 0 : C_PROFITS) + "',");
            prosql.append("@C_POSTALCODE='" + C_POSTALCODE + "',");
            prosql.append("@C_LEVEL=" + ("".equals(C_LEVEL) ? 0 : C_LEVEL) + ",");
            prosql.append("@C_TICKETNUM=" + ("".equals(C_TICKETNUM) ? 0 : C_TICKETNUM) + ",");
            prosql.append("@C_LIMITTICKETNUM=" + ("".equals(C_LIMITTICKETNUM) ? 0 : C_LIMITTICKETNUM) + ",");
            prosql.append("@C_INTERTICKETNUM=" + ("".equals(C_INTERTICKETNUM) ? 0 : C_INTERTICKETNUM) + ",");
            prosql.append("@C_LOGINNUM=" + ("".equals(C_LOGINNUM) ? 0 : C_LOGINNUM) + ",");
            prosql.append("@C_LIMITINTERTICKETNUM=" + ("".equals(C_LIMITINTERTICKETNUM) ? 0 : C_LIMITINTERTICKETNUM)
                    + ",");
            prosql.append("@C_TICKETDISCOUNT=" + ("".equals(C_TICKETDISCOUNT) ? 0 : C_TICKETDISCOUNT) + ",");
            prosql.append("@C_INTERTICKETDISCOUNT=" + ("".equals(C_INTERTICKETDISCOUNT) ? 0 : C_INTERTICKETDISCOUNT)
                    + ",");
            prosql.append("@C_TRAINNUM=" + ("".equals(C_TRAINNUM) ? 0 : C_TRAINNUM) + ",");
            prosql.append("@C_LIMITTRAINNUM=" + ("".equals(C_LIMITTRAINNUM) ? 0 : C_LIMITTRAINNUM) + ",");
            prosql.append("@C_CANCELTOTAL=" + ("".equals(C_CANCELTOTAL) ? 0 : C_CANCELTOTAL) + "");
            System.out.println(prosql.toString());
            boolean isResult = NewDBHelper.executeSql(prosql.toString());

            if (i == tableCount - 1) {
                System.out.println("-----本次完成最后id:" + C_ID + "-----");
            }
        }
    }

    /**
     * 1是开启
     * 0是关闭
     * 
     * @return
     * @time 2016年1月15日 下午4:13:44
     * @author Administrator
     */
    private static boolean getAnswer() {
        String sql = "select * from T_B2BSEQUENCE where C_NAME= 'CUSTOMERUSERJOBLOCK'";
        DataTable datatable = NewDBHelper.GetDataTable(sql, null);
        DataRow dataRow = datatable.GetRow().get(0);
        int value = dataRow.GetColumnInt("C_VALUE");
        if (value == 0) {
            return false;
        }
        return true;
    }

    private static int getEndTimeByCount() {
        String sql = "select * from T_B2BSEQUENCE where C_NAME='CUSTOMERUSERENDUPDATE' ";
        DataTable datatable = NewDBHelper.GetDataTable(sql, null);
        DataRow dataRow = datatable.GetRow().get(0);
        int endCount = dataRow.GetColumnInt("C_VALUE");
        return endCount;
    }

    private static void updateLock(int lock) {
        String sql = "update  T_B2BSEQUENCE set C_VALUE=" + lock + " where C_NAME='CUSTOMERUSERJOBLOCK' ";
        int isResult = NewDBHelper.UpdateData(sql);
    }

    private static int getEndTimeByid() {
        String sql = "select * from T_B2BSEQUENCE where C_NAME='Updateid' ";
        DataTable datatable = NewDBHelper.GetDataTable(sql, null);
        DataRow dataRow = datatable.GetRow().get(0);
        int endCount = dataRow.GetColumnInt("C_VALUE");
        return endCount;
    }

    private static void updateid(long Updateid) {
        String sql = "update  T_B2BSEQUENCE set C_VALUE=" + Updateid + " where C_NAME='Updateid' ";
        int isResult = NewDBHelper.UpdateData(sql);
    }

    private static void updateEndTimeByCount(Long count) {
        String sql = "update T_B2BSEQUENCE set C_VALUE=" + count + " where C_NAME='CUSTOMERUSERENDUPDATE'";
        int isResult = NewDBHelper.UpdateData(sql);
    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        // TODO Auto-generated method stub

    }

}
