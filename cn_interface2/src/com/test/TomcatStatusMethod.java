package com.test;

import java.util.Date;

public class TomcatStatusMethod {
    private String tomcatUrl;

    private String tomcatName;

    private boolean tomcatStatus;

    private Date errTimeDate;

    public TomcatStatusMethod(String tomcatUrl, String tomcatName) {
        this.tomcatUrl = tomcatUrl;
        this.tomcatName = tomcatName;
        this.tomcatStatus = true;
    }

    public String getTomcatUrl() {
        return tomcatUrl;
    }

    public void setTomcatUrl(String tomcatUrl) {
        this.tomcatUrl = tomcatUrl;
    }

    public String getTomcatName() {
        return tomcatName;
    }

    public void setTomcatName(String tomcatName) {
        this.tomcatName = tomcatName;
    }

    public boolean isTomcatStatus() {
        return tomcatStatus;
    }

    public void setTomcatStatus(boolean tomcatStatus) {
        this.tomcatStatus = tomcatStatus;
    }

    public Date getErrTimeDate() {
        return errTimeDate;
    }

    public void setErrTimeDate(Date errTimeDate) {
        this.errTimeDate = errTimeDate;
    }

}
