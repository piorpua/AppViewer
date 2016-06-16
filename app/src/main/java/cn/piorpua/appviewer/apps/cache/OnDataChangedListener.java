package cn.piorpua.appviewer.apps.cache;

import java.util.List;

import cn.piorpua.appviewer.apps.data.AppModel;

/**
 * Author: piorpua<helloworld.hnu@gmail.com>
 * Date: 16/6/14
 */
public interface OnDataChangedListener {

    byte OP_CLEAR  = 1;
    byte OP_UPDATE = 2;
    byte OP_REMOVE = 3;

    void onDataChanged(byte opCode, AppModel value);

    void onBatchDataChanged(byte opCode, List<AppModel> values);
}
