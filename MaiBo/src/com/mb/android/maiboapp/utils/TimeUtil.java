package com.mb.android.maiboapp.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public static String getTimeStr(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = format.parse(dateStr);
			Date today = new Date();
			long interval = today.getTime() - date.getTime();
			if (isThisYear(date)) {
				if (isYesterday(date)) {
					SimpleDateFormat sdf = new SimpleDateFormat("昨天 HH:mm");
		            return sdf.format(date);
				}else if (isToday(date)) {
					if(interval<60*1000){
						return "刚刚";
					}else if(interval<60*60*1000){
						return (interval/(60*1000))+"分钟前";
					}else{
						return (interval/(60*60*1000))+"小时前";
					}
				}else {
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		            return sdf.format(date);
				}
			}else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	            return sdf.format(date);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return dateStr;
		}
	}

	private static boolean isThisYear(Date createDate){
		boolean isThisYear = false;
		Calendar currentCal = Calendar.getInstance();
		Calendar createCal = Calendar.getInstance();
		createCal.setTime(createDate);
		isThisYear = createCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR);
		return isThisYear;
	}
	
	private static boolean isToday(Date createDate){
		boolean isToday = false;
		Calendar currentCal = Calendar.getInstance();
		Calendar createCal = Calendar.getInstance();
		createCal.setTime(createDate);
		isToday = createCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) 
				&& createCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH) 
				&& createCal.get(Calendar.DAY_OF_MONTH) == currentCal.get(Calendar.DAY_OF_MONTH);
		return isToday;
	}
	
    
    private static boolean isYesterday(Date createDate){
    	boolean isYesterday = false;
    	Calendar currentCal = Calendar.getInstance();
		Calendar createCal = Calendar.getInstance();
		createCal.setTime(createDate);
		isYesterday = createCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) 
				&& createCal.get(Calendar.MONTH) == currentCal.get(Calendar.MONTH) 
				&& createCal.get(Calendar.DAY_OF_MONTH) == currentCal.get(Calendar.DAY_OF_MONTH)-1;
		return isYesterday;
    }
}
