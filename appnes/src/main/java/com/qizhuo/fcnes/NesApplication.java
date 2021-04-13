package com.qizhuo.fcnes;

import com.qizhuo.framework.BaseApplication;
import com.qizhuo.framework.base.EmulatorHolder;
import com.qizhuo.framework.utils.ZipUtil;

public class NesApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        EmulatorHolder.setEmulatorClass(NesEmulator.class);
//        try {
//            if (!ZipUtil.checkInit())
//            {
//                ZipUtil.Init(this);
//                try {
//                    if (ZipUtil.fileIsExists())
//                    {
//                        ZipUtil.deletefile();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean hasGameMenu() {
        return true;
    }
}
