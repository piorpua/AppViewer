package cn.piorpua.appviewer.ui;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import cn.piorpua.android.ui.components.apkicon.BaseAlertDialog;
import cn.piorpua.appviewer.MainPreference;
import cn.piorpua.appviewer.R;
import cn.piorpua.appviewer.apps.AppModelCmp.SortOrder;
import cn.piorpua.appviewer.apps.AppModelCmp.SortType;

public final class AdvanceConfDialog extends BaseAlertDialog {
	
	public static final class AdvanceConfig {
		
		private static final SortOrder DEFAULT_SORT_ORDER = SortOrder.ASC;
		private static final SortType DEFAULT_SORT_TYPE = SortType.DEFAULT;
		
		public SortOrder sortOrder;
		public SortType sortType;
		public boolean sysAppFilter;
		
		public AdvanceConfig() {
            load();
		}
		
		public void reset() {
			sortOrder = DEFAULT_SORT_ORDER;
			sortType = DEFAULT_SORT_TYPE;
			sysAppFilter = false;
		}

        public void load() {
            MainPreference mainPreference = MainPreference.getIns();
            try {
                sortOrder = SortOrder.valueOf(
                        mainPreference.getDialogConfigSortOrder());
            } catch (Exception e) {
                sortOrder = DEFAULT_SORT_ORDER;
            }
            try {
                sortType = SortType.valueOf(
                        mainPreference.getDialogConfigSortType());
            } catch (Exception e) {
                sortType = DEFAULT_SORT_TYPE;
            }
            sysAppFilter = mainPreference.isDialogConfigSystemFilter();
        }

        public void save() {
            MainPreference mainPreference = MainPreference.getIns();
            mainPreference.setDialogConfigSortOrder(sortOrder.toString());
            mainPreference.setDialogConfigSortType(sortType.toString());
            mainPreference.setDialogConfigSystemFilter(sysAppFilter);
        }
		
		@Override
		protected AdvanceConfig clone() {
			AdvanceConfig obj = new AdvanceConfDialog.AdvanceConfig();
			
			if (sortOrder == null) {
				sortOrder = DEFAULT_SORT_ORDER;
			}
			switch (sortOrder) {
			case ASC:
				obj.sortOrder = SortOrder.ASC;
				break;
				
			case DESC:
				obj.sortOrder = SortOrder.DESC;
				break;
			}
			
			if (sortType == null) {
				sortType = DEFAULT_SORT_TYPE;
			}
			switch (sortType) {
			case DEFAULT:
				obj.sortType = SortType.DEFAULT;
				break;
				
			case APPNAME:
				obj.sortType = SortType.APPNAME;
				break;
				
			case PKGNAME:
				obj.sortType = SortType.PKGNAME;
				break;
				
			case TIME:
				obj.sortType = SortType.TIME;
				break;
				
			case SIZE:
				obj.sortType = SortType.SIZE;
				break;
			}
			
			obj.sysAppFilter = sysAppFilter;
			
			return obj;
		}

	}
	
	private static final int[] SORT_TYPE_DESC = {
		R.string.advance_conf_dialog_sort_type_default, 
		R.string.advance_conf_dialog_sort_type_appname, 
		R.string.advance_conf_dialog_sort_type_pkgname, 
		R.string.advance_conf_dialog_sort_type_time, 
		R.string.advance_conf_dialog_sort_type_size, 
	};
	
	private static final int POS_DEFAULT = 0;
	private static final int POS_APPNAME = 1;
	private static final int POS_PKGNAME = 2;
	private static final int POS_TIME    = 3;
	private static final int POS_SIZE    = 4;
	
	final private AdvanceConfig mConfig;
	
	private Spinner mSortTypeSp;
	private RadioButton mSortOrderASCBtn;
	private RadioButton mSortOrderDESCBtn;
	private CheckBox mSysAppCb;

	public AdvanceConfDialog(Context context) {
		super(context);
		
		mConfig = new AdvanceConfig();
		
		initView();
	}
	
	public AdvanceConfig getConfig() {
		return mConfig.clone();
	}
	
	@Override
	public void show() {
		refreshByConfig();
		
		super.show();
	}

	@Override
	protected int getConvertViewID() {
		return R.layout.advance_conf_layout;
	}
	
	private void initView() {
		View convertView = getConvertView(null);
		if (convertView == null) {
			return ;
		}
		
		mSortTypeSp = (Spinner) convertView.findViewById(R.id.typeSp);
		mSortOrderASCBtn = (RadioButton) convertView.findViewById(R.id.ascRb);
		mSortOrderDESCBtn = (RadioButton) convertView.findViewById(R.id.descRb);
		mSysAppCb = (CheckBox) convertView.findViewById(R.id.filterCb);
		
		final String[] sortTypeDesc = new String[SORT_TYPE_DESC.length];
		for (int i = 0; i < SORT_TYPE_DESC.length; ++i) {
			sortTypeDesc[i] = mContext.getString(SORT_TYPE_DESC[i]);
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				mContext, android.R.layout.simple_spinner_item, sortTypeDesc);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mSortTypeSp.setAdapter(adapter);
		
		refreshByConfig();
		
		mSortTypeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(
					AdapterView<?> parent, View view, int position, long id) {
				
				SortType type = null;
				switch (position) {
				case POS_DEFAULT:
					type = SortType.DEFAULT;
					break;
					
				case POS_APPNAME:
					type = SortType.APPNAME;
					break;
					
				case POS_PKGNAME:
					type = SortType.PKGNAME;
					break;
					
				case POS_TIME:
					type = SortType.TIME;
					break;
					
				case POS_SIZE:
					type = SortType.SIZE;
					break;
				}
				
				if (type != null) {
					mConfig.sortType = type;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		RadioGroup orderRg = (RadioGroup) convertView.findViewById(R.id.orderRg);
		orderRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.ascRb:
					mConfig.sortOrder = SortOrder.ASC;
					break;
					
				case R.id.descRb:
					mConfig.sortOrder = SortOrder.DESC;
					break;
				}
			}
		});
		
		mSysAppCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mConfig.sysAppFilter = isChecked;
			}
		});
		
		Button leftBtn = (Button) convertView.findViewById(R.id.btnLeft);
		leftBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
                mConfig.save();
				dismiss();
				
				if (mBtnListener != null) {
					mBtnListener.onNegativeClicked();
				}
			}
		});
		
		Button rightBtn = (Button) convertView.findViewById(R.id.btnRight);
		rightBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mConfig.reset();
                mConfig.save();
				dismiss();
				
				if (mBtnListener != null) {
					mBtnListener.onPositiveClicked();
				}
			}
		});
		
		mBuilder.setView(convertView);
	}
	
	private void refreshByConfig() {
		initSortType();
		initSortOrder();
		initSysAppFilter();
	}
	
	private void initSortType() {
		int pos = -1;
		switch (mConfig.sortType) {
		case DEFAULT:
			pos = POS_DEFAULT;
			break;
			
		case APPNAME:
			pos = POS_APPNAME;
			break;
			
		case PKGNAME:
			pos = POS_PKGNAME;
			break;
			
		case TIME:
			pos = POS_TIME;
			break;
			
		case SIZE:
			pos = POS_SIZE;
			break;
		}
		
		if (pos != -1) {
			mSortTypeSp.setSelection(pos);
		}
	}
	
	private void initSortOrder() {
		switch (mConfig.sortOrder) {
		case ASC:
			mSortOrderASCBtn.setChecked(true);
			break;
			
		case DESC:
			mSortOrderDESCBtn.setChecked(true);
			break;
		}
	}
	
	private void initSysAppFilter() {
		mSysAppCb.setChecked(mConfig.sysAppFilter);
	}

}
