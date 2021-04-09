package com.qizhuo.framework;


import android.app.Application;

import com.blankj.utilcode.util.Utils;

import com.qizhuo.framework.utils.EmuUtils;
import com.qizhuo.framework.utils.NLog;

abstract public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        boolean debug = EmuUtils.isDebuggable(this);
        NLog.setDebugMode(debug);
    }

    public abstract boolean hasGameMenu();
}
