package cn.piorpua.appviewer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.piorpua.android.ui.components.apkicon.BaseAlertDialog;
import cn.piorpua.android.ui.components.apkicon.GetApkIcon;
import cn.piorpua.android.ui.components.apkicon.GetDrawable;
import cn.piorpua.android.utils.CommonUtil;
import cn.piorpua.appviewer.R;
import cn.piorpua.appviewer.apps.AppModel;
import cn.piorpua.appviewer.apps.AppModelCmp;
import cn.piorpua.appviewer.ui.AdvanceConfDialog.AdvanceConfig;

public final class ListViewHandler 
           implements AdapterView.OnItemClickListener, 
                        AdapterView.OnItemLongClickListener {
	
	private static class ViewHolder {
		
		public ImageView iconIv;
		public TextView titleTv;
		public TextView subtitleTv;
		
	}
	
	private enum ListType {
		WHOLE,
		FILTER, 
	}
	
	private class MyAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		
		public MyAdapter() {
			mInflater = mAct.getLayoutInflater();
		}

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		@Override
		public AppModel getItem(int position) {
			if (mList == null) {
				return null;
			}
			
			if (position < 0 || 
					position >= mList.size()) {
				
				return null;
			}
			
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(
				int position, View convertView, ViewGroup parent) {
			
			AppModel appModel = getItem(position);
			if (appModel == null) {
				return null;
			}
			
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.listview_item_layout, null);
				
				viewHolder = new ViewHolder();
				viewHolder.iconIv = (ImageView) convertView.findViewById(R.id.icon);
				viewHolder.titleTv = (TextView) convertView.findViewById(R.id.title);
				viewHolder.subtitleTv = (TextView) convertView.findViewById(R.id.subtitle);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (viewHolder == null) {
				return null;
			}
			
			String appPath = appModel.getAppPath();
			Drawable icon = GetDrawable.getInstance(mAct).getIcon(
					appPath, viewHolder.iconIv, new GetApkIcon());
			if (icon == null) {
				viewHolder.iconIv.setImageDrawable(
						mAct.getResources().getDrawable(R.mipmap.ic_launcher));
			} else {
				viewHolder.iconIv.setImageDrawable(icon);
			}
			
			String appName = appModel.getAppName();
			String pkgName = appModel.getPkgName();
			
			viewHolder.titleTv.setText(appName);
			viewHolder.subtitleTv.setText(pkgName);
			
			return convertView;
		}
		
	}
	
	private final class MyDialogButtonListener implements BaseAlertDialog.IOnButtonClicked {
		
		private AppModel mModel;
		
		public void attachModel(AppModel model) {
			mModel = model;
		}

		@Override
		public void onNegativeClicked() {
			final String pkgName = getPkgName();
			if (TextUtils.isEmpty(pkgName)) {
				mDialog.hide();
				return ;
			}
			
			CommonUtil.MyApplication.showAppSystemDetail(mAct, pkgName);
		}

		@Override
		public void onPositiveClicked() {
			final String pkgName = getPkgName();
			if (TextUtils.isEmpty(pkgName)) {
				mDialog.hide();
				return ;
			}
			
			mModelSelected = mModel;
			CommonUtil.MyApplication.showAppSystemUninstall(mAct, pkgName);
		}
		
		private String getPkgName() {
			if (mModel == null) {
				return null;
			}
			
			return mModel.getPkgName();
		}
		
	}
	
	private final ListView mView;
	private final MyAdapter mAdapter;
	
	private final AppDetailDialog mDialog;
	private final MyDialogButtonListener mDialogListener;
	
	private AppModel mModelSelected;
	private AppModelCmp mModelCmp;
	
	private Activity mAct;
	private List<AppModel> mList;
	private List<AppModel> mOrgList;
	private List<AppModel> mWholeList;
	private List<AppModel> mFilterList;
	
	public ListViewHandler(
			Activity activity, ListView view) {
		
		if (activity == null || 
				view == null) {
			
			throw new IllegalArgumentException("Error: Activity or ListView can't be null.");
		}
		
		mAct = activity;
		mView = view;
		mAdapter = new MyAdapter();
		mView.setAdapter(mAdapter);
		
		mView.setOnItemClickListener(this);
		mView.setOnItemLongClickListener(this);
		
		mModelCmp = new AppModelCmp();
		
		mWholeList = new ArrayList<AppModel>();
		mFilterList = new ArrayList<AppModel>();
		
		mDialog = new AppDetailDialog(mAct);
		mDialogListener = new MyDialogButtonListener();
		mDialog.setOnButtonClickedListener(mDialogListener);
	}
	
	@Override
	public void onItemClick(
			AdapterView<?> parent, View view, int position, long id) {
		
		if (mList == null || 
				mList.isEmpty()) {
			
			return ;
		}
		
		AppModel appModel = mList.get(position);
		if (appModel == null) {
			return ;
		}
		
		mDialog.attachModel(appModel);
		mDialogListener.attachModel(appModel);
		mDialog.show();
	}

	@Override
	public boolean onItemLongClick(
			AdapterView<?> parent, View view, int position, long id) {
		
		if (mList == null || 
				mList.isEmpty()) {
			
			return false;
		}
		
		AppModel appModel = mList.get(position);
		if (appModel == null) {
			return false;
		}
		
		final String pkgName = appModel.getPkgName();
		return CommonUtil.MyApplication.startActivity(mAct, pkgName);
	}
	
	public void setList(List<AppModel> list) {
		mOrgList = list;
		
		resetList();
		switchList(ListType.WHOLE);
	}
	
	public void filter(String keyWords) {
		if (mWholeList == null || 
				mWholeList.isEmpty()) {
			
			return ;
		}
		
		mFilterList.clear();
		if (TextUtils.isEmpty(keyWords)) {
			switchList(ListType.WHOLE);
			return ;
		}
		
		for (AppModel appModel : mWholeList) {
			if (appModel == null) {
				continue ;
			}
			
			if (appModel.hit(keyWords)) {
				mFilterList.add(appModel);
			}
		}
		switchList(ListType.FILTER);
	}
	
	public void filter(AdvanceConfig config) {
		try {
			resetList();
			
			if (config == null) {
				return ;
			}
			
			if (config.sysAppFilter) {
				int size = mWholeList.size();
				for (int i = size - 1; i >= 0; --i) {
					AppModel model = mWholeList.get(i);
					if (model == null) {
						continue ;
					}
					
					if (model.isSystemApp()) {
						mWholeList.remove(i);
					}
				}
			}
			
			mModelCmp.setOrder(config.sortOrder);
			mModelCmp.setType(config.sortType);
			
			Collections.sort(mWholeList, mModelCmp);
		} finally {
			switchList(ListType.WHOLE);
		}
	}
	
	public void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}
	
	public void onResume() {
		if (mModelSelected == null) {
			return ;
		}
		
		final String pkgName = mModelSelected.getPkgName();
		if (TextUtils.isEmpty(pkgName)) {
			mModelSelected = null;
			return ;
		}
		
		if (!CommonUtil.MyApplication.isAppInstalled(mAct, pkgName)) {
			if (mWholeList != null) {
				mWholeList.remove(mModelSelected);
			}
			
			if (mFilterList != null) {
				mFilterList.remove(mModelSelected);
			}
			
			notifyDataSetChanged();
		}
		
		mModelSelected = null;
	}
	
	public void onStop() {
		mDialog.dismiss();
		mDialog.hide();
	}
	
	private void switchList(ListType listType) {
		if (listType == null) {
			return ;
		}
		
		switch (listType) {
		case WHOLE:
			mList = mWholeList;
			break;
		
		case FILTER:
			mList = mFilterList;
			break;
		}
	}
	
	private void resetList() {
		mWholeList.clear();
		
		if (mOrgList == null || 
				mOrgList.isEmpty()) {
			
			return ;
		}
		
		for (AppModel model : mOrgList) {
			mWholeList.add(model);
		}
	}
	
}
