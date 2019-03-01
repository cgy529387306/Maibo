package com.mb.android.maiboapp.utils;

import android.media.MediaPlayer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mb.android.maiboapp.AppApplication;
import com.mb.android.maiboapp.R;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.ResourceHelper;
/**
 * 应用消息提醒帮助者
 * @author cgy
 *
 */
public class AppMsgHelper {
	private MediaPlayer mPlayer;
	private boolean isSound;

	/**
	 * 显示头部刷新提醒
	 * @param txvMsg 提醒展示View
	 * @param msg 提醒消息
	 */
	public static void showAppMsg(TextView txvMsg, String msg) {
		if(Helper.isNull(txvMsg)) {
			return;
		}
		txvMsg.setText(msg);
		if(txvMsg.isShown()) {
			return;
		}
		txvMsg.setVisibility(View.VISIBLE);
		Animation anim = AnimationUtils.loadAnimation(AppApplication.getInstance(), R.anim.slide_in_from_top);
		txvMsg.startAnimation(anim);
		anim.start();
	}
	
	/**
	 * 显示头部刷新更新数
	 * @param txvMsg 消息展示View
	 * @param updateCount 更新数
	 */
	public static void showUpDateMsg(final TextView txvMsg, int updateCount) {
		if(Helper.isNull(txvMsg)) {
			return;
		}
		AppMsgHelper.showAppMsg(txvMsg, String.format(ResourceHelper.getString(R.string.model_new_data_count), updateCount));
		txvMsg.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				hideAppMsg(txvMsg);
			}
		}, 3000);
	}
	
	/**
	 * 隐藏头部提醒
	 * @param txvMsg
	 */
	public static void hideAppMsg(final TextView txvMsg) {
		if(Helper.isNull(txvMsg)) {
			return;
		}
		if(!txvMsg.isShown()) {
			return;
		}
		Animation anim = AnimationUtils.loadAnimation(AppApplication.getInstance(), R.anim.slide_out_to_top);
		txvMsg.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				txvMsg.setVisibility(View.GONE);
				txvMsg.setTag(1);
			}
		});
		anim.start();
	}
} 