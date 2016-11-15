package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

public class Passengerinfo {
    String name;

    String idnumber;

    public Passengerinfo(String name, String idnumber) {
        super();
        this.name = name;
        this.idnumber = idnumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdnumber() {
        return idnumber;
    }

    public void setIdnumber(String idnumber) {
        this.idnumber = idnumber;
    }

}
