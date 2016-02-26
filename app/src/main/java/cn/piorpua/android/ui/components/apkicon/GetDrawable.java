package cn.piorpua.android.ui.components.apkicon;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.piorpua.appviewer.R;

public class GetDrawable {

	public static final int TYPE_PACKAGE_NAME = 0;
	
	private static final int TYPE_SOURCE_DIR = 1;
	
	private static GetDrawable sIns;
	
	synchronized public static GetDrawable getInstance(Context context) {
		if (sIns == null) {
			sIns = new GetDrawable(context);
		}
		return sIns;
	}
	
	private interface OnCacheListener {
        void onCacheIcon(String apkPath);
    }
	
    // 利用线程池来进行管理
    private ExecutorService mExecutorService = 
    		Executors.newFixedThreadPool(3);
    private HashMap<String, SoftReference<Drawable>> mImageCache = 
    		new HashMap<String, SoftReference<Drawable>>();
    private OnCacheListener mOnCacheListener;
    
    private Context mContext;
    private Drawable mDefaultAppIcon;
    
    public void setOnCacheListener(OnCacheListener listener) {
        if (mOnCacheListener != listener) {
        	mOnCacheListener = listener;
        }
    }

    public Drawable getIcon(
    		final String apkPath, final ImageView imageview, GetDrawableRunnable runnable) {
    	
        Drawable drawable = mDefaultAppIcon;
        if (apkPath == null || imageview == null) {
        	return drawable;
        }
        imageview.setTag(apkPath);
        SoftReference<Drawable> softReference = mImageCache.get(apkPath);
        if (softReference != null) {
            drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            } else {
                drawable = mDefaultAppIcon;
            }
        }

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                String tag = (String) imageview.getTag();
                if (tag != null && !tag.equals(apkPath)) {
                    return;
                }
                
                imageview.setImageDrawable((Drawable) msg.obj);
                mImageCache.put(apkPath, new SoftReference<Drawable>((Drawable) msg.obj));
            }

        };
        runnable.setHandler(handler);
        runnable.setSouce(apkPath);
        runnable.setContext(mContext);
        mExecutorService.execute(runnable);
        return drawable;
    }

    public Drawable getIcon(String source, int type) {
        Drawable drawable = mDefaultAppIcon;

        if (source == null)
            return drawable;

        String path = source;
        if (type == TYPE_PACKAGE_NAME) {
            try {
                PackageManager pm = mContext.getPackageManager();
                ApplicationInfo aInfo = pm.getApplicationInfo(source, 0);
                path = aInfo.sourceDir;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (path == null)
            return drawable;

        if (type == TYPE_PACKAGE_NAME || type == TYPE_SOURCE_DIR) {
            if (mImageCache.containsKey(path)) {
                SoftReference<Drawable> softReference = mImageCache.get(path);
                drawable = softReference.get();
                if (drawable != null) {
                    return drawable;
                }
            }
            drawable = getApkSystemIcon(mContext, path);
            mImageCache.put(path, new SoftReference<Drawable>(drawable));
        }

        return drawable;
    }
    
    private GetDrawable(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
            try {
            	mDefaultAppIcon = context.getResources().getDrawable(
            			R.mipmap.ic_launcher);
            } catch (Exception e) {
            	mDefaultAppIcon = null;
            }
        }
    }

    private static Drawable getApkSystemIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(
        		apkPath, PackageManager.GET_ACTIVITIES);
        
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
            }
        }
        return null;
    }

}
