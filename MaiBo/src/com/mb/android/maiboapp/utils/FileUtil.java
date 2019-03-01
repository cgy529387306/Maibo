package com.mb.android.maiboapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

public class FileUtil {
	public static final String SDCARD_NAME = "MaiboAPP";
	/**
	 * 获取存储空间路径 当SD卡存在时，存于sdcard/vhome;当SD卡不存在时，存于data/data/packName/cache
	 * 
	 * @param ctx
	 * @return
	 */
	private static File getStorageFileDir(Context ctx) {
		if (ctx == null) {
			return null;
		}
		File folder = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			folder = new File(Environment.getExternalStorageDirectory(),
					SDCARD_NAME);
		} else {
			folder = ctx.getCacheDir();
		}
		return folder;
	}

	/**
	 * 获取文件（路径：/sdcard/TravelAPP/name） 若sd卡存储不存在，存于data/data/packName/cache
	 * 创建文件必须创建中间目录
	 * 
	 * @param context
	 * @param name
	 * @param mkDir
	 *            是否创建中间目录
	 * @param flag
	 *            是否创建文件
	 * @return
	 * @throws IOException
	 */
	public static File getWebFile(Context context, String name, boolean mkDir,
			boolean flag) throws IOException {
		File file = null;
		File folder = getStorageFileDir(context);

		String dirPath = "";
		String fileName = name;
		if (null != name) {
			int index = name.lastIndexOf("/");
			if (index != -1) {
				dirPath = "/" + name.substring(0, index);
				fileName = name.substring(index, name.length());
			}
		}
		folder = new File(folder, "web" + dirPath);
		if (folder != null && !folder.exists() && (mkDir || flag)) {
			folder.mkdirs();
		}
		if (null == name) {
			return file;
		}
		file = new File(folder, fileName);
		if (flag) {
			if (!file.exists()) {
				file.createNewFile();
			}
		}
		return file;
	}

	/**
	 * 获取文件（路径：/sdcard/TravelAPP/name） 若sd卡存储不存在，存于data/data/packName/cache
	 * 创建文件必须创建中间目录
	 *
	 * @param context
	 * @param name
	 * @param mkDir
	 *            是否创建中间目录
	 * @param flag
	 *            是否创建文件
	 * @return
	 * @throws IOException
	 */
	public static File getShareImg(Context context, String name, boolean mkDir,
								  boolean flag) throws IOException {
		File file = null;
		File folder = getStorageFileDir(context);

		String dirPath = "";
		String fileName = name;
		if (null != name) {
			int index = name.lastIndexOf("/");
			if (index != -1) {
				dirPath = "/" + name.substring(0, index);
				fileName = name.substring(index, name.length());
			}
		}
		folder = new File(folder, "web" + dirPath);
		if (folder != null && !folder.exists() && (mkDir || flag)) {
			folder.mkdirs();
		}
		if (null == name) {
			return file;
		}
		file = new File(folder, fileName);
		if (flag) {
			if (!file.exists()) {
				file.createNewFile();
			}
		}
		return file;
	}


	/**
	 * 把流转换为byte[]
	 * 
	 * @param is
	 * @return
	 */
	public static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (IOException e) {
		}
		return os.toByteArray();
	}
}
