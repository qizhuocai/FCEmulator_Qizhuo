package com.qizhuo.framework.ui.preferences;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.qizhuo.framework.GfxProfile;
import com.qizhuo.framework.R;
import com.qizhuo.framework.SlotInfo;
import com.qizhuo.framework.base.EmulatorUtils;
import com.qizhuo.framework.base.GameMenu;
import com.qizhuo.framework.base.GameMenu.GameMenuItem;
import com.qizhuo.framework.base.GameMenu.OnGameMenuListener;
import com.qizhuo.framework.base.SlotUtils;
import com.qizhuo.framework.ui.gamegallery.GameDescription;
import com.qizhuo.framework.ui.multitouchbutton.MultitouchLayer;
import com.qizhuo.framework.ui.multitouchbutton.MultitouchLayer.EDIT_MODE;
import com.qizhuo.framework.utils.DatabaseHelper;

public class TouchControllerSettingsActivity extends AppCompatActivity implements
        OnGameMenuListener {

    MultitouchLayer mtLayer;
    String gameHash = "";
    DatabaseHelper dbHelper;
    Bitmap lastGameScreenshot;
    private GameMenu gameMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controler_layout);
        gameMenu = new GameMenu(this, this);
        mtLayer = findViewById(R.id.touch_layer);
        dbHelper = new DatabaseHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mtLayer.setEditMode(EDIT_MODE.TOUCH);
        GameDescription games = dbHelper.selectObjFromDb(GameDescription.class,
                "where lastGameTime!=0 ORDER BY lastGameTime DESC LIMIT 1");
        GfxProfile gfxProfile;
        lastGameScreenshot = null;

        if (games != null) {
            SlotInfo info = SlotUtils.getSlot(EmulatorUtils.getBaseDir(this),
                    games.checksum, 0);
            lastGameScreenshot = info.screenShot;
        }

        gfxProfile = PreferenceUtil.getLastGfxProfile(this);
        mtLayer.setLastgameScreenshot(lastGameScreenshot,
                gfxProfile == null ? null : gfxProfile.name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mtLayer.saveEditElements();
        mtLayer.stopEditMode();

        if (lastGameScreenshot != null) {
            lastGameScreenshot.recycle();
            lastGameScreenshot = null;
        }
    }


    @Override
    public void onGameMenuCreate(GameMenu menu) {
        menu.add(R.string.act_tcs_reset, R.drawable.ic_restart);
    }

    @Override
    public void onGameMenuPrepare(GameMenu menu) {
    }

    @Override
    public void onGameMenuOpened(GameMenu menu) {
    }

    @Override
    public void onGameMenuClosed(GameMenu menu) {
    }

    @Override
    public void onGameMenuItemSelected(GameMenu menu, GameMenuItem item) {
        runOnUiThread(() -> mtLayer.resetEditElement(gameHash));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            openGameMenu();
            return true;

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void openGameMenu() {
        gameMenu.open();
    }

    @Override
    public void openOptionsMenu() {
        gameMenu.open();
    }

}
