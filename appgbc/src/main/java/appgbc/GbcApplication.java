package appgbc;

import com.qizhuo.framework.BaseApplication;
import com.qizhuo.framework.base.EmulatorHolder;


public class GbcApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        EmulatorHolder.setEmulatorClass(GbcEmulator.class);
    }

    @Override
    public boolean hasGameMenu() {
        return false;
    }

}