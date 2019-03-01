package com.mb.android.maiboapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.ResourceHelper;

/**
 * 
 * function: ProgressDialog助手类
 * 
 * @ author:cgy
 */
public class ProgressLoadingHelper {

private static Dialog sDialog = null;
	
	/**
	 * 显示加载对话框
	 * @param context
	 */
	public static void showLoadingDialog(Context context) {
		showLoadingDialog(context, "正在加载...");
	}
	
	
	/**
	 * 显示加载对话框
	 * @param cotext 上下文
	 * @param textId 显示文案Id
	 */

	public static void showLoadingDialog(final Context context, final int textId) {
		if (0 != textId) {
			showLoadingDialog(context, ResourceHelper.getString(textId));
		}
	}


	/**
	 * 显示加载对话框
	 * @param cotext 上下文
	 * @param textStr 显示文案
	 */
	public static void showLoadingDialog(final Context context, final String textStr) {
		if(Helper.isNull(context)) {
			return;
		}
		try {
			if(context instanceof Activity) {
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(Helper.isNull(sDialog)) {
							sDialog = new Dialog(context, R.style.CustomProgressDialog);
							View contentView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);
							TextView txvHint = (TextView) contentView.findViewById(R.id.txv_loading_dialog_hint);
							if(Helper.isNotNull(txvHint) && Helper.isNotEmpty(textStr)) {
								txvHint.setText(textStr);
							}
							sDialog.setContentView(contentView);
							sDialog.setCancelable(false);
							sDialog.setCanceledOnTouchOutside(false);
						}
						
						sDialog.show();
					}
				});
			}else {
				if(Helper.isNull(sDialog)) {
					sDialog = new Dialog(context, R.style.CustomProgressDialog);
					View contentView = LayoutInflater.from(context).inflate(R.layout.custom_progress_dialog, null);
					TextView txvHint = (TextView) contentView.findViewById(R.id.txv_loading_dialog_hint);
					if(Helper.isNotNull(txvHint) && Helper.isNotEmpty(textStr)) {
						txvHint.setText(textStr);
					}
					sDialog.setContentView(contentView);
					sDialog.setCancelable(false);
					sDialog.setCanceledOnTouchOutside(false);
				}
				
				sDialog.show();
			}
		}catch (Exception e) {
			LogHelper.e(e.toString());
		}
	}
	
	/**
	 * 隐藏加载对话框
	 */
	public static void dismissLoadingDialog() {
		if(Helper.isNotNull(sDialog) && sDialog.isShowing()) {
			try {
				sDialog.dismiss();
				sDialog = null;
			}catch (IllegalArgumentException e) {
				LogHelper.e(e.toString());
			}finally {
				sDialog = null;
			}
		}
	}
	
}
