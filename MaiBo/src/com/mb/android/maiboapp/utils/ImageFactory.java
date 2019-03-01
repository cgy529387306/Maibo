package com.mb.android.maiboapp.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;

/**
 * 图片处理
 * 
 */
public class ImageFactory {

	/**
	 * 获取Bitmap
	 * 
	 * @param uri
	 *            图片地址
	 * @return
	 */
	public static Bitmap GetBitmapByUrl(String uri) {
		if (uri == null || uri.equals(""))
			return null;
		Bitmap bitmap;
		InputStream is;
		try {
			is = GetImageByUrl(uri);
			if (is == null)
				return null;
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取图片流
	 * 
	 * @param uri
	 *            图片地址
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	public static InputStream GetImageByUrl(String uri)
			throws MalformedURLException {
		if (uri == null || uri.equals(""))
			return null;
		URL url = new URL(uri);
		URLConnection conn;
		InputStream is;
		try {
			conn = url.openConnection();
			conn.connect();
			is = conn.getInputStream();
			return is;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 根据路径获得图片并压缩，返回bitmap用于缩略图
	public static Bitmap getSmallBitmap(String filePath) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, 256, 256);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitmapFactory.decodeFile(filePath, options).compress(
				CompressFormat.JPEG, 80, baos);
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		return BitmapFactory.decodeStream(isBm, null, null);
	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 根据一个网络连接(String)存储bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws MalformedURLException
	 */
	public static boolean getbitmap(String imageUri, File file) {
		// 显示网络上的图片
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			int a;
			while ((a = is.read()) != -1) {
				fos.write(a);
			}
			fos.flush();
			fos.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 把图片流转换为byte[]
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

	/**
	 * 图片转换为byte[]
	 * 
	 * @param bitmap
	 * @param format
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bitmap,
			CompressFormat format) {
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// 将Bitmap压缩，质量为100%存储h
		bitmap.compress(format, 100, os);// 除了PNG还有很多常见格式，如jpeg等。
		return os.toByteArray();
	}

	/**
	 * 把字符串转换为图像格式
	 */
	@SuppressLint("NewApi")
	public static CompressFormat getBitmapFormat(String imgType) {
		String upperImgType = imgType.toUpperCase();
		if (imgType.contains("PNG")) {
			return CompressFormat.PNG;
		} else if (imgType.contains("WEBP")) {
			return CompressFormat.WEBP;
		} else // if(imgType.contains("JPEG"))
		{
			return CompressFormat.JPEG;
		}
	}

	/**
	 * 图片去色
	 * 
	 * @param img
	 *            处理原图
	 * @param fileName
	 *            存储文件位置+名称 若为null则不存储
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayImage(byte[] img, String fileName) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
		return bitmap;
	}

	public static Bitmap toGrayImage(Context context, int res, String fileName) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				res);
		return toGrayImage(bitmap, fileName);
	}

	/**
	 * 图片去色
	 * 
	 * @param bitmap
	 *            处理原图
	 * @param fileName
	 *            存储文件位置+名称 若为null则不存储
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayImage(Bitmap bitmap, String fileName) {
		if (bitmap == null) {
			return null;
		}
		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Bitmap grayImg = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);

			// 图片去色处理
			Canvas canvas = new Canvas(grayImg);
			Paint paint = new Paint();
			ColorMatrix colorMatrix = new ColorMatrix();
			colorMatrix.setSaturation(0);
			ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
					colorMatrix);
			paint.setColorFilter(colorMatrixFilter);
			canvas.drawBitmap(bitmap, 0, 0, paint);

			if (fileName != null) {
				File file = new File(fileName);
				if (file.createNewFile()) {
					FileOutputStream stream = new FileOutputStream(file);
					grayImg.compress(CompressFormat.JPEG, 100, stream);
					stream.flush();
					stream.close();
				}
			}
			return grayImg;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 计算BitmapFactory.Options 的inSampleSize大小
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * 初始化samplesize
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 截图
	 * 
	 * @param bmp
	 *            原图
	 * @return 截取中间的正方形部分
	 */
	public static Bitmap screenshot(Bitmap bmp) {
		if (bmp == null)
			return null;
		int size;
		int px = 0;
		int py = 0;
		if (bmp.getHeight() > bmp.getWidth()) {
			size = bmp.getWidth();
			py = (bmp.getHeight() - size) / 2;
		} else {
			size = bmp.getHeight();
			px = (bmp.getWidth() - size) / 2;
		}
		try {
			Bitmap b = Bitmap.createBitmap(bmp, px, py, size, size);
			return b;
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return null;
	}

	/**
	 * 截图
	 * 
	 * @param bmp
	 * @param sw
	 *            组件宽度
	 * @param sh
	 *            组件高度
	 * @return 截取中间的部分
	 */
	public static Bitmap screenshot(Bitmap bmp, int sw, int sh) {
		if (bmp == null)
			return null;
		int wsize, hsize;
		int px = 0;
		int py = 0;
		double sProportion = Double.valueOf(sh) / Double.valueOf(sw);
		double bProportion = Double.valueOf(bmp.getHeight())
				/ Double.valueOf(bmp.getWidth());
		if (sProportion > bProportion) {
			hsize = bmp.getHeight();
			wsize = (int) (hsize / sProportion);
			px = (bmp.getWidth() - wsize) / 2;
		} else {
			wsize = bmp.getWidth();
			hsize = (int) (bmp.getWidth() * sProportion);
			py = (bmp.getHeight() - hsize) / 2;
		}
		try {
			Bitmap b = Bitmap.createBitmap(bmp, px, py, wsize, hsize);
			return b;
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return null;
	}

	/**
	 * 获取缩略图
	 * 
	 * @param path
	 * @param maxNumOfPixels
	 * @return
	 */
	public static Bitmap getImageThumbnail(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回 bm// 为空
		options.inJustDecodeBounds = false; // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = (int) (options.outHeight / (float) 320);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be; // 重新读入图片，注意此时已经把
									// options.inJustDecodeBounds 设回 false 了
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + " " + h); // after zoom
		return bitmap;
	}

	/**
	 * 获取缩放尺寸大小
	 * 
	 * @param relW
	 *            真实宽度
	 * @param relH
	 *            真实高度
	 * @param cvW
	 *            所放区域宽度
	 * @param cvH
	 *            所放区域高度
	 * @return int[0] 宽度 int[1] 高度
	 */
	public static int[] getViewWH(int relW, int relH, int cvW, int cvH) {
		int[] re = new int[2];
		double relProportion = Double.valueOf(relW) / Double.valueOf(relH);
		double cvProportion = Double.valueOf(cvW) / Double.valueOf(cvH);
		if (cvProportion > relProportion) {
			re[1] = cvH;
			re[0] = (int) (cvH * relProportion);
		} else {
			re[0] = cvW;
			re[1] = (int) (cvW / relProportion);
		}
		return re;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			System.out.println("angle2=" + angle);
			// 创建新的图片
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizedBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 将图片缩放到指定大小（实际缩放后会偏大）
	 * 
	 * @param maxSize
	 *            缩放后大小（Kb）
	 * @param bitMap
	 * @return
	 */
	public static Bitmap imageZoom(int maxSize, Bitmap bitMap) {
		// 图片允许最大空间 单位：KB
		// double maxSize = 30.00;
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitMap.compress(CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		// Logger.d(LOGTAG, "==== bitMap size : " + mid);
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		if (mid > maxSize) {
			// 获取bitmap大小 是允许最大大小的多少倍
			double i = mid / maxSize;
			// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
			// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
			bitMap = zoomImage(bitMap, bitMap.getWidth() / Math.sqrt(i),
					bitMap.getHeight() / Math.sqrt(i));
		}
		return bitMap;
	}

	/***
	 * 图片的缩放方法
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
			double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
				(int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 压缩图片到指定大小 (所得图片会偏小)
	 * 
	 * @param bitmap
	 *            原图片
	 * @param maxSize
	 *            指定大小
	 * @return
	 */
	public static Bitmap zoomImageToSize(Bitmap bitmap, int maxSize) {
		// 图片允许最大空间 单位：KB
		// 将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		// 将字节换成KB
		double mid = b.length / 1024;
		// 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
		while (mid > maxSize) {
			try {
				// 获取bitmap大小 是允许最大大小的多少倍
				double i = mid / maxSize;
				// 开始压缩 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
				// （1.保持刻度和高度和原bitmap比率一致，压缩后也达到了最大大小占用空间的大小）
				bitmap = zoomImage(bitmap, bitmap.getWidth() / Math.sqrt(i),
						bitmap.getHeight() / Math.sqrt(i));
				baos.close();
				baos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, baos);
				b = baos.toByteArray();
				mid = b.length / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

}
