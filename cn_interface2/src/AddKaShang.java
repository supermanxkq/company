import com.ccservice.b2b2c.util.db.DBHelper;

public class AddKaShang {

    public static void main(String[] args) {
        String sql = null;
        //PhoneTest
        // insert into PhoneCardBusiness (Name,Remark,KaShangNo,Qq)values('SH000"+i+"','广州银宇帐号"+i+"',i,'284185885')
        //        卡商广州银宇新开账号：XH001-XH500，麻烦技术处理下。谢谢
        int sum = 501;
        for (int i = 201; i < sum; i++) {
            String name = "WXL";
            if (i < 10) {
                name += "000" + i;
                //                sql = "  insert into PhoneCardBusiness (Name,Remark,KaShangNo,Qq)values(' + "','广州银宇帐号"
                //                        + i + "'," + i + ",'284185885')";
            }
            else if (i > 9 && i <= 99) {
                name += "00" + i;
                //                sql = "  insert into PhoneCardBusiness (Name,Remark,KaShangNo,Qq)values('maoting00" + i + "','广州银宇帐号"
                //                        + i + "'," + i + ",'284185885')";
            }
            else if (i > 99 && i <= 999) {
                name += "0" + i;
                //                sql = "  insert into PhoneCardBusiness (Name,Remark,KaShangNo,Qq)values('maoting0" + i + "','广州银宇帐号"
                //                        + i + "'," + i + ",'284185885')";
            }
            else if (i > 999) {
                name += i;
                //                sql = "  insert into PhoneCardBusiness (Name,Remark,KaShangNo,Qq)values('maoting" + i + "','广州银宇帐号" + i
                //                        + "'," + i + ",'284185885')";
            }
            sql = "  insert into PhoneCardBusiness (Name,Remark,KaShangNo,Qq)values('" + name + "','广州银宇帐号-" + i + "',"
                    + i + ",'284185885')";
            System.out.println(sql);
            DBHelper.insertSql(sql);
            //            System.out.println(i);
        }
        /*       String qq = "1360008151";
               String qqx = "广州银宇工作室";
               for (int i = 17880; i < sum; i++) {
                   sql = "DELETE FROM PhoneCardBusiness WHERE PKId = " + i + "";
                   DBHelper.insertSql(sql);
                    System.out.println(i);
               }*/

    }
}
