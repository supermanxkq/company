package TrainInterfaceMethod;

public class TrainInterfaceMethod {
    public final static int HTHY = 1;

    public final static int QUNAR = 2;

    public final static int TONGCHENG = 3;

    //代扣-->出票流程
    public final static int WITHHOLDING_BEFORE = 4;

    //出票-->代扣流程
    public final static int WITHHOLDING_AFTER = 5;

    public final static int TAOBAO = 6;

    //美团流程
    public final static int MEITUAN = 7;

    //途牛约票
    public final static int TRAIN_BESPEAKTICKET = 8;

    /**
     * YILONG1   先占座模式[艺龙]
     */
    public final static int YILONG1 = 9;

    /**
     * YILONG2   先支付模式就是黄牛模式[艺龙]
     */
    public final static int YILONG2 = 10;

    //美团约票
    public final static int TRAIN_BESPEAKTICKET_MEITUAN = 11;

    //qunar约票
    public static final int TRAINORDERBESPEAK_INTERFACETYPE_QUNAR = 12;
}
