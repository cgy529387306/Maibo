package com.mb.android.maiboapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tandy.android.fw2.utils.Helper;

/**
 * 
 * function: 
 *
 * @ author:linjunying
 */
public class NavigationHelper {
	
	public static final void startActivity(Activity act, Class<?> toActivity, Bundle bundle, boolean finish) {
		Intent intent = new Intent(act, toActivity);
		if(Helper.isNotNull(bundle)) {
			intent.putExtras(bundle);
		}
		
		act.startActivity(intent);
		if (finish){
			act.finish();
		}
	}
	
	
	public static final void startActivityForResult(Activity act, Class<?> toActivity, Bundle bundle, int requestCode) {
		Intent intent = new Intent(act, toActivity);
		if(Helper.isNotNull(bundle)) {
			intent.putExtras(bundle);
		}
		
		act.startActivityForResult(intent, requestCode);
	}
	
	
	public static final void finish(Activity act, int resultCode, Intent intent){
		
		if(Helper.isNull(intent)) {
			act.setResult(resultCode);
		}
		else {
			act.setResult(resultCode, intent);
		}
		
		act.finish();
	}

}
