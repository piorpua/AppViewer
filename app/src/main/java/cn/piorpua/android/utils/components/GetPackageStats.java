package cn.piorpua.android.utils.components;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import android.text.TextUtils;
import cn.piorpua.android.utils.components.GetPackageStats.IGetPackageStatsObserver.ErrorCode;

public final class GetPackageStats extends IPackageStatsObserver.Stub {
	
	public static interface IGetPackageStatsObserver {
		
		public static enum ErrorCode {
			ILLEGAL_ARGS, 
			EXCEPTION, 
			FAILED, 
		}
		
		void onGetStatFailed(ErrorCode Code);
		void onGetStatCompleted(PackageStats pkgStats);
		
	}
	
	private Context mContext;
	private String mPkgName;
	private boolean mStarted;
	
	private IGetPackageStatsObserver mObserver;
	
	public GetPackageStats(Context context, String pkgName) {
		mContext = context;
		mPkgName = pkgName;
	}
	
	public void setOnGetPackageStatsObserver(
			IGetPackageStatsObserver observer) {
		
		mObserver = observer;
	}
	
	synchronized public void start() {
		if (mStarted) {
			return ;
		}
		
		mStarted = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (mContext == null || 
						TextUtils.isEmpty(mPkgName)) {
					
					if (mObserver != null) {
						mObserver.onGetStatFailed(
								ErrorCode.ILLEGAL_ARGS);
					}
					clear();
					return ;
				}
				
				try {
					PackageManager pkgMgr = mContext.getPackageManager();
					Method mthGetPackageSizeInfo = pkgMgr.getClass().getMethod(
							"getPackageSizeInfo", String.class, IPackageStatsObserver.class);
					mthGetPackageSizeInfo.invoke(pkgMgr, mPkgName, GetPackageStats.this);
				} catch (Exception e) {
					e.printStackTrace();
					if (mObserver != null) {
						mObserver.onGetStatFailed(
								ErrorCode.EXCEPTION);
					}
					clear();
					return ;
				}
			}
		}).start();
	}
	
	@Override
	public void onGetStatsCompleted(
			PackageStats pkgStats, boolean succ) throws RemoteException {
		
		try {
			if (!succ) {
				if (mObserver != null) {
					mObserver.onGetStatFailed(
							ErrorCode.FAILED);
				}
				return ;
			}
			
			if (mObserver != null) {
				mObserver.onGetStatCompleted(pkgStats);
			}
		} finally {
			clear();
		}
	}
	
	private void clear() {
		mContext = null;
		mPkgName = null;
		mObserver = null;
	}

}
