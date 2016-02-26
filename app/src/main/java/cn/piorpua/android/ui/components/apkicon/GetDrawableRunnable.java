package cn.piorpua.android.ui.components.apkicon;

import android.content.Context;
import android.os.Handler;

public interface GetDrawableRunnable extends Runnable {

	void setHandler(Handler handler);

	void setSouce(String source);

	void setContext(Context context);
	
}
