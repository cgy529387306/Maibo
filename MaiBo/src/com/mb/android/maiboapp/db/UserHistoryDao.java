package com.mb.android.maiboapp.db;

import java.util.ArrayList;

import com.mb.android.maiboapp.entity.UserEntity;

import android.database.Cursor;

/**
 * 
 * function: user_history表
 *
 * @ author:cgy
 */
public class UserHistoryDao extends BaseDao {
	
	
	public static String TABLE_NAME = "user_history";

	/**
	 * 
	 * function: 创建表 DataBaseOpenHelper->onCreate 调用
	 *
	 *
	 * @ author:cgy
	 */
	public void createTable() {
		
		execSQL("CREATE TABLE user_history" +
				" ( " +
				" member_id VARCHAR(255)," +
				" user_name VARCHAR(255)," +
				" avatar_large VARCHAR(255)," +
				" password VARCHAR(255)" +
				" );"); 
	}
	
	/**
	 * 判断景点是否存在
	 * 
	 * @param id
	 * @return
	 */
	public boolean exist(String scene) {
		boolean isExist = false;
		int count = 0;
		StringBuffer sql = new StringBuffer();
		sql.append(" select count(*) from ").append(TABLE_NAME).append(" where member_id=? ");
		Cursor cursor = rawQuery(sql.toString(), new String[] { scene });
		if (cursor.moveToNext()) {
			count = cursor.getInt(0);
		}
		DataBaseOpenHelper.closeCursor(cursor);
		if (count > 0) {
			isExist = true;
		}
		return isExist;
	}
	
	public void updatePwd(String pwd){
		String user_name = UserEntity.getInstance().getUser_name();
		String member_id = UserEntity.getInstance().getMember_id();
		if (exist(user_name)) {
			String sql = " update " + TABLE_NAME + " set password = " + pwd + " where member_id =" +member_id;
			execSQL(sql);
		}
	}
	
	/**
	 * @param scene
	 */
	public void add(String member_id,String user_name,String avatar_large,String password){
		if (exist(member_id)) {
			String sql = " delete from " + TABLE_NAME + " where member_id = "+member_id;
			execSQL(sql);
		}
		String sql = " insert into " + TABLE_NAME + " values(?, ?, ?, ?)";
		execSQL(sql, new String[] {member_id ,user_name,avatar_large,password});
	}
	
	/**
	 * 添加景点搜索历史
	 * @param scene
	 */
	public void delete(String member_id){
		if (exist(member_id)) {
			String sql = " delete from " + TABLE_NAME + " where member_id = "+ member_id;
			execSQL(sql);
		}
	}
	
	/**
	 * 清除景点搜索历史
	 */
	public void clear() {
		String sql = " delete from " + TABLE_NAME;
		execSQL(sql);
	}
	
	/**
	 * 获取账号登录历史
	 * @return
	 */
	public ArrayList<UserHistoryEntity> getList() {
		ArrayList<UserHistoryEntity> list = new ArrayList<UserHistoryEntity>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from ").append(TABLE_NAME);
		Cursor cursor = rawQuery(sql.toString(), new String[] {});
		while(cursor.moveToNext()) {
			UserHistoryEntity po = new UserHistoryEntity();
			po.setMember_id(cursor.getString(cursor.getColumnIndex("member_id")));
			po.setUser_name(cursor.getString(cursor.getColumnIndex("user_name")));
			po.setAvatar_large(cursor.getString(cursor.getColumnIndex("avatar_large")));
			po.setPassword(cursor.getString(cursor.getColumnIndex("password")));
			list.add(po);
		}
		DataBaseOpenHelper.closeCursor(cursor);
		return list;
	}
	
}
