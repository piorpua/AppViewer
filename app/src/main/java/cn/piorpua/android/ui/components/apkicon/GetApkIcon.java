package cn.piorpua.android.ui.components.apkicon;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class GetApkIcon implements GetDrawableRunnable {
	
	private Handler mHandler;
	private Context mContext;
	private String mSource;

	@Override
	public void run() {
		Drawable daw = getApkSystemIcon(mContext, mSource);
		if (daw == null) {
			return;
		}
		
		Message msg = mHandler.obtainMessage();
		msg.obj = daw;
		mHandler.sendMessage(msg);
	}

	@Override
	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	@Override
	public void setSouce(String source) {
		mSource = source;

	}

	@Override
	public void setContext(Context context) {
		mContext = context;
	}
	
	private static Drawable getApkSystemIcon(
			Context context, String apkPath) {
		
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
            } catch (Exception e) {
            }
        }
        return null;
    }

}
