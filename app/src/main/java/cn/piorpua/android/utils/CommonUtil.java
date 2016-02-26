package cn.piorpua.android.utils;

import java.io.File;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.RuleBasedCollator;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

public final class CommonUtil {
	
	/**
	 * 应用类
	 */
	public static final class MyApplication {
		
		private static final int FLAG_ACTIVITY_CLEAR_TASK = 0x00008000;
		
		private static final String SCHEME = "package";
		
		// 调用系统 InstalledAppDetails 界面所需的 Extra 名称(用于 Android 2.1 及之前的版本)
		private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
		
		// 调用系统 InstalledAppDetails 界面所需的 Extra 名称(用于 Android 2.2)
		private static final String APP_PKG_NAME_22 = "pkg";
		
		// InstalledAppDetails 所在包名
		private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
		
		// InstalledAppDetails 类名
		private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
		
		// 调用系统 InstalledAppDetails 界面显示已安装应用程序的详细信息。
		// Android 2.3(API LEVEL 9) 以上，使用 SDK 提供的接口；
		// Android 2.3(API LEVEL 9) 以下，使用非公开的接口(详见 InstalledAppDetails 源码)
		private static final String APP_SDK_23 = "android.settings.APPLICATION_DETAILS_SETTINGS";
		
		/**
		 * 添加桌面快捷方式
		 * @param activity
		 * @param titleRes
		 * @param iconRes
		 * @warning 需要权限：com.android.launcher.permission.INSTALL_SHORTCUT
		 */
		public static final void addShortcut(Activity activity, int titleRes, int iconRes) {
			if (activity == null) {
				return ;
			}
			
			Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, 
					activity.getString(titleRes).trim());
			shortcutIntent.putExtra("duplicate", false);   // 不允许重复创建
			
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName(activity, activity.getClass().getName());
			
			String pkgName = activity.getPackageName();
			StringBuilder clsNameBuilder = new StringBuilder();
			clsNameBuilder.append(pkgName);
			clsNameBuilder.append('.');
			clsNameBuilder.append(activity.getLocalClassName());
			String clsName = clsNameBuilder.toString();
			ComponentName compName = new ComponentName(pkgName, clsName);
			
			intent.setComponent(compName);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
			
			ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(activity, iconRes);
			shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
			
			activity.sendBroadcast(shortcutIntent);
		}
		
		/**
		 * 判断是否存在桌面快捷方式
		 * @param activity
		 * @param titleRes
		 * @return
		 * @warning 需要权限：com.android.launcher.permission.READ_SETTINGS
		 */
		public static final boolean hasShortcut(Activity activity, int titleRes) {
			if (activity == null) {
				return false;
			}
			
			Cursor cursor = null;
			try {
				StringBuilder uriBuilder = new StringBuilder();
				uriBuilder.append("content://");
				uriBuilder.append("com.android.launcher.settings");
				uriBuilder.append("/favorites?notify=true");
				final Uri contentUri = Uri.parse(uriBuilder.toString());
				
				final ContentResolver cr = activity.getContentResolver();
				cursor = cr.query(contentUri, 
						new String[] { "title" }, 
						"title=?", 
						new String[] { activity.getString(titleRes).trim() }, 
						null);
				if (cursor != null && cursor.getCount() > 0) {
					return true;
				}
			} catch (Exception e) {
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			
			return false;
		}
		
		/**
		 * 打开指定包名的应用
		 * @param context
		 * @param pkgName 包名
		 * @return
		 */
		public static final boolean startActivity(
				Context context, String pkgName) {
			
			if (context == null || 
					TextUtils.isEmpty(pkgName)) {
				
				return false;
			}
			
			try {
				PackageManager pkgMgr = context.getPackageManager();
				Intent intent = pkgMgr.getLaunchIntentForPackage(pkgName);
				return startActivity(context, intent);
			} catch (Exception e) {
			}
			
			return false;
		}
		
		public static final boolean startActivity(
				Context context, Intent intent) {
			
			if (context == null || 
					intent == null) {
				
				return false;
			}
			
			try {
				context.startActivity(intent);
				return true;
			} catch (Exception e) {
			}
			
			return false;
		}
		
		public static final boolean startActivityForResult(
				Activity activity, Intent intent, int requestCode) {
			
			if (activity == null || 
					intent == null) {
				
				return false;
			}
			
			try {
				activity.startActivityForResult(intent, requestCode);
				return true;
			} catch (Exception e) {
			}
			
			return false;
		}
		
		/**
		 * 调用指定包名的系统卸载页面
		 * @param context
		 * @param pkgName 包名
		 */
		public static final void showAppSystemUninstall(
				Context context, String pkgName) {
			
			if (context == null || 
					TextUtils.isEmpty(pkgName)) {
				
				return ;
			}
			
			Uri packageURI = Uri.parse("package:" + pkgName);
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(packageURI);
			
			startActivity(context, intent);
		}
		
		/**
		 * 调用指定包名的系统卸载页面
		 * @param activity
		 * @param pkgName 包名
		 * @param requestCode
		 */
		public static final void showAppSystemUninstall(
				Activity activity, String pkgName, int requestCode) {
			
			if (activity == null || 
					TextUtils.isEmpty(pkgName)) {
				
				return ;
			}
			
			Uri packageURI = Uri.parse("package:" + pkgName);
			Intent intent = new Intent(Intent.ACTION_DELETE);
			intent.setData(packageURI);
			
			startActivityForResult(activity, intent, requestCode);
		}
		
		/**
		 * 嗲用指定包名的系统详情页面
		 * @param context
		 * @param pkgName 包名
		 * @return
		 */
		public static final boolean showAppSystemDetail(
				Context context, String pkgName) {
			
			if (context == null || 
					pkgName == null) {
				
				return false;
			}
			
			final int apiLevel = Build.VERSION.SDK_INT;
			int flags = Intent.FLAG_ACTIVITY_NEW_TASK;
			if (apiLevel >= 11) {
				flags |= FLAG_ACTIVITY_CLEAR_TASK;
			}

			Intent intent = new Intent();
			intent.setFlags(flags);
			
			if (apiLevel >= 9) {   // Android 2.3(API LEVEL 9) 以上，使用 SDK 提供的接口
				intent.setAction(APP_SDK_23);
				Uri uri = Uri.fromParts(SCHEME, pkgName, null);
				intent.setData(uri);
			} else {   // Android 2.3(API LEVEL 9) 以下，使用非公开的接口(详见 InstalledAppDetails 源码)
				final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
				intent.putExtra(appPkgName, pkgName);
			}
			
			List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(
					intent, PackageManager.MATCH_DEFAULT_ONLY);
			
			if (null == list || 
					list.isEmpty()) {
				
				return false;
			}

			return startActivity(context, intent);
		}
		
		/**
		 * 获取应用名称
		 * @param context
		 * @param pkgName 包名
		 * @return
		 */
		public static final String getAppName(Context context, String pkgName) {
			if (context == null || 
					TextUtils.isEmpty(pkgName)) {
				
				return null;
			}
			
			String appName = null;
			try {
				PackageManager pkgMgr = context.getPackageManager();
				ApplicationInfo appInfo = pkgMgr.getApplicationInfo(
						pkgName, PackageManager.GET_META_DATA);
				appName = pkgMgr.getApplicationLabel(appInfo).toString();
			} catch (Exception e) {
			}
			
			if (TextUtils.isEmpty(appName)) {
				return pkgName;
			}
			return appName;
		}
		
		/**
		 * 获取应用安装时间
		 * @param pkgInfo
		 * @return
		 */
		public static final long getAppInstallTime(PackageInfo pkgInfo) {
			if (pkgInfo == null) {
				return 0;
			}
			
			if (Build.VERSION.SDK_INT >= 9) {
				return pkgInfo.firstInstallTime;
			}
			
			try {
				String srcDir = pkgInfo.applicationInfo.sourceDir;
				return new File(srcDir).lastModified();
			} catch (Exception e) {
			}
			
			return 0;
		}
		
		/**
		 * 获取应用 Apk 大小
		 * @param appInfo
		 * @param pkgStats
		 * @return byte
		 */
		public static final long getApkSize(
				ApplicationInfo appInfo, PackageStats pkgStats) {
			
			long apkSize = 0;
			
			if (pkgStats != null) {
				apkSize = pkgStats.codeSize;
				
				if (apkSize <= 0) {
					apkSize = pkgStats.dataSize;
				}
			}
			
			if (apkSize <= 0 && isSystemApp(appInfo)) {
				try {
					String srcDir = appInfo.sourceDir;
					apkSize = new File(srcDir).length();
				} catch (Exception e) {
				}
			}
			
			return apkSize;
		}
		
		/**
		 * 获取应用外部数据大小
		 * @param pkgStats
		 * @return byte
		 */
		public static final long getApkExternalDataSize(PackageStats pkgStats) {
			if (pkgStats == null) {
				return 0;
			}
			
			int sdk = Build.VERSION.SDK_INT;
			if (sdk >= 14) {
				return pkgStats.externalCacheSize + 
						pkgStats.externalDataSize + 
						pkgStats.externalCodeSize;
			} else if (sdk >= 11) {
				return pkgStats.externalCacheSize + 
						pkgStats.externalDataSize; 
			}
			
			return 0;
		}
		
		/**
		 * 格式化标准存储大小
		 * @param size
		 * @return 默认保留2位小数
		 */
		public static final String formatSTDStorageSize(long size) {
			return formatSTDStorageSize(size, "#0.00");
		}
		
		/**
		 * 格式化标准存储大小
		 * @param size
		 * @param decimalFormatStr
		 * @return
		 */
		public static final String formatSTDStorageSize(
				long size, String decimalFormatStr) {
			
			if (size < 0 || 
					TextUtils.isEmpty(decimalFormatStr)) {
				
				return null;
			}
			
			String suffixStr = "B";
			float valSize = size;
			
			if (Float.compare(valSize, 1024.0f) >= 0) {
				suffixStr = "KB";
				valSize /= 1024.0f;
				
				if (Float.compare(valSize, 1024.0f) >= 0) {
					suffixStr = "MB";
					valSize /= 1024.0f;
					
					if (Float.compare(valSize, 1024.0f) >= 0) {
						suffixStr = "GB";
						valSize /= 1024.0f;
					}
				}
			}
			
			DecimalFormat decimalFormat = new DecimalFormat(decimalFormatStr);
			StringBuilder strBuilder = new StringBuilder(
					decimalFormat.format(valSize));
			strBuilder.append(suffixStr);
			
			return strBuilder.toString();
		}
		
		/**
		 * 判断是否是系统应用
		 * @param appInfo
		 * @return
		 */
		public static final boolean isSystemApp(ApplicationInfo appInfo) {
			if (appInfo == null) {
				return false;
			}
			
			return ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 || 
					 (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
		}
		
		/**
		 * 判断是否已安装指定包名的应用
		 * @param context
		 * @param pkgName 包名
		 * @return
		 */
		public static final boolean isAppInstalled(Context context, String pkgName) {
			if (context == null || 
					TextUtils.isEmpty(pkgName)) {
				
				return false;
			}
			
			try {
				PackageManager pkgMgr = context.getPackageManager();
				pkgMgr.getPackageInfo(pkgName, 0);
				return true;
			} catch (Exception e) {
			}
			
			return false;
		}
		
	}
	
	/**
	 * 字符串类
	 */
	public static final class MyString {
		
		/**
		 * 字符串比较
		 * @param lStr
		 * @param rStr
		 * @param ignoreCase 是否忽略大小写
		 * @return
		 */
		public static final int compare(
				String lStr, String rStr, boolean ignoreCase) {
			
			boolean lEmpty = TextUtils.isEmpty(lStr);
			boolean rEmpty = TextUtils.isEmpty(rStr);
			
			if (lEmpty && !rEmpty) {
				return -1;
			} else if (!lEmpty && rEmpty) {
				return 1;
			} else if (lEmpty && rEmpty) {
				return 0;
			}
			
			return ignoreCase ? 
					lStr.compareToIgnoreCase(rStr) : 
					lStr.compareTo(rStr);
		}
		
		/**
		 * 根据 Locale 进行字符串比较
		 * @param lStr
		 * @param rStr
		 * @param locale
		 * @return
		 */
		public static final int compare(
				String lStr, String rStr, Locale locale) {
			
			boolean lEmpty = TextUtils.isEmpty(lStr);
			boolean rEmpty = TextUtils.isEmpty(rStr);
			
			if (lEmpty && !rEmpty) {
				return -1;
			} else if (!lEmpty && rEmpty) {
				return 1;
			} else if (lEmpty && rEmpty) {
				return 0;
			}
			
			RuleBasedCollator collator = null;
			if (locale != null) {
				try {
					collator = (RuleBasedCollator) Collator.getInstance(locale);
				} catch (Exception e) {
					collator = null;
				}
			}
			
			if (collator == null) {
				return lStr.compareTo(rStr);
			}
			return collator.compare(lStr, rStr);
		}
		
		/**
		 * 拷贝字符串
		 * @param target
		 * @param allowRetNull 是否允许返回 null
		 * @return
		 */
		public static final String getDeepCopy(String target, boolean allowRetNull) {
			if (TextUtils.isEmpty(target)) {
				return allowRetNull ? null : "";
			}
			
			return "" + target;
		}
		
		/**
		 * 拷贝字符串数组
		 * @param targets
		 * @param allowNull
		 * @return
		 */
		public static final String[] getDeepCopy(String[] targets, boolean allowNull) {
			if (targets == null || 
					targets.length <= 0) {
				
				return null;
			}
			
			int len = targets.length;
			String[] retVal = new String[len];
			for (int i = 0; i < len; ++i) {
				retVal[i] = getDeepCopy(targets[i], allowNull);
			}
			return retVal;
		}
		
	}
	
	/**
	 * 集合类
	 */
	public static final class MyCollection {
		
		/**
		 * 判断指定集合是否为空
		 * @param collection
		 * @return
		 */
		public static final boolean isEmpty(Collection<?> collection) {
			if (collection == null || 
					collection.isEmpty()) {
				
				return true;
			}
			
			return false;
		}
		
	}
	
}
