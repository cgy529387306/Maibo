package com.tandy.android.fw2.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkHelper {
	
	private static final String TAG = NetworkHelper.class.getSimpleName();
	/**
	 * KEY:网络传输用,user-agent1
	 */
	public static final String NETWORK_KEY_USER_AGENT1 = "User-Agent1";

	public static final short TYPE_IP_V4 = 4;
	public static final short TYPE_IP_V6 = 6;
	
	/**
	 * 检查网络连接是否可用
	 * 
	 * @param context
	 *            上下文
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo[] netinfo = cm.getAllNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		for (int i = 0; i < netinfo.length; i++) {
			if (netinfo[i].isConnected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前ip
	 * @param ipType ipv4或者ipv6，请使用{@link #TYPE_IP_V4}或者{@link #TYPE_IP_V6}
	 * @return 当前ip
	 */
	public static String getLocalIpAddress(short ipType) {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
					en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
						enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						switch (ipType) {
						case TYPE_IP_V4:
							if (inetAddress instanceof Inet4Address) {
								return inetAddress.getHostAddress().toString();
							}
							break;
						case TYPE_IP_V6:
							if (inetAddress instanceof Inet6Address) {
								return inetAddress.getHostAddress().toString();
							}
							break;

						default:
							break;
						}
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
}
