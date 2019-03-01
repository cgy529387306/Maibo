package com.mb.android.maiboapp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.content.Context;

import com.tandy.android.fw2.utils.AppHelper;

/**
 * 本应用数据清除帮助类
 *
 * @author cgy
 */
public class CacheHelper {

	/**
	 * 清除app缓存
	 */
	public static void cleanCache(Context context){
		context.deleteDatabase("webview.db");  
		context.deleteDatabase("webview.db-shm");  
		context.deleteDatabase("webview.db-wal");  
		context.deleteDatabase("webviewCache.db");  
		context.deleteDatabase("webviewCache.db-shm");  
		context.deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
//		clearCacheFolder(context.getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(context.getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(context.getExternalCacheDir(),System.currentTimeMillis());
		}
	}	
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	public static int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
    
    /**
     * 获取内外缓存的总大小
     *
     * @param context
     * @return cacheSizeString(B/KB/MB/GB...)
     */
    public static String getCacheSize(Context context) {
        long cacheSize = 0;
        if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
        	cacheSize = getDirSize(context.getFilesDir()) + getDirSize(context.getCacheDir()) + getDirSize(context.getExternalCacheDir());
		}else {
			cacheSize = getDirSize(context.getFilesDir()) + getDirSize(context.getCacheDir());
		}
        return getFriendlySize(cacheSize, true);
    }
    
    /**
     * 获取指定目录的大小
     *
     * @param dirFile
     * @return dirSize(long)
     */
    public static long getDirSize(File dirFile) {
        long dirSize = 0;
        if (dirFile != null && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirSize(file);
                }
            }
        }
        return dirSize;
    }

    /**
     * 将字节数转换为友好的字符
     * <p>这个方法实在是太牛X了</p>
     *
     * @param bytes 字节数
     * @param si true则返回KB格式(1000进制)，否则返回KiB格式(1024进制)
     * @return 友好的字符
     * @see <a href="http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java">来源</a>
     */
    public static String getFriendlySize(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
//        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    
    /**
     * 将List数据缓存到文件中
     * @param <T>
     * @param key
     * @param list
     */
    public static <T> void putCacheList(String key, List<T> list){
    	try {
			FileOutputStream fout= new FileOutputStream (AppHelper.getBaseFilePath().concat(key));
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(list);
			oos.flush();
			oos.close();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 获取缓存中的List数据
     * @param <T>
     * @param key
     * @return
     */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getCacheList(String key){
    	List<T> list = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(AppHelper.getBaseFilePath().concat(key));
			ObjectInputStream ois = new ObjectInputStream(fis);
			list = (List<T>) ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
    }
    
    /**
     * 将Object数据缓存到文件中
     * @param key
     * @param object
     */
    public static void putCacheObject(String key, Object object){
    	try {
			FileOutputStream fout= new FileOutputStream (AppHelper.getBaseFilePath().concat(key));
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(object);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 获取缓存中Object数据
     * @param key
     * @return
     */
   	public static Object getCacheObject(String key){
       	Object object = null;
   		FileInputStream fis;
   		try {
   			fis = new FileInputStream(AppHelper.getBaseFilePath().concat(key));
   			ObjectInputStream ois = new ObjectInputStream(fis);
   			object = ois.readObject();
   			ois.close();
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
   		return object;
       }
}
