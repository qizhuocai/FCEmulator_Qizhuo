package appgbc;

import java.util.HashSet;
import java.util.Set;

import com.qizhuo.framework.Emulator;
import com.qizhuo.framework.base.EmulatorActivity;
import com.qizhuo.framework.ui.gamegallery.GalleryActivity;

public class GbcGalleryActivity extends GalleryActivity {

    @Override
    public Class<? extends EmulatorActivity> getEmulatorActivityClass() {
        return GbcEmulatorActivity.class;
    }

    @Override
    protected Set<String> getRomExtensions() {
        HashSet<String> set = new HashSet<>();
        set.add("gb");
        set.add("gbc");
        return set;
    }

    @Override
    public Emulator getEmulatorInstance() {
        return GbcEmulator.getInstance();
    }

}
