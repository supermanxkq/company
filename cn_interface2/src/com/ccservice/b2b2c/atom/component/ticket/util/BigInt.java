package com.ccservice.b2b2c.atom.component.ticket.util;

public class BigInt {
    Integer[] digits = new Integer[130];

    boolean isNeg = false;

    public Integer[] getDigits() {
        return digits;
    }

    public void setDigits(Integer[] digits) {
        this.digits = digits;
    }

    public boolean isNeg() {
        return isNeg;
    }

    public void setNeg(boolean isNeg) {
        this.isNeg = isNeg;
    }

}
