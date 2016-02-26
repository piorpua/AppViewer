package cn.piorpua.appviewer.ui;

import android.widget.SearchView;

public final class SearchViewHandler 
         implements SearchView.OnQueryTextListener {
	
	public static interface IQueryCallback {
		
		void onKeyWordsChange(String newKeyWords);
		
	}
	
	private SearchView mView;
	private IQueryCallback mCallback;
	
	public SearchViewHandler(SearchView view) {
		if (view == null) {
			throw new IllegalArgumentException("Error: SearchView can't be null.");
		}
		
		mView = view;
		mView.setOnQueryTextListener(this);
	}
	
	public void setOnQueryCallback(IQueryCallback callback) {
		mCallback = callback;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (mCallback != null) {
			mCallback.onKeyWordsChange(newText);
		}
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (mCallback != null) {
			mCallback.onKeyWordsChange(query);
		}
		return false;
	}
	
}
