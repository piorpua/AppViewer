package cn.piorpua.appviewer;

import android.app.Application;

public class MainApplication extends Application {
	
	private static MainApplication sApp;

	public static MainApplication getApplication() {
		return sApp;
	}
	
	public MainApplication() {
		sApp = this;
	}
	
}
