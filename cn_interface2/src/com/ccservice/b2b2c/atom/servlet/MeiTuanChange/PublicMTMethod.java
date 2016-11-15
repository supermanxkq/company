package com.ccservice.b2b2c.atom.servlet.MeiTuanChange;

public class PublicMTMethod {
    
  //转成美团code
    public static String gethtname(String changecode,String zname,String seatno){
        String result="";
        if("软卧".equals(zname)||"高级软卧".equals(zname)||"硬卧".equals(zname)){
            if(seatno.contains("上")){
                zname+="上";
            }else if(seatno.contains("中")){
                zname+="中";
            }else if(seatno.contains("下")){
                zname+="下";
            }
        }
        if("9".equals(changecode)){
            result=getHTcode(zname);
            return result;
        }else if("P".equals(changecode)){
            result=getHTcode(zname);
            return result;
        }else if("M".equals(changecode)){
            result=getHTcode(zname);
            return result;
        }else if("O".equals(changecode)){
            if(zname.contains("代")){
                zname="二等座"; 
            }else if("无座".equals(seatno)){
                zname="无座";
            }
            result=getHTcode(zname);
            return result;
        }else if("6".equals(changecode)){
            result=getHTcode(zname);
            return result;
        }else if("4".equals(changecode)){
            result=getHTcode(zname);
            return result;
        }else if("3".equals(changecode)){
            result=getHTcode(zname);
            return result;
        }else if("2".equals(changecode)){
            if(zname.contains("代")){
                zname="软座"; 
            }
            result=getHTcode(zname);
            return result;
        }else if("1".equals(changecode)){
            if(zname.contains("代")){
                zname="硬座"; 
            }else if("无座".equals(seatno)){
                zname="无座";
            }
            result=getHTcode(zname);
            return result;
        }
        return result;
    }
    
    //美团code转ht
    public static String getMTcode(String changecode){
        String result="";
        if("9".equals(changecode)){
//          9:商务座，   9 
            result="9";
            return result;
        }else if("10".equals(changecode)||"11".equals(changecode)||"12".equals(changecode)){
//          P:特等座，   12 特等座     10 观光座     11  一等包座
            result="P";
            return result;
        }else if("13".equals(changecode)){
//          M:一等座，     13
            result="M";
            return result;
        }else if("14".equals(changecode)){
//          O:二等座，    14
            result="O";
            return result;
        }else if("15".equals(changecode)||"16".equals(changecode)||"18".equals(changecode)||"21".equals(changecode)){
//          6:高级软卧，  15 高级软卧上     16 高级软卧下     18  一人软包     21 高级动卧
            result="6";
            return result;
        }else if("6".equals(changecode)||"7".equals(changecode)||"8".equals(changecode)||"20".equals(changecode)){
//          4:软卧，    6 软卧上  7 软卧中    8 软卧下   20  动卧
            result="4";
            return result;
        }else if("2".equals(changecode)||"3".equals(changecode)||"4".equals(changecode)||"22".equals(changecode)){
//          3:硬卧，    2  硬卧上     3 硬卧中     4硬卧下       22  包厢硬卧
            result="3";
            return result;
        }else if("5".equals(changecode)){
//          2:软座，    5  软座
            result="2";
            return result;
        }else if("1".equals(changecode)||"17".equals(changecode)){
//          1:硬座    1     17  无座
            result="1";
            return result;
        }
        return result;
    }
    
    //美团code转ht     1成人    2学生    3儿童  
    public static String getMTpiaotypecode(String piaotype){
        String result="";
        if("1".equals(piaotype)){
            result="1";
            return result;
        }else if("2".equals(piaotype)){
            result="3";
            return result;
        }else if("3".equals(piaotype)){
            result="2";
            return result;
        }
        return result;
    }
    
    
    public static String getHTcode(String zname){
        String result="";
        if("商务座".equals(zname)){
            result="9";
            return result;
        }else if("特等座".equals(zname)||"11".equals(zname)||"12".equals(zname)){
            result="12";
            return result;
        }else if("观光座".equals(zname)){
            result="10";
            return result;
        }else if("一等包座".equals(zname)){
            result="11";
            return result;
        }else if("一等座".equals(zname)){
            result="13";
            return result;
        }else if("二等座".equals(zname)){
            result="14";
            return result;
        }else if("高级软卧上".equals(zname)){
            result="15";
            return result;
        }else if("高级软卧下".equals(zname)){
            result="16";
            return result;
        }else if("一人软包".equals(zname)){
            result="18";
            return result;
        }else if("高级动卧".equals(zname)){
            result="21";
            return result;
        }else if("软卧上".equals(zname)){
            result="6";
            return result;
        }else if("软卧中".equals(zname)){
            result="7";
            return result;
        }else if("软卧下".equals(zname)){
            result="8";
            return result;
        }else if("动卧".equals(zname)){
            result="20";
            return result;
        }else if("硬卧上".equals(zname)){
            result="2";
            return result;
        }else if("硬卧中".equals(zname)){
            result="3";
            return result;
        }else if("硬卧下".equals(zname)){
            result="4";
            return result;
        }else if("包厢硬卧".equals(zname)){
            result="22";
            return result;
        }else if("软座".equals(zname)){
            result="5";
            return result;
        }else if("硬座".equals(zname)){
            result="1";
            return result;
        }else if("无座".equals(zname)){
            result="17";
            return result;
        }
        return result;
    }
}
