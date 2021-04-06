package appgg;

import java.util.HashSet;
import java.util.Set;

import com.qizhuo.framework.Emulator;
import com.qizhuo.framework.base.EmulatorActivity;
import com.qizhuo.framework.ui.gamegallery.GalleryActivity;

public class GGGalleryActivity extends GalleryActivity {

    @Override
    public Emulator getEmulatorInstance() {
        return GGEmulator.getInstance();
    }

    @Override
    public Class<? extends EmulatorActivity> getEmulatorActivityClass() {
        return GGEmulatorActivity.class;
    }

    @Override
    protected Set<String> getRomExtensions() {
        HashSet<String> set = new HashSet<>();
        set.add("gg");
        return set;
    }
}
