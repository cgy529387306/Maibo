package com.mb.android.maiboapp;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.mb.android.maiboapp.broadcast.NetWorkStateChangeReceiver;
import com.mb.android.maiboapp.utils.LogHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.NetworkHelper;

/**
 * 基类
 * @author cgy
 *
 */
public class BaseActivity extends ActionBarActivity{
	

	
	public static final String BROADCAST_KEY_CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
	public static final String INTENTKEY_CLOSETAG = "INTENTKEY_CLOSETAG";
	
	// 网络状态
	protected boolean isNetworkAvailable = false;
	protected NetWorkStateChangeReceiver netWorkStateChangeReceiver;
	protected AlertDialog netDialog = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        isNetworkAvailable = NetworkHelper.isNetworkAvailable(this);
        registerNetWorkConnectivityChangeReceiver();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AppApplication.setRunningActivity(this);
		// 判断网络是否恢复
		if(isNetworkAvailable) {
			reResumeNet();
		}
	}
	
	/**
	 * 
	 * function: 网络变化接收
	 *
	 * 
	 * @ author:cgy 2014-12-26 上午10:12:37
	 */
	private void registerNetWorkConnectivityChangeReceiver() {

   		IntentFilter filter = new IntentFilter();
   		filter.addAction(BROADCAST_KEY_CONNECTIVITY_CHANGE);
   		filter.setPriority(1000);
   		
   		netWorkStateChangeReceiver = new NetWorkStateChangeReceiver() {
   			
   			@Override
   			public void onReceive(Context context, Intent intent) {
   				super.onReceive(context, intent);
   				
   				String action = intent.getAction();
   		        if(null != action && BROADCAST_KEY_CONNECTIVITY_CHANGE.equals(action)){
   		        	boolean temp = NetworkHelper.isNetworkAvailable(BaseActivity.this);
					if(temp != isNetworkAvailable) {
						isNetworkAvailable = temp;
						doNetWorkChange();
					}

   		            return;
   		        }
   			}
   		};
   		
   		registerReceiver(netWorkStateChangeReceiver, filter);
   }
	
	// 处理网络连接状态变化
	protected void doNetWorkChange() {
		// 没有网络
		if(!isNetworkAvailable) {
			if(AppApplication.isRunningActivity(this)) {
				if(!AppApplication.isShowNetworkDialog) {
					AppApplication.isShowNetworkDialog = true;
					
					// 显示网络弹窗
					netDialog = new Builder(this)
									.setTitle("提示")
									.setCancelable(false)
									.setMessage("当前网络连接中断！")
									.setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											
											try {
							                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
							                    field.setAccessible(true);
							                    field.set(dialog, false);
							                } catch (Exception e) {
							                    e.printStackTrace();
							                }
											
											Intent intent = null;
							                //判断手机系统的版本  即API大于10 就是3.0或以上版本 
							                if(android.os.Build.VERSION.SDK_INT>10){
							                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
							                }else{
							                    intent = new Intent();
							                    ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
							                    intent.setComponent(component);
							                    intent.setAction("android.intent.action.VIEW");
							                }
							                startActivity(intent);
										}
									})
									.setNegativeButton("取消", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									}).show();
											
				}
			}
			else {
				LogHelper.i("不是当前activity");
			}
		}
		// 有网络
		else {
			if(AppApplication.isRunningActivity(this)) {
				reResumeNet();
			}
		}
	}
	
	private void reResumeNet() {
		
		if(AppApplication.isShowNetworkDialog && Helper.isNotNull(netDialog)) {
			
			try {
				Field field = netDialog.getClass().getSuperclass().getDeclaredField("mShowing");
				field.setAccessible(true);
				field.set(netDialog, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			netDialog.dismiss();
			
			// 显示网络弹窗
			netDialog = new Builder(this)
							.setTitle("提示")
							.setCancelable(false)
							.setMessage("网络恢复了哦～～")
							.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									AppApplication.isShowNetworkDialog = false;
									netDialog.dismiss();
								}
							})
							.show();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != netWorkStateChangeReceiver){
			unregisterReceiver(netWorkStateChangeReceiver);
		}
	}
	
	
	/**
	 * 
	 * function: findView
	 *
	 * @param id
	 * @return
	 * 
	 * @ author:gy 2014年8月7日 下午2:53:01
	 */
	public <T extends View> T findView(int id) {
		return (T) findViewById(id);
	}


}
