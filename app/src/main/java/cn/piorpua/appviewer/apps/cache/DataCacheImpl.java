package cn.piorpua.appviewer.apps.cache;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.piorpua.appviewer.apps.data.AppModel;

/**
 * Author: piorpua<helloworld.hnu@gmail.com>
 * Date: 16/6/14
 */
final class DataCacheImpl implements DataCache {

    private final Map<String, AppModel> mCache;

    public DataCacheImpl() {
        mCache = new HashMap<String, AppModel>();
    }

    @Override
    public boolean isEmpty() {
        return mCache.isEmpty();
    }

    @Override
    public AppModel update(AppModel value) {
        if (!isValueValid(value)) {
            return null;
        }

        mCache.put(getKey(value), value);
        return value;
    }

    @Override
    public List<AppModel> update(List<AppModel> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        List<AppModel> list = new ArrayList<AppModel>();
        for (AppModel value : values) {
            if (!isValueValid(value)) {
                continue;
            }

            String key = getKey(value);
            if (mCache.containsKey(key)) {
                list.remove(mCache.get(key));
            }

            mCache.put(getKey(value), value);
            list.add(value);
        }

        return list;
    }

    @Override
    public AppModel remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        return mCache.remove(key);
    }

    @Override
    public void clear() {
        mCache.clear();
    }

    @Override
    public List<AppModel> get() {
        Collection<AppModel> values = mCache.values();
        if (values == null || values.isEmpty()) {
            return null;
        }

        List<AppModel> list = new ArrayList<AppModel>();
        for (AppModel model : values) {
            list.add(model);
        }
        return list;
    }

    private boolean isValueValid(AppModel value) {
        if (value == null) {
            return false;
        }

        return !TextUtils.isEmpty(getKey(value));
    }

    private String getKey(@NonNull AppModel value) {
        return value.getPkgName();
    }
}
