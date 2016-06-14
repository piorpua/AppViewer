package cn.piorpua.appviewer.apps.data;

import java.util.Comparator;
import java.util.Locale;

import cn.piorpua.android.utils.CommonUtil;

public final class AppModelCmp implements Comparator<AppModel> {
	
	public static enum SortOrder {
		ASC, 
		DESC, 
	}
	
	public static enum SortType {
		DEFAULT, 
		APPNAME, 
		PKGNAME, 
		TIME, 
		SIZE, 
	}
	
	private static final SortOrder DEFAULT_ORDER = SortOrder.ASC;
	private static final SortType  DEFAULT_TYPE  = SortType.DEFAULT;
	
	private SortOrder mOrder = DEFAULT_ORDER;
	private SortType mType = DEFAULT_TYPE;
	
	@Override
	public int compare(AppModel lhs, AppModel rhs) {
		boolean lEmpty = (lhs == null);
		boolean rEmpty = (rhs == null);
		
		if (lEmpty && rEmpty) {
			return 0;
		} else if (lEmpty && !rEmpty) {
			return less();
		} else if (!lEmpty && rEmpty) {
			return greater();
		}
		
		switch (mType) {
		case DEFAULT:
		{
			return getCmpCode(lhs.compareTo(rhs));
		}
		
		case APPNAME:
		{
			String lAppName = lhs.getAppName();
			String rAppName = rhs.getAppName();
			
			int cmpCode = CommonUtil.MyString.compare(
					lAppName, rAppName, Locale.CHINA);
			if (cmpCode != 0) {
				return getCmpCode(cmpCode);
			}
			break;
		}
			
		case PKGNAME:
		{
			String lPkgName = lhs.getPkgName();
			String rPkgName = rhs.getPkgName();
			
			int cmpCode = CommonUtil.MyString.compare(
					lPkgName, rPkgName, Locale.CHINA);
			if (cmpCode != 0) {
				return getCmpCode(cmpCode);
			}
			break;
		}
		
		case TIME:
		{
			long lTime = lhs.getInstallTime();
			long rTime = rhs.getInstallTime();
			long detaTime = lTime - rTime;
			
			if (detaTime > 0) {
				return getCmpCode(1);
			} else if (detaTime < 0) {
				return getCmpCode(-1);
			}
			break;
		}
		
		case SIZE:
		{
			long lSize = lhs.getApkSize() + lhs.getDataSize();
			long rSize = rhs.getApkSize() + rhs.getDataSize();
			long detaSize = lSize - rSize;
			
			if (detaSize > 0) {
				return getCmpCode(1);
			} else if (detaSize < 0) {
				return getCmpCode(-1);
			}
			break;
		}
		
		}
		
		return getCmpCode(lhs.compareTo(rhs));
	}
	
	public void setOrder(SortOrder order) {
		if (order == null) {
			return ;
		}
		
		mOrder = order;
	}
	
	public void setType(SortType type) {
		if (type == null) {
			return ;
		}
		
		mType = type;
	}
	
	private int getCmpCode(int cmpCode) {
		if (cmpCode < 0) {
			return less();
		} else if (cmpCode > 0) {
			return greater();
		}
		
		return 0;
	}
	
	private int greater() {
		switch (mOrder) {
		case ASC:
			return 1;
			
		case DESC:
			return -1;
		}
		
		return 0;
	}
	
	private int less() {
		switch (mOrder) {
		case ASC:
			return -1;
			
		case DESC:
			return 1;
		}
		
		return 0;
	}

}
