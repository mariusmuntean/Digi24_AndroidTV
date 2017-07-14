package de.marius.android.digi24_androidtv;

import android.view.Window;

/**
 * Created by marius on 14.07.17.
 */

public class WindowAdjusterFactory {

    public WindowAdjuster getWindowAdjuster(Window window){
        return new WindowAdjusterImp(window);
    }
}
