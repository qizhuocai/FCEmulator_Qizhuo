package com.qizhuo.fcnes;

import com.qizhuo.framework.BaseApplication;
import com.qizhuo.framework.base.EmulatorHolder;

public class NesApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        EmulatorHolder.setEmulatorClass(NesEmulator.class);
    }

    @Override
    public boolean hasGameMenu() {
        return true;
    }
}
