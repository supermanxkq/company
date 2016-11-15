package com.ccservice.b2b2c.atom.component.ticket.api;

public class DaMaCommon {
    public static final int UUYUN = 0; //优优云

    public static final int RUOKUAI = 1;//若快

    public static final int QUNAR = 2;//去哪儿

    public static final int UUYUNLINUX = 3;

    public static final int HTHYCODE = 4;//航天华有

    public static final int DAMA2 = 5;//打码兔

    public static final int EASY = 6;//简单答

    public static final int TaoBao = 7;//淘宝

    public static final int HTHYCODE_DLL = 8;//hthydll 打码

    private int tpye; //0:优优云,1若快

    private String id;//

    private String result;

    private String msg;

    private boolean regDama;//注册打码

    public DaMaCommon() {
        super();
    }

    /**
     * 
     * @param tpye 0:优优云,1若快 ,2qunar,3优优云linux,4航天华有
     * @param id
     * @param result
     */
    public DaMaCommon(int tpye, String id, String result) {
        super();
        this.tpye = tpye;
        this.id = id;
        this.result = result;
    }

    public int getTpye() {
        return tpye;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTpye(int tpye) {
        this.tpye = tpye;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "DaMaCommon [tpye=" + tpye + ", id=" + id + ", result=" + result + ", msg=" + msg + "]";
    }

    public boolean getRegDama() {
        return regDama;
    }

    public void setRegDama(boolean regDama) {
        this.regDama = regDama;
    }

}
