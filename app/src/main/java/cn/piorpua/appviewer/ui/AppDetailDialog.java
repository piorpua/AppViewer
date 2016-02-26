package cn.piorpua.appviewer.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.piorpua.android.ui.components.apkicon.BaseAlertDialog;
import cn.piorpua.android.ui.components.apkicon.GetApkIcon;
import cn.piorpua.android.ui.components.apkicon.GetDrawable;
import cn.piorpua.android.utils.CommonUtil;
import cn.piorpua.appviewer.R;
import cn.piorpua.appviewer.apps.AppModel;

public final class AppDetailDialog extends BaseAlertDialog {
	
	private ImageView mIconIv;
	private TextView mTitleTv;
	private TextView mSubTitleTv;
	private TextView mLevelTv;
	private TextView mInstallTimeTv;
	private TextView mApkSizeTv;
	private TextView mDataSizeTv;
	private TextView mPkgNameTv;
	private TextView mAppPathTv;
	
	private Button mLeftBtn;
	private Button mRightBtn;
	
	private String mLevelDesc;
	private String mLevelDescSys;
	private SimpleDateFormat mDateFormat;
	
	public AppDetailDialog(Context context) {
		super(context);
		
		initView();
		init();
	}
	
	public void attachModel(AppModel model) {
		if (model == null) {
			return ;
		}
		
		final String appPath = model.getAppPath();
		if (!TextUtils.isEmpty(appPath)) {
			Drawable icon = GetDrawable.getInstance(mContext).getIcon(
					appPath, mIconIv, new GetApkIcon());
			if (icon == null) {
				mIconIv.setImageDrawable(
						mContext.getResources().getDrawable(R.mipmap.ic_launcher));
			} else {
				mIconIv.setImageDrawable(icon);
			}
			
			mAppPathTv.setText(mContext.getString(
					R.string.appdetail_dialog_apppath_desc, appPath));
		}
		
		mTitleTv.setText(model.getAppName());
		mSubTitleTv.setText(model.getVersion());
		
		if (model.isSystemApp()) {
			mLevelTv.setText(Html.fromHtml(mLevelDescSys));
			mRightBtn.setVisibility(View.GONE);
		} else {
			mLevelTv.setText(mLevelDesc);
			mRightBtn.setVisibility(View.VISIBLE);
		}
		
		mInstallTimeTv.setText(mContext.getString(
				R.string.appdetail_dialog_installtime_desc, 
				mDateFormat.format(new Date(model.getInstallTime()))));
		
		long apkSize = model.getApkSize();
		if (apkSize <= 0) {
			mApkSizeTv.setVisibility(View.GONE);
		} else {
			mApkSizeTv.setVisibility(View.VISIBLE);
			mApkSizeTv.setText(mContext.getString(
					R.string.appdetail_dialog_apksize_desc, 
					CommonUtil.MyApplication.formatSTDStorageSize(apkSize)));
		}
		
		long dataSize = model.getDataSize();
		if (dataSize <= 0) {
			mDataSizeTv.setVisibility(View.GONE);
		} else {
			mDataSizeTv.setVisibility(View.VISIBLE);
			mDataSizeTv.setText(mContext.getString(
					R.string.appdetail_dialog_datasize_desc, 
					CommonUtil.MyApplication.formatSTDStorageSize(dataSize)));
		}
		
		final String pkgName = model.getPkgName();
		if (!TextUtils.isEmpty(pkgName)) {
			mPkgNameTv.setText(mContext.getString(
					R.string.appdetail_dialog_pkgname_desc, pkgName));
		}
	}
	
	@Override
	protected int getConvertViewID() {
		return R.layout.listview_item_detail_layout;
	}
	
	private void initView() {
		View convertView = getConvertView(null);
		if (convertView == null) {
			return ;
		}
		
		mIconIv = (ImageView) convertView.findViewById(R.id.iconIv);
		mTitleTv = (TextView) convertView.findViewById(R.id.titleTv);
		mSubTitleTv = (TextView) convertView.findViewById(R.id.subtitleTv);
		mLevelTv = (TextView) convertView.findViewById(R.id.levelTv);
		mInstallTimeTv = (TextView) convertView.findViewById(R.id.installTimeTv);
		mApkSizeTv = (TextView) convertView.findViewById(R.id.apkSizeTv);
		mDataSizeTv = (TextView) convertView.findViewById(R.id.apkDataSizeTv);
		mPkgNameTv = (TextView) convertView.findViewById(R.id.pkgNameTv);
		mAppPathTv = (TextView) convertView.findViewById(R.id.appPathTv);
		
		mLeftBtn = (Button) convertView.findViewById(R.id.btnLeft);
		mLeftBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBtnListener != null) {
					mBtnListener.onNegativeClicked();
				}
			}
		});
		
		mRightBtn = (Button) convertView.findViewById(R.id.btnRight);
		mRightBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mBtnListener != null) {
					mBtnListener.onPositiveClicked();
				}
			}
		});
		
		mBuilder.setView(convertView);
	}
	
	private void init() {
		mLevelDesc = mContext.getString(
				R.string.appdetail_dialog_level_desc);
		mLevelDescSys = mContext.getString(
				R.string.appdetail_dialog_level_desc_sys);
		mDateFormat = new SimpleDateFormat("yyyy/MM/dd");
	}
	
}
