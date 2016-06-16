package cn.piorpua.appviewer.apps.cache;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import cn.piorpua.appviewer.apps.data.AppModel;

/**
 * Author: piorpua<helloworld.hnu@gmail.com>
 * Date: 16/6/16
 */
public final class DataCacheManager {

    private static final String TAG = "DataCacheManager";

    public static interface OnDataLoadedListener {
        void onLoaded(List<AppModel> values);
    }

    private static DataCacheManager sIns;

    public static DataCacheManager getIns() {
        if (sIns == null) {
            synchronized (DataCacheManager.class) {
                if (sIns == null) {
                    sIns = new DataCacheManager();
                }
            }
        }
        return sIns;
    }

    private final class InnerHandler extends Handler {

        private static final byte MSG_ADD_LISTENER    = 1;
        private static final byte MSG_REMOVE_LISTENER = 2;

        private static final byte MSG_LOAD         = 3;
        private static final byte MSG_CLEAR        = 4;
        private static final byte MSG_UPDATE       = 5;
        private static final byte MSG_UPDATE_BATCH = 6;
        private static final byte MSG_REMOVE       = 7;

        public InnerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD_LISTENER: {
                    handleAddOnDataChangedListener(
                            (OnDataChangedListener) msg.obj);
                    break;
                }

                case MSG_REMOVE_LISTENER: {
                    handleRemoveOnDataChangedListener(
                            (OnDataChangedListener) msg.obj);
                    break;
                }

                case MSG_LOAD: {
                    handleLoad((OnDataLoadedListener) msg.obj);
                    break;
                }

                case MSG_CLEAR: {
                    handleClear();
                    break;
                }

                case MSG_UPDATE: {
                    handleUpdate((AppModel) msg.obj);
                    break;
                }

                case MSG_UPDATE_BATCH: {
                    handleUpdateBatch((List<AppModel>) msg.obj);
                    break;
                }

                case MSG_REMOVE: {
                    handleRemove((String) msg.obj);
                    break;
                }

                default: {
                    super.handleMessage(msg);
                    break;
                }
            }
        }
    }

    private HandlerThread mThread;
    private InnerHandler mHandler;

    private ReferenceQueue<OnDataChangedListener> mListenerRefQ;
    private List<WeakReference<OnDataChangedListener>> mListenerList;

    private DataCache mCache;

    public void addOnDataChangedListener(OnDataChangedListener listener) {
        if (listener == null) {
            return;
        }

        Message msg = mHandler.obtainMessage(
                InnerHandler.MSG_ADD_LISTENER);
        msg.obj = listener;
        mHandler.sendMessage(msg);
    }

    public void removeOnDataChangedListener(OnDataChangedListener listener) {
        if (listener == null) {
            return;
        }

        Message msg = mHandler.obtainMessage(
                InnerHandler.MSG_REMOVE_LISTENER);
        msg.obj = listener;
        mHandler.sendMessage(msg);
    }

    public void load(OnDataLoadedListener listener) {
        if (listener == null) {
            return;
        }

        Message msg = mHandler.obtainMessage(
                InnerHandler.MSG_LOAD);
        msg.obj = listener;
        mHandler.sendMessage(msg);
    }

    public void clear() {
        mHandler.sendEmptyMessage(InnerHandler.MSG_CLEAR);
    }

    public void update(AppModel value) {
        if (value == null) {
            return;
        }

        Message msg = mHandler.obtainMessage(
                InnerHandler.MSG_UPDATE);
        msg.obj = value;
        mHandler.sendMessage(msg);
    }

    public void update(List<AppModel> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        Message msg = mHandler.obtainMessage(
                InnerHandler.MSG_UPDATE_BATCH);
        msg.obj = values;
        mHandler.sendMessage(msg);
    }

    public void remove(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }

        Message msg = mHandler.obtainMessage(
                InnerHandler.MSG_REMOVE);
        msg.obj = key;
        mHandler.sendMessage(msg);
    }

    private DataCacheManager() {
        mThread = new HandlerThread(TAG);
        mThread.start();
        mHandler = new InnerHandler(mThread.getLooper());

        mListenerRefQ = new ReferenceQueue<OnDataChangedListener>();
        mListenerList = new LinkedList<WeakReference<OnDataChangedListener>>();

        mCache = new DataCacheImpl();
    }

    private void handleAddOnDataChangedListener(OnDataChangedListener listener) {
        removeDetachedListener();

        for (WeakReference<OnDataChangedListener> listenerRef : mListenerList) {
            OnDataChangedListener listenerSaved = listenerRef.get();
            if (listenerSaved == null) {
                continue;
            }

            if (listenerSaved == listener) {
                return;
            }
        }

        mListenerList.add(new WeakReference<
                OnDataChangedListener>(listener, mListenerRefQ));
    }

    private void handleRemoveOnDataChangedListener(OnDataChangedListener listener) {
        if (mListenerList.isEmpty()) {
            return;
        }

        int size = mListenerList.size();
        for (int i = size - 1; i >= 0; --i) {
            WeakReference<OnDataChangedListener> listenerRef = mListenerList.get(i);
            OnDataChangedListener listenerSaved = listenerRef.get();
            if (listenerSaved == null) {
                continue;
            }

            if (listenerSaved == listener) {
                mListenerList.remove(listenerRef);
            }
        }
    }

    private void handleLoad(OnDataLoadedListener listener) {
        listener.onLoaded(mCache.get());
    }

    private void handleClear() {
        if (mCache.isEmpty()) {
            return;
        }

        mCache.clear();
        notifyOnBatchDataChanged(
                OnDataChangedListener.OP_CLEAR, null);
    }

    private void handleUpdate(AppModel value) {
        AppModel model = mCache.update(value);
        if (model == null) {
            return;
        }

        notifyOnDataChanged(
                OnDataChangedListener.OP_UPDATE, model);
    }

    private void handleUpdateBatch(List<AppModel> values) {
        List<AppModel> models = mCache.update(values);
        if (models == null || models.isEmpty()) {
            return;
        }

        notifyOnBatchDataChanged(
                OnDataChangedListener.OP_UPDATE, models);
    }

    private void handleRemove(String key) {
        AppModel value = mCache.remove(key);
        if (value == null) {
            return;
        }

        notifyOnDataChanged(
                OnDataChangedListener.OP_REMOVE, value);
    }

    private void notifyOnDataChanged(byte opCode, AppModel value) {
        if (mListenerList.isEmpty()) {
            return;
        }

        for (WeakReference<OnDataChangedListener> listenerRef : mListenerList) {
            OnDataChangedListener listener = listenerRef.get();
            if (listener == null) {
                return;
            }

            listener.onDataChanged(opCode, value);
        }
    }

    private void notifyOnBatchDataChanged(byte opCode, List<AppModel> values) {
        if (mListenerList.isEmpty()) {
            return;
        }

        for (WeakReference<OnDataChangedListener> listenerRef : mListenerList) {
            OnDataChangedListener listener = listenerRef.get();
            if (listener == null) {
                return;
            }

            listener.onBatchDataChanged(opCode, values);
        }
    }

    private void removeDetachedListener() {
        Reference<? extends OnDataChangedListener> listenerRef;
        while ((listenerRef = mListenerRefQ.poll()) != null) {
            mListenerList.remove(listenerRef);
        }
    }
}
