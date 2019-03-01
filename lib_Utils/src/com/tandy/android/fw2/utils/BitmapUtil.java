package com.tandy.android.fw2.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.tandy.android.fw2.utils.Helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

/**
 * 图片工具类
 * @author chengfu.bao
 *
 */
public class BitmapUtil {

	/**
	 * Byte转BMP
	 * @param bytes
	 * @param opts
	 * @return
	 */
	public static Bitmap bytes2Bitmap(byte[] bytes, BitmapFactory.Options opts) {
		if (bytes != null){
			if (opts != null){
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			}else{
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}
		}
		return null;
	}

	/**
	 * BMP转Byte
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		if (bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 80, baos);
			return baos.toByteArray();
		} else {
			return null;
		}
	}
	
	/**
	 * 图片缩放
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		Bitmap newbmp = null;
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / w);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newbmp;
	}
	/**
	 * 图片缩放
	 * @param bitmap
	 * @param width
	 * @param canZoomIn 是否允许放大
	 * @return
	 */
	public static Bitmap zoomBitmapWithWidth(Bitmap bitmap, int width, boolean canZoomIn){
		Bitmap result = null;
		try{
			int w = bitmap.getWidth();
			if (!canZoomIn && w < width){
				result = bitmap;
			}else{
				Matrix matrix = new Matrix();
				float scale = (float)width / w;
				matrix.postScale(scale, scale);
				result = Bitmap.createBitmap(bitmap, 0, 0, w, bitmap.getHeight(), matrix, true);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 图片缩放
	 * @param bitmap
	 * @param maxsize
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int maxsize) {
		Bitmap newbmp = null;
		try {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = 1;
			float scaleHeight = 1;
			if (w > maxsize || h > maxsize) {
				if (w > h) {
					scaleWidth = ((float) maxsize / w);
					scaleHeight = scaleWidth;
				} else {
					scaleHeight = ((float) maxsize / h);
					scaleWidth = scaleHeight;
				}
			}else {
				return bitmap;
			}
			matrix.postScale(scaleWidth, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!bitmap.isRecycled()){ 
			//如果没有回收  
			bitmap.recycle(); 
		}
		return newbmp;
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	/**
	 * 缓存图片文件到SD卡(PNG格式)
	 * @param bitmap
	 * @param filename
	 * @return 保存成功与否
	 */
	public static boolean saveBitmap(Bitmap bitmap, String filename) {
		return saveBitmap(bitmap, filename, Bitmap.CompressFormat.PNG);
	}
	
	/**
	 * 缓存图片文件到SD卡
	 * @param bitmap
	 * @param filename
	 * @param expandedName 扩展名，参见Bitmap.CompressFormat
	 * @return 保存成功与否
	 */
	public static boolean saveBitmap(Bitmap bitmap, String filename, Bitmap.CompressFormat format) {
		if (bitmap != null) {
			File file = new File(filename);
			file.getParentFile().mkdirs();
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(filename);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			bitmap.compress(format, 80, fileOut);
			try {
				fileOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileOut.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
     * 图片去色,返回灰度图片
     * @param bmpOriginal 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();    

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    
    /**
     * 去色同时加圆角
     * @param bmpOriginal 原图
     * @param pixels 圆角弧度
     * @return 修改后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
        return toRoundCorner(toGrayscale(bmpOriginal), pixels);
    }
    /**
     * 去色同时加圆角
     * @param bmpOriginal 原图
     * @param pixels 圆角弧度
     * @return 修改后的图片
     */
    /**
     *  去色同时加圆角
     * @param bmpOriginal 原图
     * @param pixels 圆角弧度
     * @param width 宽度
     * @param height 高度
     * @return 修改后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels, int width, int height) {
    	return toRoundCorner(toGrayscale(bmpOriginal), pixels, width, height);
    }
    
    /**
     * 把图片变成圆角
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    /**
     * 
     * @param bitmap 
     * @param pixels 
     * @return 
     */
    /**
     * 把图片变成圆角
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @param width 输出的图片宽度
     * @param height 输出的图片的高度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels, int width, int height) {
    	
    	Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    	Canvas canvas = new Canvas(output);
    	
    	int color = 0xff424242;
    	Paint paint = new Paint();
    	Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    	Rect rect2 = new Rect(0, 0, width, height);
    	RectF rectF = new RectF(rect2);
    	float roundPx = pixels;
    	
    	paint.setAntiAlias(true);
    	canvas.drawARGB(0, 0, 0, 0);
    	paint.setColor(color);
    	canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
    	
    	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
    	canvas.drawBitmap(bitmap, rect, rect2, paint);
    	
    	return output;
    }
    
    /**
     * 使圆角功能支持BitampDrawable
     * @param bitmapDrawable 
     * @param pixels 
     * @return
     */
    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels) {
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bitmapDrawable = new BitmapDrawable(Resources.getSystem(), toRoundCorner(bitmap, pixels));
        return bitmapDrawable;
    }
    /**
     * 使圆角功能支持BitampDrawable
     * @param bitmapDrawable 
     * @param pixels 
     * @return
     */
    public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable, int pixels, int width, int height) {
    	Bitmap bitmap = bitmapDrawable.getBitmap();
    	bitmapDrawable = new BitmapDrawable(Resources.getSystem(), toRoundCorner(bitmap, pixels, width, height));
    	return bitmapDrawable;
    }
    /**
     * 回收bitmap(在调用后若返回的是true,建议将此bitmap设置为null)
     * @param bitmap
     * @return
     */
    public static boolean recycleBitmap(Bitmap bitmap){
    	if (Helper.isNotNull(bitmap)){
    		if (!bitmap.isRecycled()){
    			bitmap.recycle();
    		}
    		//System.gc();
    		return true;
    	}
    	return false;
    }
}
