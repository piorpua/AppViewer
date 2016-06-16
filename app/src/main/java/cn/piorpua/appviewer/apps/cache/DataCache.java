package cn.piorpua.appviewer.apps.cache;

import java.util.List;

import cn.piorpua.appviewer.apps.data.AppModel;

/**
 * Author: piorpua<helloworld.hnu@gmail.com>
 * Date: 16/6/14
 */
interface DataCache {

    boolean isEmpty();

    AppModel update(AppModel value);
    List<AppModel> update(List<AppModel> values);

    AppModel remove(String key);

    void clear();

    List<AppModel> get();
}
