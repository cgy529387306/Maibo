package com.mb.android.maiboapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * function: 数据版本控制
 * 
 * @ author:linjunying 2011-12-23
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "MB_DataBase";
	
	public static final int VERSION = 1;
		
	private static SQLiteDatabase db;
	private static DataBaseOpenHelper dataBaseHelper;
	
	private Context context;
	
	public DataBaseOpenHelper(Context context) {
		
		super(context, DB_NAME, null, VERSION);
		this.context = context;
	}
	
	/**
	 * 
	 * function: closeCursor
	 *
	 * @param cursor
	 * 
	 * @ author:linjunying 2011-11-17 下午03:59:04
	 */
	public static void closeCursor(Cursor cursor) {
		
		if(null != cursor && !cursor.isClosed()) {
			
			cursor.close();
			cursor = null;
		}
	}
	
	public static synchronized SQLiteDatabase getInstance(Context context) {
		
		if(null == db) {
			
			dataBaseHelper =new DataBaseOpenHelper(context);
			db = dataBaseHelper.getWritableDatabase();
	        
		}else if(!db.isOpen()){
			
			dataBaseHelper =new DataBaseOpenHelper(context);
			db = dataBaseHelper.getWritableDatabase();
			
		} 
		
		return db;

	}
	
	/**
	 * 
	 * function: closeDataBase
	 *
	 * @param db
	 * 
	 * @ author:linjunying 2011-11-17 下午03:59:07
	 */
	public static void closeDataBase(SQLiteDatabase db) {
		
		if(null != db && db.isOpen()) {
			
			db.close();
			db = null;
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DataBaseOpenHelper.db = db;
        // 创建表
		UserHistoryDao userHistoryDao = new UserHistoryDao();
		userHistoryDao.createTable();
		GroupDao groupDao = new GroupDao();
		groupDao.createTable();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 各种版本间的变化处理
		onCreate(db);
	}

}
