package cn.piorpua.appviewer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.piorpua.android.ui.components.apkicon.BaseAlertDialog;
import cn.piorpua.android.utils.CommonUtil;
import cn.piorpua.appviewer.apps.AppListHandler;
import cn.piorpua.appviewer.apps.AppModel;
import cn.piorpua.appviewer.ui.AdvanceConfDialog;
import cn.piorpua.appviewer.ui.ListViewHandler;
import cn.piorpua.appviewer.ui.SearchViewHandler;

public class MainActivity extends Activity {
	
	private static final class MyHandler extends Handler {
		
		private static final int MSG_LOADING_SHOW = 0x0001;
		private static final int MSG_LOADING_HIDE = 0x0002;
		private static final int MSG_DATA_READY   = 0x0010;
		
		private WeakReference<MainActivity> mRefAct;
		
		public MyHandler(MainActivity act) {
			mRefAct = new WeakReference<MainActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			MainActivity act = mRefAct.get();
			if (act == null || 
					act.isFinishing()) {
				
				return ;
			}
			
			switch (msg.what) {
			case MSG_LOADING_SHOW:
				act.showLoadingDialog();
				break;
				
			case MSG_LOADING_HIDE:
				act.hideLoadingDialog();
				break;
				
			case MSG_DATA_READY:
				act.onDataReady();
				break;
				
			default:
				super.handleMessage(msg);
			}
		}
		
	}
	
	private Context mAppContext = MainApplication.getApplication();    // prevent activity leak
	
	private MyHandler mHandler;
	private AppListHandler mAppListHandler;
	
	private ProgressDialog mLoadingDialog;
	private SearchViewHandler mSearchView;
	private ListViewHandler mListView;
	
	private AdvanceConfDialog mConfDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initView();
		init();
		
		mHandler.sendEmptyMessage(MyHandler.MSG_LOADING_SHOW);
		mAppListHandler.asycLoadAppList(new AppListHandler.ICallback() {

			@Override
			public void onLoad(List<AppModel> list) {
				mListView.setList(list);
                mListView.filter(mConfDialog.getConfig());

				mHandler.sendEmptyMessage(MyHandler.MSG_DATA_READY);
				mHandler.sendEmptyMessage(MyHandler.MSG_LOADING_HIDE);
			}
		});

		MainPreference mainPreference = MainPreference.getIns();
		if (!mainPreference.isShortcurCreated() &&
                !CommonUtil.MyApplication.hasShortcut(this, R.string.app_name)) {

			CommonUtil.MyApplication.addShortcut(
					this, R.string.app_name, R.mipmap.ic_launcher);
            mainPreference.setShortcutCreated(true);
		}
	}

	@Override
	protected void onResume() {
		mListView.onResume();
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		mConfDialog.dismiss();
		mConfDialog.hide();
		
		mListView.onStop();
		
		super.onStop();
	}
	
	private void init() {
		mHandler = new MyHandler(this);
		mAppListHandler = new AppListHandler(mAppContext);
		
		mSearchView.setOnQueryCallback(new SearchViewHandler.IQueryCallback() {
			
			@Override
			public void onKeyWordsChange(String newKeyWords) {
				mListView.filter(newKeyWords);
				
				mHandler.sendEmptyMessage(MyHandler.MSG_DATA_READY);
			}
		});
		
		mConfDialog = new AdvanceConfDialog(this);
		mConfDialog.setOnButtonClickedListener(new BaseAlertDialog.IOnButtonClicked() {
			
			@Override
			public void onPositiveClicked() {
				onAdvanceConfig();
			}
			
			@Override
			public void onNegativeClicked() {
				onAdvanceConfig();
			}
		});
	}
	
	private void initView() {
		mSearchView = new SearchViewHandler(
				(SearchView) findViewById(R.id.searchView));
		mListView = new ListViewHandler(this, 
				(ListView) findViewById(R.id.listView));
		
		TextView advanceView = (TextView) findViewById(R.id.advanceView);
		advanceView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mConfDialog.show();
			}
		});
	}
	
	private void showLoadingDialog() {
		if (mLoadingDialog == null) {
			mLoadingDialog = new ProgressDialog(this);
			mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					MainActivity.this.finish();
				}
			});
			mLoadingDialog.setCanceledOnTouchOutside(false);
			mLoadingDialog.setMessage(getString(R.string.loading_txt));
		}
		
		if (!mLoadingDialog.isShowing()) {
			mLoadingDialog.show();
		}
	}
	
	private void hideLoadingDialog() {
		if (mLoadingDialog.isShowing()) {
			mLoadingDialog.hide();
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}
	
	private void onDataReady() {
		mListView.notifyDataSetChanged();
	}
	
	private void onAdvanceConfig() {
		mListView.filter(mConfDialog.getConfig());
		mHandler.sendEmptyMessage(MyHandler.MSG_DATA_READY);
	}

}
