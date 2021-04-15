package com.qizhuo.framework.ui.preferences;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import java.util.List;

import com.qizhuo.framework.GfxProfile;
import com.qizhuo.framework.R;
import com.qizhuo.framework.base.EmulatorHolder;
import com.qizhuo.framework.gamedata.dao.entity.GameEntity;


public class GamePreferenceActivity extends AppCompatPreferenceActivity {

    public static final String EXTRA_GAME = "EXTRA_GAME";
    private GameEntity game;

    static void initZapper(Preference zapper, PreferenceCategory zapperCategory) {
        if (!EmulatorHolder.getInfo().hasZapper()) {
            zapperCategory.removePreference(zapper);
        }
    }

    static void initVideoPreference(ListPreference preference,
                                    PreferenceCategory category, PreferenceScreen screen) {
        List<GfxProfile> profiles = EmulatorHolder.getInfo().getAvailableGfxProfiles();

    }

        @Override
    protected boolean isValidFragment(String fragmentName) {

                return true;


    }
//    @Override
//    public boolean isValidFragment(String fragmentName)  {
//        return GamePreferenceActivity.class.getName().equals(fragmentName);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        game = (GameEntity) getIntent().getSerializableExtra(EXTRA_GAME);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.game_preferences_header, target);
    }

}
