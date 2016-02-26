package cn.piorpua.appviewer;

import cn.piorpua.android.utils.BaseSharedPreference;

public class MainPreference extends BaseSharedPreference {

    private static final String TAG = MainPreference.class.getSimpleName();

    private static final String KEY_SHORTCUT_CREATED = "key_shortcut_created";

    private static MainPreference sIns;

    public static MainPreference getIns() {
        if (sIns == null) {
            synchronized (MainPreference.class) {
                if (sIns == null) {
                    sIns = new MainPreference();
                }
            }
        }
        return sIns;
    }

    public boolean isShortcurCreated() {
        return getBoolean(KEY_SHORTCUT_CREATED);
    }

    public void setShortcutCreated(boolean flag) {
        putBoolean(KEY_SHORTCUT_CREATED, flag);
    }

    @Override
    protected String getPreferenceName() {
        return TAG;
    }

    private MainPreference() {
        super(MainApplication.getApplication());
    }

}
