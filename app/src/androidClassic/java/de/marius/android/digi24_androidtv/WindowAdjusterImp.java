package de.marius.android.digi24_androidtv;

import android.view.View;
import android.view.Window;

/**
 * Created by marius on 14.07.17.
 */

public class WindowAdjusterImp implements WindowAdjuster {


    private Window window;

    public WindowAdjusterImp(Window window) {
        this.window = window;
    }

    @Override
    public void enableFullscreen() {
        this.window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
