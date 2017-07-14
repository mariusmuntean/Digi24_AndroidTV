package de.marius.android.digi24_androidtv;

import android.view.Window;

/**
 * Created by marius on 14.07.17.
 */

class WindowAdjusterFactory {
    public WindowAdjuster getWindowAdjuster(Window window) {
        return new WindowAdjusterImpl(window);
    }
}
