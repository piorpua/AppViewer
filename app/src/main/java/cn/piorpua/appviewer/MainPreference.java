package cn.piorpua.appviewer;

import cn.piorpua.android.utils.BaseSharedPreference;

public class MainPreference extends BaseSharedPreference {

    private static final String TAG = MainPreference.class.getSimpleName();

    private static final String KEY_SHORTCUT_CREATED = "key_shortcut_created";

    private static final String KEY_DLG_CFG_SORT_ORDER = "key_dlg_cfg_sort_order";
    private static final String KEY_DLG_CFG_SORT_TYPE = "key_dlg_cfg_sort_type";
    private static final String KEY_DLG_CFG_SYS_FILTER = "key_dlg_cfg_sys_filter";

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
    public void setShortcutCreated(boolean value) {
        putBoolean(KEY_SHORTCUT_CREATED, value);
    }

    public String getDialogConfigSortOrder() {
        return getString(KEY_DLG_CFG_SORT_ORDER);
    }
    public void setDialogConfigSortOrder(String value) {
        putString(KEY_DLG_CFG_SORT_ORDER, value);
    }

    public String getDialogConfigSortType() {
        return getString(KEY_DLG_CFG_SORT_TYPE);
    }
    public void setDialogConfigSortType(String value) {
        putString(KEY_DLG_CFG_SORT_TYPE, value);
    }

    public boolean isDialogConfigSystemFilter() {
        return getBoolean(KEY_DLG_CFG_SYS_FILTER);
    }
    public void setDialogConfigSystemFilter(boolean value) {
        putBoolean(KEY_DLG_CFG_SYS_FILTER, value);
    }

    @Override
    protected String getPreferenceName() {
        return TAG;
    }

    private MainPreference() {
        super(MainApplication.getApplication());
    }

}
