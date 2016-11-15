package com.ccservice.b2b2c.atom.servlet.MeiTuanChange.Method;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;

public class GetReqTokenByResignId {

    public static void main(String[] args) {
        System.out.println(GetReqTokenByResignId.Method.CONFIRM_RESIGN);

    }

    public static String getChangeReqToken(Method method, long orderId, long resignId, String trainCode,
            String from_station_code, String to_station_code, String train_date, List<ChangeTicket> tickets) {
        String key = PropertyUtil.getValue("MeiTuan_key", "Train.properties");

        StringBuilder seed = new StringBuilder();

        seed.append(orderId).append("_").append(resignId);

        if (method != null) {

            seed.append("_").append(method.toString());
        }

        if (trainCode != null) {

            seed.append("_").append(trainCode);
        }

        if (from_station_code != null) {

            seed.append("_").append(from_station_code);
        }
        if (to_station_code != null) {

            seed.append("_").append(to_station_code);
        }
        if (train_date != null) {

            seed.append("|").append(train_date);
        }
        if (tickets != null) {

            seed.append("tickets:");
            Collections.sort(tickets, new Comparator<ChangeTicket>() {

                @Override
                public int compare(ChangeTicket o1, ChangeTicket o2) {

                    if (o1.getTicketId() > o2.getTicketId()) {

                        return 1;
                    }
                    else {

                        return -1;
                    }

                }
            });
            for (ChangeTicket ticket : tickets) {

                seed.append(ticket.getTicketId()).append(ticket.getCertificateNo());
            }
        }
        String reqToken = "";
        try {

            reqToken = ElongHotelInterfaceUtil.MD5(seed.toString() + ElongHotelInterfaceUtil.MD5(key));
        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return reqToken;
    }

    public enum Method {

        RESIGN_TICKET("resignTicket"), //

        CANCEL_RESIGN("cancelResign"), //

        CONFIRM_RESIGN("confirmResign");//

        private String method;

        Method(String method) {

            this.method = method;

        }

        public String toString() {

            return this.method.toString();

        }

    }

}
