package cn.piorpua.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public abstract class BaseSharedPreference {

    protected final SharedPreferences mPreference;

    protected BaseSharedPreference(Context ctx) {
        mPreference = ctx.getSharedPreferences(
                getPreferenceName(), getPreferenceMode());
    }

    protected abstract String getPreferenceName();

    protected int getPreferenceMode() {
        return Context.MODE_PRIVATE;
    }

    protected final boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    protected final boolean getBoolean(String key) {
        return mPreference.getBoolean(key, false);
    }

    protected final boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    protected final int getInt(String key) {
        return mPreference.getInt(key, 0);
    }

    protected final boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    protected final long getLong(String key) {
        return mPreference.getLong(key, 0L);
    }

    protected final boolean putFloat(String key, float value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    protected final float getFloat(String key) {
        return mPreference.getFloat(key, 0.0f);
    }

    protected final boolean putString(String key, String value) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    protected final String getString(String key) {
        return mPreference.getString(key, null);
    }

    protected final boolean putStringSet(String key, Set<String> values) {
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putStringSet(key, values);
        return editor.commit();
    }

    protected final Set<String> getStringSet(String key) {
        return mPreference.getStringSet(key, null);
    }

}
