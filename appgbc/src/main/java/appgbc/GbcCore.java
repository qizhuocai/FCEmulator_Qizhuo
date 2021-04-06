package appgbc;

import com.qizhuo.framework.base.JniBridge;

public class GbcCore extends JniBridge {
    private static GbcCore instance = new GbcCore();

    static {
        System.loadLibrary("gbc");
    }

    private GbcCore() {
    }

    public static GbcCore getInstance() {
        return instance;
    }
}
