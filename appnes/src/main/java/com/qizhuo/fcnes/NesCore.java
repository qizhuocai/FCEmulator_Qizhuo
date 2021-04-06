package com.qizhuo.fcnes;

import com.qizhuo.framework.base.JniBridge;

public class NesCore extends JniBridge {
    private static NesCore instance = new NesCore();

    static {
        System.loadLibrary("nes");
    }

    private NesCore() {
    }

    public static NesCore getInstance() {
        return instance;
    }

}
