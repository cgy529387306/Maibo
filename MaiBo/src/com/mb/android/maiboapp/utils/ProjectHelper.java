package com.mb.android.maiboapp.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ListView;

import com.google.gson.Gson;
import com.mb.android.maiboapp.R;
import com.mb.android.maiboapp.entity.UserEntity;
import com.mb.android.maiboapp.entity.WeiboEntity;
import com.tandy.android.fw2.utils.AppHelper;
import com.tandy.android.fw2.utils.Helper;
import com.tandy.android.fw2.utils.ToastHelper;

public class ProjectHelper {
	 /**
	 * 取得UserAgent1
	 * (网站)
	 * @return
	 */
	public static String getUserAgent1() {
		if (UserEntity.getInstance().born()) {
			return UserAgentHelper.getUserAgent1(0, UserEntity.getInstance().getMember_id());
		}
		return UserAgentHelper.getUserAgent1();
	}
	
	/*** 
     * 判断 String 是否是 int 
     *  
     * @param input 
     * @return 
     */  
    public static boolean isInteger(String input){  
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);  
        return mer.find();  
    }  
    
	  /**
	   * 将Map转化为Json
	   * 
	   * @param map
	   * @return String
	   */
	  public static <T> String mapToJson(Map<String, T> map) {
	   Gson gson = new Gson();
	   String jsonStr = gson.toJson(map);
	   return jsonStr;
	  }
	  
	/**
	 * 通用判断
	 * @param telNum
	 * @return
	 */
	public static boolean isMobiPhoneNum(String telNum){
		String regex = "^((13[0-9])|(15[0-9])|(18[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(telNum);
        return m.matches();
	}
	/**
	 * 用Intent打开url(即处理外部链接地址)
	 * 
	 * @param context
	 * @param url
	 */
	public static void openUrlWithIntent(Context context, String url) {
		if (Helper.isNull(context) || Helper.isEmpty(url)) {
			return;
		}
		Uri uri = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(uri);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解压assets的zip压缩文件到指定目录
	 * 
	 * @param context上下文对象
	 * @param assetName压缩文件名
	 * @param outputDirectory输出目录
	 * @param isReWrite是否覆盖
	 * @throws IOException
	 */
	public static void unZip(Context context, String assetName,
			String outputDirectory, boolean isReWrite) throws IOException {
		// 创建解压目标目录
		File file = new File(outputDirectory);
		// 如果目标目录不存在，则创建
		if (!file.exists()) {
			file.mkdirs();
		}
		// 打开压缩文件
		InputStream inputStream = context.getAssets().open(assetName);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		// 读取一个进入点
		ZipEntry zipEntry = zipInputStream.getNextEntry();
		// 使用1Mbuffer
		byte[] buffer = new byte[1024 * 1024];
		// 解压时字节计数
		int count = 0;
		// 如果进入点为空说明已经遍历完所有压缩包中文件和目录
		while (zipEntry != null) {
			// 如果是一个目录
			if (zipEntry.isDirectory()) {
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				// 文件需要覆盖或者是文件不存在
				if (isReWrite || !file.exists()) {
					file.mkdir();
				}
			} else {
				// 如果是文件
				file = new File(outputDirectory + File.separator
						+ zipEntry.getName());
				// 文件需要覆盖或者文件不存在，则解压文件
				if (isReWrite || !file.exists()) {
					file.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(
							file);
					while ((count = zipInputStream.read(buffer)) > 0) {
						fileOutputStream.write(buffer, 0, count);
					}
					fileOutputStream.close();
				}
			}
			// 定位到下一个文件入口
			zipEntry = zipInputStream.getNextEntry();
		}
		zipInputStream.close();
	}

	/**
	 * 将网络html保存到本地
	 * 
	 * @param url
	 *            html网络地址
	 * @param path
	 */
	public static void saveHtmlToSdcard(String url, String path) {
		try {
			URL myUrl = new URL(url);
			try {
				HttpURLConnection connection = (HttpURLConnection) myUrl
						.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5 * 1000);
				InputStream inputStream = connection.getInputStream();
				OutputStream outStream = new FileOutputStream(new File(path));
				byte[] buffer = new byte[1024];
				int len = -1;
				while ((len = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				outStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 改变父空间触摸事件拦截状态
	 * 
	 * @param parentView
	 * @param isDisallow
	 */
	public static void changeParentDisallowInterceptState(
			ViewParent parentView, boolean isDisallow) {
		if (Helper.isNull(parentView)) {
			return;
		}
		if (Helper.isNull(parentView.getParent())) {
			return;
		}
		// 改变触摸拦截状态
		parentView.requestDisallowInterceptTouchEvent(isDisallow);
		changeParentDisallowInterceptState(parentView.getParent(), isDisallow);
	}



	/**
	 * 保存图片到相册
	 * 
	 * @param context
	 *            上下文
	 * @param bmp
	 *            待保存图片
	 * @return 是否保存成功
	 * @see <a
	 *      href="http://changshizhe.blog.51cto.com/6250833/1295241">source</a>
	 */
	public static boolean saveImageToGallery(Context context, Bitmap bmp) {
		if (Helper.isNull(context)) {
			return false;
		}
		if (Helper.isNull(bmp)) {
			ToastHelper.showToast("图片保存失败！");
			return false;
		}
		String uriStr = MediaStore.Images.Media.insertImage(
				context.getContentResolver(), bmp, "", "");

		if (Helper.isEmpty(uriStr)) {
			ToastHelper.showToast("图片保存失败！");
			return false;
		}
		String picPath = getFilePathByContentResolver(context,
				Uri.parse(uriStr));
		if (Helper.isNotEmpty(picPath)) {
			ToastHelper.showToast("图片保存成功！");
		}
		return true;
	}

	/**
	 * 获取插入图片路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 * @see <a
	 *      href="http://blog.csdn.net/xiaanming/article/details/8990627">source</a>
	 */
	private static String getFilePathByContentResolver(Context context, Uri uri) {
		if (Helper.isNull(uri) || Helper.isNull(context)) {
			return null;
		}
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		String filePath = null;
		if (Helper.isNull(cursor)) {
			throw new IllegalArgumentException("Query on " + uri
					+ " returns null result.");
		}
		try {
			if ((cursor.getCount() != 1) || !cursor.moveToFirst()) {
			} else {
				filePath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaColumns.DATA));
			}
		} finally {
			cursor.close();
		}
		return filePath;
	}



	/**
	 * 浏览器打开指定Url
	 * 
	 * @param context
	 *            上下文
	 * @param url
	 *            链接地址
	 */
	public static void openUrlByBrowse(Context context, String url) {
		if (Helper.isEmpty(context) || Helper.isEmpty(url)) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}


	/**
	 * 隐藏View（透明度变化）
	 * 
	 * @param view
	 */
	public static void hideViewAlpha(final View view) {
		if (Helper.isNull(view)) {
			return;
		}
		AlphaAnimation anim = new AlphaAnimation(1, 0);
		anim.setDuration(800);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.INVISIBLE);
			}
		});
		view.startAnimation(anim);
	}

	public static void showViewAlpha(final View view) {
		if (Helper.isNull(view)) {
			return;
		}
		AlphaAnimation anim = new AlphaAnimation(0, 1);
		anim.setDuration(800);
		view.setVisibility(View.VISIBLE);
		view.startAnimation(anim);
	}

	/**
	 * 取共享数据
	 * 
	 * @param c
	 * @return
	 */
	public static SharedPreferences getDefaultShare(Context c) {
		return PreferenceManager.getDefaultSharedPreferences(c);
	}

	/**
	 * 判断一天内只离线一次
	 * 
	 * @return
	 */
	public static String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(new java.util.Date());
	}


	/**
	 * 判断是否安装目标应用
	 * 
	 * @param packageName
	 *            目标应用安装后的包名
	 * @return 是否已安装目标应用
	 */
	public static boolean isInstallByread(Context context, String packageName) {
		return new File("/data/data/" + packageName).exists();
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		List<String> pName = new ArrayList<String>();
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				pName.add(pn);
			}
		}
		return pName.contains(packageName);
	}

	/**
	 * 先判断是否安装，已安装则启动目标应用程序，否则先安装
	 * 
	 * @param packageName
	 *            目标应用安装后的包名
	 * @param appPath
	 *            目标应用apk安装文件所在的路径
	 */
	public static void launchApp(Context context, String packageName) {
		// 启动目标应用
		if (isInstallByread(context, packageName)) {
			// 获取目标应用安装包的Intent
			Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			context.startActivity(intent);
		}
		// 安装目标应用
//		else {
			// Intent intent = new Intent();
			// // 设置目标应用安装包路径
			// intent.setDataAndType(Uri.fromFile(new File(appPath)),
			// "application/vnd.android.package-archive");
			// context.startActivity(intent);
//			return false;
//		}
	}
	
	/**
	 * 判断文章是否是今天更新的
	 * @param date 文章更新时间
	 * @return
	 */
	public static boolean isTodayArticle(String date){
		boolean isTodayArticle = false;
		Date publishDate = Helper.string2Date(date, "yyyy-MM-dd");
		String todayDate = Helper.long2DateString(System.currentTimeMillis());
		Date today = Helper.string2Date(todayDate, "yyyy-MM-dd");
		isTodayArticle = today.equals(publishDate);
		return isTodayArticle;
	}
	
//	/**
//	 * 判断是否是今天第一次打开应用
//	 * @return
//	 */
//	public static boolean isToday(){
//		String date = PreferencesHelper.getInstance().getString(ProjectConstants.Preferences.KEY_UDATE);
//		if (Helper.isEmpty(date)) {
//			return true;
//		}else {
//			Date lastTime = Helper.string2Date(date, "yyyy-MM-dd");
//			String now = Helper.long2DateString(System.currentTimeMillis());
//			Date nowTime = Helper.string2Date(now, "yyyy-MM-dd");
//			return !nowTime.equals(lastTime);
//		}
//	}
	
	public static String getImagePath(Context context) {
		String imagePath = null;
		try {
			String cachePath = AppHelper.getBaseCachePath();
			imagePath = cachePath + "ic_launtcher.png";
			File file = new File(imagePath);
			if (!file.exists()) {
				file.createNewFile();
				Bitmap pic = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
				FileOutputStream fos = new FileOutputStream(file);
				pic.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			imagePath = null;
		}
		return imagePath;
	}
	
	
	/**
	 * 防止控件被连续点击
	 * @param view
	 */
	public static void disableViewDoubleClick(final View view) {
		if(Helper.isNull(view)) {
			return;
		}
		view.setEnabled(false);
		view.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				view.setEnabled(true);
			}
		}, 3000);
	}
	
	/**
	 * 获取ListView中某子项的视图View
	 * @param pos
	 * @param listView
	 * @return
	 */
	public static View getViewByPosition(int pos, ListView listView) {
		 int firstListItemPosition = listView.getFirstVisiblePosition();
		 int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
		if (pos < firstListItemPosition || pos > lastListItemPosition ) {
		    return listView.getAdapter().getView(pos, null, listView);
		} else {
		    int childIndex = pos - firstListItemPosition;
		    return listView.getChildAt(childIndex);
		}
	}
	
	/**
	 * 开始缩放动画
	 * @param view
	 */
	public static void startScaleAnimation(final View view){
		if (Helper.isNull(view)){
			return;
		}
		Animation myAnimation_Scale =new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,   
	             Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);   
		myAnimation_Scale.setDuration(300);
		myAnimation_Scale.setFillAfter(true);
		myAnimation_Scale.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
//					Animation myAnimation_Scale2 = new ScaleAnimation(1.2f, 1.0f, 1.2f, 1.0f,
//							Animation.RELATIVE_TO_SELF, 0.5f,
//							Animation.RELATIVE_TO_SELF, 0.5f);
//
//					myAnimation_Scale2.setDuration(300);
//					myAnimation_Scale2.setFillAfter(true);
//			        view.startAnimation(myAnimation_Scale2);
				}
				
			});
        view.startAnimation(myAnimation_Scale);  
	}
	
	// 在状态栏提示分享操作
	public static void showNotification(Context context,String text) {
		try {
			Context app = context.getApplicationContext();
			NotificationManager nm = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
			final int id = Integer.MAX_VALUE / 13 + 1;
			nm.cancel(id);
			long when = System.currentTimeMillis();
			Notification notification = new Notification(R.drawable.ic_launcher, text, when);
			PendingIntent pi = PendingIntent.getActivity(app, 0, new Intent(), 0);
			notification.setLatestEventInfo(app, text, text, pi);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			nm.notify(id, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**
	   * ImageGetter用于text图文混排
	   * 
	   * @return
	   */
	  public static ImageGetter getImageGetterInstance(final Context context) {
	    ImageGetter imgGetter = new Html.ImageGetter() {
	      @Override
	      public Drawable getDrawable(String source) {
	    	   Drawable drawable=null;
	    	   int rId=Integer.parseInt(source);
	    	   drawable = context.getResources().getDrawable(rId);
	    	   drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	    	   return drawable;
	      }
	    };
	    return imgGetter;
	  }
	  
	  public static String getSinceId(List<WeiboEntity> list){
		  String sinceId = "0";
		  if (Helper.isNotEmpty(list)) {
			 WeiboEntity entity = list.get(list.size()-1);
			 sinceId = entity.getId();
		  }
		  return sinceId;
	  }
	  
}
