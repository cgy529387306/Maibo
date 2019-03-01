package com.mb.android.maiboapp.activity;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider.ExtendProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.mb.android.maiboapp.AppApplication;
import com.mb.android.maiboapp.ContentFragment;
import com.mb.android.maiboapp.MenuFragment;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.constants.ProjectConstants;
import com.mb.android.maiboapp.entity.UserEntity;
import com.tandy.android.fw2.utils.PreferencesHelper;
import com.tandy.android.fw2.utils.ToastHelper;

/**
 * 首页
 * 
 * @author cgy
 * 
 */
public class MainActivity extends SlidingFragmentActivity {

	private Fragment mContent;
	private Fragment mMenuFragment;
	String Token = "";
	private SlidingMenu sm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);

		if (UserEntity.getInstance().born()) {
			Token = PreferencesHelper.getInstance().getString(
					ProjectConstants.Preferences.KEY_CURRENT_TOKEN_RONGCLOUD);
		} else {
			// 未登录
			Token = "B11oteMQTc1x4XfC1GEJokS++/qBdLb/OgcO7pr88MQpU9N/j3lvittw4jQwwwb+YTxP7bhB2Stg27IG/05eFQ==";
		}
		connect(Token);
		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {
			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
			mMenuFragment = getSupportFragmentManager().getFragment(
					savedInstanceState, "mMenu");
		}

		if (mContent == null) {
			mContent = new ContentFragment();
		}
		if (mMenuFragment == null) {
			mMenuFragment = new MenuFragment();
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, mMenuFragment).commit();

		// customize the SlidingMenu
		sm = getSlidingMenu();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeEnabled(false);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		sm.setBehindCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, -canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}
		});

		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.25);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
		initThemeStyle();
	}
	
	public void initThemeStyle(){
		boolean isNight = PreferencesHelper.getInstance().getBoolean(ProjectConstants.Preferences.KEY_IS_NIGHT,true);
		if (isNight) {
			sm.setBackgroundImage(R.drawable.bg_menu_night);
		}else {
			sm.setBackgroundImage(R.drawable.bg_menu_day);
		}
	}

	/**
	 * 建立与融云服务器的连接
	 * 
	 * @param token
	 */
	private void connect(String token) {

		if (getApplicationInfo().packageName.equals(AppApplication
				.getCurProcessName(getApplicationContext()))) {

			/**
			 * IMKit SDK调用第二步,建立与服务器的连接
			 */
			RongIM.connect(token, new RongIMClient.ConnectCallback() {

				/**
				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的
				 * Token
				 */
				@Override
				public void onTokenIncorrect() {

					Log.e("LoginActivity", "--onTokenIncorrect：" + Token);
				}

				/**
				 * 连接融云成功
				 * 
				 * @param userid
				 *            当前 token
				 */
				@Override
				public void onSuccess(String userid) {
					Log.e("LoginActivity", "--onSuccess" + userid);
					// 扩展功能自定义
					ExtendProvider[] provider = {
							new ImageInputProvider(RongContext.getInstance()),// 图片
							new CameraInputProvider(RongContext.getInstance()) // 相机
					};
					RongIM.getInstance().resetInputExtensionProvider(
							Conversation.ConversationType.PRIVATE, provider);
				}

				/**
				 * 连接融云失败
				 * 
				 * @param errorCode
				 *            错误码，可到官网 查看错误码对应的注释
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {

					Log.e("LoginActivity", "--onError" + errorCode);
				}
			});
		}
	}
	/**
	 * 保存Fragment的状态
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	// region 双击返回
	private static final long DOUBLE_CLICK_INTERVAL = 2000;
	private long mLastClickTimeMills = 0;

	@Override
	public void onBackPressed() {
		if (System.currentTimeMillis() - mLastClickTimeMills > DOUBLE_CLICK_INTERVAL) {
			ToastHelper.showToast("再按一次返回退出");
			mLastClickTimeMills = System.currentTimeMillis();
			return;
		}
		finish();
	}
	// endregion 双击返回

}
