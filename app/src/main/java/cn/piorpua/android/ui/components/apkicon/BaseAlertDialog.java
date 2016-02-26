package cn.piorpua.android.ui.components.apkicon;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAlertDialog {
	
	public static interface IOnButtonClicked {
		
		void onNegativeClicked();
		
		void onPositiveClicked();
		
	}
	
	protected final Context mContext;
	protected final AlertDialog.Builder mBuilder;
	
	protected AlertDialog mDialog;
	protected IOnButtonClicked mBtnListener;
	
	public BaseAlertDialog(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("Error: Context can't be null.");
		}
		
		mContext = context;
		mBuilder = new AlertDialog.Builder(mContext);
	}
	
	public void setOnButtonClickedListener(
			IOnButtonClicked listener) {
		
		mBtnListener = listener;
	}
	
	public void show() {
		if (mDialog == null) {
			mDialog = mBuilder.create();
		}
		
		if (mDialog == null || 
				mDialog.isShowing()) {
			
			return ;
		}
		
		mDialog.show();
	}
	
	public void hide() {
		if (mDialog == null || 
				!mDialog.isShowing()) {
			
			return ;
		}
		
		mDialog.hide();
	}
	
	public void dismiss() {
		if (mDialog == null) {
			return ;
		}
		
		mDialog.dismiss();
	}
	
	protected abstract int getConvertViewID();
	
	protected View getConvertView(ViewGroup viewGroup) {
		try {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			return inflater.inflate(getConvertViewID(), viewGroup);
		} catch (Exception e) {
		}
		
		return null;
	}
	
}
