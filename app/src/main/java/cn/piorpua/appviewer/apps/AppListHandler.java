package cn.piorpua.appviewer.apps;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cn.piorpua.android.utils.CommonUtil;
import cn.piorpua.android.utils.components.GetPackageStats;
import cn.piorpua.appviewer.apps.data.AppModel;

public final class AppListHandler {
	
	public static interface ICallback {
		
		void onLoad(List<AppModel> list);
		
	}
	
	private class AppLoadRunnable implements Runnable {
		
		private ICallback mCallback;
		
		public AppLoadRunnable(ICallback callback) {
			mCallback = callback;
		}

		@Override
		public void run() {
			PackageManager pkgMgr = null;
			List<PackageInfo> pkgList = null;
			try {
				pkgMgr = mCtx.getPackageManager();
				pkgList = pkgMgr.getInstalledPackages(0);
			} catch (Exception e) {
			}
			
			if (pkgList == null || 
					pkgList.isEmpty()) {
				
				if (mCallback != null) {
					mCallback.onLoad(null);
					mCallback = null;
				}
				return ;
			}
			
			final AtomicInteger count = new AtomicInteger(0);
			List<AppModel> list = new ArrayList<AppModel>();
			
			for (PackageInfo pkgInfo : pkgList) {
				final AppModel appModel = new AppModel();
				
				try {
					final ApplicationInfo appInfo = pkgInfo.applicationInfo;

					appModel.setSystemApp(CommonUtil.
                            MyApplication.isSystemApp(appInfo));
					appModel.setAppPath(CommonUtil.
                            MyString.getDeepCopy(appInfo.publicSourceDir, true));

                    String pkgName = CommonUtil.
                            MyString.getDeepCopy(pkgInfo.packageName, true);
					appModel.setPkgName(pkgName);

                    String appName = pkgMgr.getApplicationLabel(appInfo).toString();
					if (TextUtils.isEmpty(appName)) {
                        appName = pkgName;
					}
					appModel.setAppName(CommonUtil.
                            MyString.getDeepCopy(appName, true));

					appModel.setVersion(CommonUtil.
                            MyString.getDeepCopy(pkgInfo.versionName, true));
					
					appModel.setInstallTime(CommonUtil.
                            MyApplication.getAppInstallTime(pkgInfo));

                    count.incrementAndGet();
                    GetPackageStats stats = new GetPackageStats(mCtx, pkgName);
                    stats.setOnGetPackageStatsObserver(new GetPackageStats.IGetPackageStatsObserver() {

                        @Override
                        public void onGetStatFailed(ErrorCode Code) {
                            count.decrementAndGet();
                        }

                        @Override
                        public void onGetStatCompleted(PackageStats pkgStats) {
                            appModel.setApkSize(CommonUtil.
                                    MyApplication.getApkSize(appInfo, pkgStats));
                            appModel.setDataSize(CommonUtil.
                                    MyApplication.getApkExternalDataSize(pkgStats));

                            count.decrementAndGet();
                        }
                    });
                    stats.start();
				} catch (Exception e) {
					continue ;
				}
				
				list.add(appModel);
			}
			
			while (count.get() > 0) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			Collections.sort(list);
			
			if (mCallback != null) {
				mCallback.onLoad(list);
				mCallback = null;
			}
		}
	}
	
	private Context mCtx;
	
	public AppListHandler(Context ctx) {
		if (ctx == null) {
			throw new IllegalArgumentException("Error: Context can't be null.");
		}
		
		mCtx = ctx;
	}
	
	public void asycLoadAppList(ICallback callback) {
        String threadName = "AppListHandler-".concat(
                String.valueOf(System.currentTimeMillis()));
		new Thread(new AppLoadRunnable(callback), threadName).start();
	}
}
