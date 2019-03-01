package com.mb.android.maiboapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.tandy.android.fw2.utils.BitmapUtil;
import com.tandy.android.fw2.utils.Helper;

/**
 * Web图片帮助者
 * @author zlh
 *
 */
public class ImageDownloadHelper {
	private static Context mContext;
	
	private static final String TAG = ImageDownloadHelper.class.getSimpleName();
	
	private static ImageDownloadHelper sInstance;
	
	public static ImageDownloadHelper getInstance() {
		if(Helper.isNull(sInstance)) {
			sInstance = new ImageDownloadHelper();
		}
		return sInstance;
	}
	
	/**
	 * 根据地址下载图片
	 * @param url 图片地址
	 * @param callBack 回调
	 * @param extras 附加参数（原样返回）
	 */
	public static void startDownLoadImg(Context context, String url, DownLoadCallBack callBack, Object... extras) {
		mContext = context;
		getInstance().new DownLoadTask(url, callBack, extras).execute();
	}
	
	
	/**
	 * 获取图片保存地址
	 * @param url
	 * @return
	 */
	public static String getImageCachePath(String url) {
		return Environment.getExternalStorageDirectory()+"/com.mb.android.maiboapp/download/" + getCacheKey(url);
	}
	
	/**
	 * 获取缓存名
	 * @param url
	 * @return
	 */
	private static String getCacheKey(String url) {
        if(url == null){
            throw new RuntimeException("Null url passed in");
        } else {
            return url.replaceAll("[.:/,%?&=]", ".").replaceAll("[.]+", ".");
        }
    }

	/**
	 * 下载任务
	 * @author zlh
	 *
	 */
	class DownLoadTask extends AsyncTask<Void, Void, Boolean>  {
		
		private String mUrl;
		
		private DownLoadCallBack mCallBack;
		
		private Object[] mExtras;
		
		public DownLoadTask(String url, DownLoadCallBack callBack, Object... extras) {
			this.mUrl = url;
			this.mCallBack = callBack;
			this.mExtras = extras;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if(Helper.isEmpty(mUrl)) {
				return false;
			}
			String filePath = getImageCachePath(mUrl);
			File file = new File(filePath);
			// 若图片不存在，进行下载
			if(!file.exists()) {
				try {
					// 下载图片流
					URL url = new URL(mUrl);
					URLConnection connection = url.openConnection();
					InputStream is = connection.getInputStream();
					// 读取图片流
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						os.write(buffer, 0, len);
                        if (Helper.isNotNull(mCallBack)) {
                            mCallBack.onLoading(len);
                        }
                    }
                    os.close();
					is.close();
					// 保存到文件
					Bitmap bmp = BitmapUtil.bytes2Bitmap(os.toByteArray(), null);
					BitmapUtil.saveBitmap(bmp, filePath, Bitmap.CompressFormat.JPEG);
					String fileName = System.currentTimeMillis() + ".jpg";
					try {
				        MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
								file.getAbsolutePath(), fileName, null);
				    } catch (FileNotFoundException e) {
				        e.printStackTrace();
				    }
				    // 最后通知图库更新
					mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
					Log.d(TAG, filePath);
				} catch (MalformedURLException e) {
					Log.d(TAG, "MalformedURLException Error");
					return false;
				} catch (IOException e) {
					Log.d(TAG, "IOException Error");
					return false;
				}
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(Helper.isNull(mCallBack)) {
				return;
			}
			// 通知外部，进行回调
			if(result) {
				// 下载成功
				mCallBack.onSuccess(mUrl, mExtras);
			}else {
				// 下载失败
				mCallBack.onFail(mUrl, mExtras);
			}
		}
	}
	
	/**
	 * 下载回调
	 * @author zlh
	 *
	 */
	public interface DownLoadCallBack {
		/**
		 * 下载成功
		 * @param url 图片地址
		 * @param extras 附加参数
		 */
		public void onSuccess(String url, Object... extras);

        /**
         * 下载中
         * @param current 当前下载字节数
         */
        public void onLoading(long current, Object... extras);
		/**
		 * 下载失败
		 * @param url 图片地址
		 * @param extras 附加参数
		 */
		public void onFail(String url, Object... extras);
	}
}