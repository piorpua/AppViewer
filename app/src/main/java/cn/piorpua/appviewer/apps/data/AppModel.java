package cn.piorpua.appviewer.apps.data;

import android.text.TextUtils;

import java.util.Locale;

import cn.piorpua.android.utils.CommonUtil;

public final class AppModel implements Comparable<AppModel> {
	
	protected String mAppPath;
	protected String mPkgName;
	protected String mAppName;
	protected String mVersion;
	protected int mVersionCode;
	protected boolean mSystem;
	
	protected long mInstallTime;
	protected long mApkSize;
	protected long mDataSize;

	@Override
	public int compareTo(AppModel another) {
		if (another == null) {
			return 1;
		}
		
		if (mSystem && !another.mSystem) {
			return 1;
		} else if (!mSystem && another.mSystem) {
			return -1;
		}
		
		int compareCode;
		
		compareCode = CommonUtil.MyString.compare(
				mAppName, another.mAppName, Locale.CHINA);
		if (compareCode != 0) {
			return compareCode;
		}
		
		compareCode = CommonUtil.MyString.compare(
				mPkgName, another.mPkgName, Locale.CHINA);
		if (compareCode != 0) {
			return compareCode;
		}
		
		compareCode = CommonUtil.MyString.compare(
				mAppPath, another.mAppPath, Locale.CHINA);
		if (compareCode != 0) {
			return compareCode;
		}
		
		long detaSize = (mApkSize + mDataSize) - (another.mApkSize + another.mDataSize);
		if (detaSize > 0) {
			return 1;
		} else if (detaSize < 0) {
			return -1;
		}
		
		long detaTime = mInstallTime - another.mInstallTime;
		if (detaTime > 0) {
			return 1;
		} else if (detaTime < 0) {
			return -1;
		}
		
		return 0;
	}
	
	public boolean hit(String keyWords) {
		if (TextUtils.isEmpty(keyWords)) {
			return false;
		}
		
		if (hit(mAppName, keyWords)) {
			return true;
		}
		
		if (hit(mPkgName, keyWords)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isSystemApp() {
		return mSystem;
	}
	public void setSystemApp(boolean isSystemApp) {
		mSystem = isSystemApp;
	}
	
	public String getAppPath() {
		return getStrDeepCopy(mAppPath);
	}
	public void setAppPath(String appPath) {
		mAppPath = getStrDeepCopy(appPath);
	}
	
	public String getPkgName() {
		return getStrDeepCopy(mPkgName);
	}
	public void setPkgName(String pkgName) {
		mPkgName = getStrDeepCopy(pkgName);
	}
	
	public String getAppName() {
		return getStrDeepCopy(mAppName);
	}
	public void setAppName(String appName) {
		mAppName = getStrDeepCopy(appName);
	}
	
	public String getVersion() {
		return getStrDeepCopy(mVersion);
	}
	public void setVersion(String version) {
		mVersion = getStrDeepCopy(version);
	}

	public int getVersionCode() {
		return mVersionCode;
	}
	public void setVersionCode(int code) {
		mVersionCode = code;
	}
	
	public long getInstallTime() {
		return mInstallTime;
	}
	public void setInstallTime(long installTime) {
		mInstallTime = installTime;
	}
	
	public long getApkSize() {
		return mApkSize;
	}
	public void setApkSize(long apkSize) {
		mApkSize = apkSize;
	}
	
	public long getDataSize() {
		return mDataSize;
	}
	public void setDataSize(long dataSize) {
		mDataSize = dataSize;
	}
	
	private String getStrDeepCopy(String target) {
		return CommonUtil.MyString.getDeepCopy(target, true);
	}
	
	private boolean hit(String target, String keyWords) {
		if (!TextUtils.isEmpty(target) && 
				target.contains(keyWords)) {
			
			return true;
		}
		
		return false;
	}

}
