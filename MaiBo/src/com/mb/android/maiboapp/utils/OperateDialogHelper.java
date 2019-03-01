package com.mb.android.maiboapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mb.android.maiboapp.R;
import com.tandy.android.fw2.utils.Helper;

/**
 * 
 * function: ProgressDialog助手类
 * 
 * @ author:cgy
 */
public class OperateDialogHelper {

private static Dialog sDialog = null;
	

	/**
	 * 显示加载对话框
	 * @param cotext 上下文
	 * @param textStr 显示文案
	 */
	public static void showLoadingDialog(final Context context, final String textStr,final OnClickListener clickListener) {
		if(Helper.isNull(context)) {
			return;
		}
		try {
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					sDialog = new Dialog(context, R.style.CustomProgressDialog);
					View contentView = LayoutInflater.from(context).inflate(R.layout.custom_operate_dialog, null);
					TextView txv_operate = (TextView) contentView.findViewById(R.id.txv_operate);
					if(Helper.isNotNull(txv_operate) && Helper.isNotEmpty(textStr)) {
						txv_operate.setText(textStr);
					}
					TextView txv_cancel = (TextView) contentView.findViewById(R.id.txv_cancel);
					txv_cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							sDialog.dismiss();
						}
					});
					txv_operate.setOnClickListener(clickListener);
					sDialog.setContentView(contentView);
					sDialog.setCancelable(false);
					sDialog.setCanceledOnTouchOutside(false);
					sDialog.show();
				}
			});
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
