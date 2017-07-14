package de.marius.android.digi24_androidtv;

import android.view.Window;

/**
 * Created by marius on 14.07.17.
 */

class WindowAdjusterImpl implements WindowAdjuster {
    public WindowAdjusterImpl(Window window) {
    }

    @Override
    public void enableFullscreen() {
        // Do nothing on Android TV
    }
}
