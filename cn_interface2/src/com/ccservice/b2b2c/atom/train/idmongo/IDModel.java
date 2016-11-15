package com.ccservice.b2b2c.atom.train.idmongo;

public class IDModel {
    private long _idNumber;

    private String _realName;

    private int _idType;

    private String _supplyAccount;

    private int _ticketType;

    /**
     * 1: 身份证冒用新加的版本
     */
    private int _version;

    public IDModel() {
    }

    public IDModel(long idNumber, String realName, int idType, String supplyAccount) {
        _idNumber = idNumber;
        _realName = realName;
        _idType = idType;
        _supplyAccount = supplyAccount;
    }

    public IDModel(long idNumber, String realName, int idType, String supplyAccount, int ticketType) {
        _idNumber = idNumber;
        _realName = realName;
        _idType = idType;
        _supplyAccount = supplyAccount;
        _ticketType = ticketType;
    }

    public void SetIDNumber(long idNumber) {
        _idNumber = idNumber;
    }

    public void SetRealName(String realName) {
        _realName = realName;
    }

    public void SetIDType(int idType) {
        _idType = idType;
    }

    public void SetSupplyAccount(String supplyAccount) {
        _supplyAccount = supplyAccount;
    }

    public long GetIDNumber() {
        return _idNumber;
    }

    public String GetRealName() {
        return _realName;
    }

    public int GetIDType() {
        return _idType;
    }

    public String GetSupplyAccount() {
        return _supplyAccount;
    }

    public int get_ticketType() {
        return _ticketType;
    }

    public void set_ticketType(int _ticketType) {
        this._ticketType = _ticketType;
    }

    public int get_version() {
        return _version;
    }

    public void set_version(int _version) {
        this._version = _version;
    }

}
